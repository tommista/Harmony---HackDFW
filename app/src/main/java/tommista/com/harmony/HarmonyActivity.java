package tommista.com.harmony;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.LayoutRes;

import timber.log.Timber;
import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.spotify.SpotifyAuthenticator;
import tommista.com.harmony.tracks.ForegroundService;
import tommista.com.harmony.tracks.TrackPlayer;


public class HarmonyActivity extends Activity {

    private static final int SPOTIFY_REQUEST_CODE = 1337;

    private static HarmonyActivity instance;

    private Bitmap bitmap;

    private int layoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("OnCreate.");

        instance = this;

        setContentView(R.layout.playlist_view);

        SpotifyAuthenticator.authenticate(this, SPOTIFY_REQUEST_CODE);

        PlaylistManager.getInstance().loadList();
    }

    public static HarmonyActivity getInstance() {
        return instance;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void startService() {
        Intent startIntent = new Intent(this, ForegroundService.class);
        startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
        startService(startIntent);
    }

    public void stopService() {
        Intent stopIntent = new Intent(this, ForegroundService.class);
        stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
        startService(stopIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        this.layoutId = layoutResID;
        super.setContentView(layoutResID);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SPOTIFY_REQUEST_CODE) {
            SpotifyAuthenticator.handleResponse(this, resultCode, intent);
        }
    }

    @Override
    public void onBackPressed() {
        Timber.i("back button pressed");
        if(layoutId != R.layout.playlist_view) {
            setContentView(R.layout.playlist_view);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.i("OnStart.");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("OnResume.");

        stopService();
    }

    @Override
    protected void onPause() {
        Timber.i("OnPause.");
        startService();

        PlaylistManager.getInstance().serializeList();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        stopService();

        TrackPlayer.getInstance().release();

        Timber.i("Destroyed.");
        super.onDestroy();
    }
}
