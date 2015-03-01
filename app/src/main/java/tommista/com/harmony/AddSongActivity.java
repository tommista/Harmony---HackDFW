package tommista.com.harmony;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;
import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.models.Track;
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
                    //Sound cloud
                }
                else {
                    //Error
                }
            }
        }
        else {
            Timber.i("Bundle Empty");
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
}
