package tommista.com.harmony;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import java.net.URI;
import java.net.URL;

import timber.log.Timber;


public class HarmonyActivity extends ActionBarActivity implements MediaPlayer.OnPreparedListener {
    private  MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harmony);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        String clientId = "55de8cc1d6246dd72e0a78b1c70fd91a";
        String secret = "ab4c14572d6c83b5ec1e333ce9de47c5";
        String callbackUrl = "harmony://soundcloud/callback";

        String shareURL = "Long Way Down - SoundCloud Listen to Long Way Down by Robert DeLong #np on #SoundCloud http://soundcloud.com/robertdelong/long-way-down";
        String url = shareURL.substring(shareURL.indexOf("http"),shareURL.length());

        Log.i("substring",url);


        URL url2 = null;
        try {
            String apiUrl = "http://api.soundcloud.com/resolve.json?url=" + url + "&client_id=55de8cc1d6246dd72e0a78b1c70fd91a";
            url2 = new URL(apiUrl);
            new DownloadResolveMetaData().execute(url2);
        } catch (MalformedURLException e) {
            Log.e("onCreate", "Could not start resolvemetadata background task");
            e.printStackTrace();
        }

        }
    public void onPrepared(MediaPlayer mediaPlayer){
        //mMediaPlayer.start();
        Log.i("playing", "should be playing the song");
    }










    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_harmony, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        protected void onProgressUpdate(Integer... progress) {
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

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(SoundcloudTrack result) {
            try {
                Log.d("starting media player", result.uri.toString());

                String uriWithClientId = result.uri.toString() + "/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a";

                mMediaPlayer.setDataSource(uriWithClientId);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                Log.d(":)", "Success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

