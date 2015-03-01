package tommista.com.harmony;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;
import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.models.Track;
import tommista.com.harmony.soundcloud.SoundcloudTrack;
import tommista.com.harmony.spotify.SpotifyAuthenticator;
import tommista.com.harmony.spotify.models.SpotifyTrack;
import tommista.com.harmony.spotify.webapi.SpotifyApi;
import tommista.com.harmony.spotify.webapi.SpotifyService;

/**
 * Created by Jacob on 2/28/15.
 */
public class AddSongActivity extends Activity {

    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private String authToken;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_song_layout);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO eventually put remote logging into a tree and put here.
        }

        authToken = getAuthToken();
        if(authToken != null) {
            parseIntent();
        }
        else {
            SpotifyAuthenticator.authenticate(this, SPOTIFY_REQUEST_CODE);
        }
    }

    private void parseIntent() {
        Intent intent = getIntent();

        if(intent != null && intent.getExtras() != null) {

            Bundle bundle = intent.getExtras(); //SMS data bundle

            if(bundle.containsKey("sms_body")) {

                String bundleData = bundle.getString("sms_body");  //String containing URL

                if(bundleData.contains("spotify")) {

                    //Parse track ID from URL then launch web API if track ID exists
                    String spotifyTrackID = getSpotifyID(bundleData);
                    if(spotifyTrackID != null) {
                        getSpotifyTrack(spotifyTrackID);
                    }
                    else {
                        Toast.makeText(this, "Error Getting Song Data", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                if(bundleData.contains("soundcloud")) {
                   Timber.i("@@@@" + bundle.getString("sms_body"));
                }
                else {
                    //Error
                }
            } else{
                if(bundle.containsKey("android.intent.extra.TEXT")){
                    String bundleData = bundle.getString("android.intent.extra.TEXT");
                    Timber.i("$$$$ " + bundleData);
                    parseSoundcloudIntent(bundleData);
                }else{
                    //error
                }
            }
        }
        else {
            Timber.i("Bundle Empty");
        }
    }

    public void parseSoundcloudIntent(String data){
        String clientId = "55de8cc1d6246dd72e0a78b1c70fd91a";
        String secret = "ab4c14572d6c83b5ec1e333ce9de47c5";
        String callbackUrl = "harmony://soundcloud/callback";

        String url = data.substring(data.indexOf("http"),data.length());

        Log.i("substring",url);

        URL trackUrl = null;
        try {
            String apiUrl = "http://api.soundcloud.com/resolve.json?url=" + url + "&client_id=55de8cc1d6246dd72e0a78b1c70fd91a";
            trackUrl = new URL(apiUrl);
            new DownloadResolveMetaData().execute(trackUrl);
        } catch (MalformedURLException e) {
            Log.e("onCreate", "Could not start resolvemetadata background task");
            e.printStackTrace();
        }
    }

    //For use in parseIntent()
    private String getSpotifyID(String bundleData) {
        Pattern spotifyPattern = Pattern.compile("(?<=track/).*"); //See if spotify URL contains "track"
        Matcher matcher = spotifyPattern.matcher(bundleData);

        if(matcher.find()) {
            Timber.i(" " + matcher.group());

            return matcher.group();
        }
        else {
            Toast.makeText(this, "Error Finding Song", Toast.LENGTH_SHORT).show();

            return null;
        }
    }

    private String getAuthToken() {
        //get authToken from shared prefs
        String prefsName = getResources().getString(R.string.shared_prefs_name);
        SharedPreferences preferences = getSharedPreferences(prefsName, Context.MODE_PRIVATE);

        String authKey = getResources().getString(R.string.spotify_token_key);
        final String defValue = getResources().getString(R.string.shared_prefs_def_string);

        return preferences.getString(authKey, defValue);
    }

    //For use in parseIntent() when getSpotifyID() returns a track ID
    private void getSpotifyTrack(String trackID) {

        //connect to spotify to retrieve track data

        SpotifyApi wrapper = new SpotifyApi();

        wrapper.setAccessToken(authToken);

        SpotifyService spotify = wrapper.getService();

        spotify.getTrack(trackID, new Callback<SpotifyTrack>() {
            @Override
            public void success(SpotifyTrack spotifyTrack, Response response) {
                Timber.i(spotifyTrack.name + " " + spotifyTrack.href);
                PlaylistManager.getInstance().addTrack(new Track(spotifyTrack));
                dataSuccess();
            }

            @Override
            public void failure(RetrofitError error) {

                String logMessage;

                switch (error.getResponse().getStatus()) {
                    case 400:
                        logMessage = "Bad Request";
                        break;
                    case 401:
                        logMessage = "Authentication Failed";
                        break;
                    case 403:
                        logMessage = "Forbidden";
                        break;
                    case 404:
                        logMessage = "Not Found";
                        break;

                    default:
                        logMessage = "Other Error";
                }

                Timber.i(logMessage);
                dataFailure(error.getResponse().getStatus());
            }
        });
    }

    private void getSoundCloudTrack(String whateverData) {

    }

    private void dataSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "Song Added Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void dataFailure(final int responseCode) {

        final Activity context = this;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(responseCode == 401) {
                    Toast.makeText(getBaseContext(), "Song Could Not Be Added, Please Log In Again", Toast.LENGTH_SHORT).show();
                    SpotifyAuthenticator.authenticate(context, SPOTIFY_REQUEST_CODE);
                }
                else {
                    Toast.makeText(getBaseContext(), "Song Could Not Be Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SPOTIFY_REQUEST_CODE) {
            Toast.makeText(getBaseContext(), "Try Adding the Song Again", Toast.LENGTH_SHORT).show();
            SpotifyAuthenticator.handleResponse(this, resultCode, intent);
            finish();

        }
    }

    private class DownloadResolveMetaData extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {

            Log.d("debug", "running resolve");
            HttpURLConnection urlConnection = null;
            String result = null;
            JSONObject tempObj = null;

            try{
                Log.d("params[0] = ", params[0].toString());

                urlConnection = (HttpURLConnection) params[0].openConnection();
                int statusCode = urlConnection.getResponseCode();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String inputStr;
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStringBuilder = new StringBuilder();

                while ((inputStr = streamReader.readLine()) != null){
                    responseStringBuilder.append(inputStr);
                    Log.i("Parsing JSON: current string", inputStr);
                }

                tempObj = new JSONObject(responseStringBuilder.toString());

                result = tempObj.getString("location");

                Log.d("Done with task, JSON is as follows", tempObj.toString());
            }
            catch(IOException e){
                e.printStackTrace();
                return null;
            } catch(JSONException e){
                e.printStackTrace();
                return null;
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        protected void onPostExecute(String result) {
            URL url = null;

            try{
                url = new URL(result);
                Log.e("on post execute", "...");

            }catch(Exception e){
                e.printStackTrace();
            }

            new DownloadSoundcloudTrackMetadata().execute(url);
        }
    }

    private class DownloadSoundcloudTrackMetadata extends AsyncTask<URL, Integer, SoundcloudTrack> {

        @Override
        protected SoundcloudTrack doInBackground(URL... params) {

            Log.d("soundcloudtrack params[0] = ", params[0].toString());
            HttpURLConnection urlConnection = null;
            JSONObject tempObj = null;
            SoundcloudTrack track = null;

            try{
                urlConnection = (HttpURLConnection) params[0].openConnection();
                int statusCode = urlConnection.getResponseCode();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String inputStr;
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStringBuilder = new StringBuilder();

                while ((inputStr = streamReader.readLine()) != null){
                    responseStringBuilder.append(inputStr);
                    Log.i("Parsing JSON for Track task: current string", inputStr);
                }

                tempObj = new JSONObject(responseStringBuilder.toString());

                Timber.i("tempObje" + tempObj);

                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                track = gson.fromJson(tempObj.toString(), SoundcloudTrack.class);

                Log.i("asdf", "woot  " + track.toString());

            }
            catch(IOException e){
                e.printStackTrace();
                return null;
            } catch(JSONException e){
                e.printStackTrace();
                return null;
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return track;
        }

        protected void onPostExecute(SoundcloudTrack result) {
            Log.d("starting media player", result.uri.toString());

            //String uriWithClientId = result.uri.toString() + "/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a";

            //sou = new SoundcloudPlayer(result.uri.toString());

            if(!result.streamable){
                Toast.makeText(getBaseContext(), "Song Not Streamable", Toast.LENGTH_LONG).show();
                finish();
            }

            PlaylistManager.getInstance().addTrack(new Track(result));

            Log.d(":)", "Success");
            Toast.makeText(getBaseContext(), "Song Added Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
