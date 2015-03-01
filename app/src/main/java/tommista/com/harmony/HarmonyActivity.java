package tommista.com.harmony;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import timber.log.Timber;
import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.spotify.SpotifyAuthenticator;


public class HarmonyActivity extends Activity {

    private static final int SPOTIFY_REQUEST_CODE = 1337;

    public static Activity instance;

    public static Activity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO eventually put remote logging into a tree and put here.
        }

        setContentView(R.layout.playlist_view);

        SpotifyAuthenticator.authenticate(this, SPOTIFY_REQUEST_CODE);

        PlaylistManager.getInstance().loadList();

    }

    @Override
    protected void onPause(){
        PlaylistManager.getInstance().serializeList();
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SPOTIFY_REQUEST_CODE) {
            SpotifyAuthenticator.handleResponse(this, resultCode, intent);
        }
    }

    @Override
    public void onBackPressed(){
        Timber.i("back button pressed");
        setContentView(R.layout.playlist_view);
    }

}
