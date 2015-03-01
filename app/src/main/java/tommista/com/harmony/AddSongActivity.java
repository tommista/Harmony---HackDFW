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
import tommista.com.harmony.spotify.models.Track;
import tommista.com.harmony.spotify.webapi.SpotifyApi;
import tommista.com.harmony.spotify.webapi.SpotifyService;

/**
 * Created by Jacob on 2/28/15.
 */
public class AddSongActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_song_layout);

        Timber.plant(new Timber.DebugTree());

        parseIntent();
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

    //For use in parseIntent() when getSpotifyID() returns a track ID
    private void getSpotifyTrack(String trackID) {

        //get authToken from shared prefs
        String prefsName = getResources().getString(R.string.shared_prefs_name);
        SharedPreferences preferences = getSharedPreferences(prefsName, Context.MODE_PRIVATE);

        String authKey = getResources().getString(R.string.spotify_token_key);
        String defValue = getResources().getString(R.string.shared_prefs_def_string);

        String authToken = preferences.getString(authKey, defValue);


        //connect to spotify to retrieve track data

        SpotifyApi wrapper = new SpotifyApi();

        wrapper.setAccessToken(authToken);

        SpotifyService spotify = wrapper.getService();

        spotify.getTrack(trackID, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                Timber.i(track.name + " " + track.href);
                dataSuccess();
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.i(error.toString());
                dataFailure();
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

    private void dataFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "Song Could Not Be Added", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
