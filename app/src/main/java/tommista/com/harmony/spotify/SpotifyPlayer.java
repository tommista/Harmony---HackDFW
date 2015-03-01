package tommista.com.harmony.spotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import timber.log.Timber;
import tommista.com.harmony.R;

/**
 * Created by Jacob on 2/28/15.
 */
public class SpotifyPlayer {

    private Context context;
    private Player player;
    private String songID;

    private ConnectionStateCallback connectionStateCallback;
    private PlayerNotificationCallback playerNotificationCallback;

    public SpotifyPlayer(Context context, String songID) {
        this.context = context;
        this.songID = songID;

        makeListeners();
        init();
    }

    private void init() {
        Resources res = context.getResources();
        String prefsName = res.getString(R.string.shared_prefs_name);
        String key = res.getString(R.string.spotify_token_key);
        String defValue = res.getString(R.string.shared_prefs_def_string);
        String clientID = res.getString(R.string.spotify_client_id);

        SharedPreferences preferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        String authToken = preferences.getString(key, defValue);

        Config playerConfig = new Config(context, authToken, clientID);
        player = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                player.addConnectionStateCallback(connectionStateCallback);
                player.addPlayerNotificationCallback(playerNotificationCallback);
                player.play("spotify:track:" + songID);
                //player.play("spotify:track:3DujQnASb2AqRJFr52EehS");
            }

            @Override
            public void onError(Throwable throwable) {
                Timber.d(throwable, "Player Error");
            }
        });
    }

    private void makeListeners() {
        connectionStateCallback = new ConnectionStateCallback() {
            @Override
            public void onLoggedIn() {
                Timber.i("Logged In");
            }

            @Override
            public void onLoggedOut() {
                Timber.i("Logged Out");
            }

            @Override
            public void onLoginFailed(Throwable throwable) {
                Timber.i(throwable, "Log In Failed");
            }

            @Override
            public void onTemporaryError() {
                Timber.i("Temporary Error");
            }

            @Override
            public void onConnectionMessage(String s) {
                Timber.i("Connection Message: " + s);
            }
        };

        playerNotificationCallback = new PlayerNotificationCallback() {

            @Override
            public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
                Timber.i("Playback Event: " + eventType.name());
            }

            @Override
            public void onPlaybackError(ErrorType errorType, String s) {
                Timber.i("Playback Error:\n" + errorType.name() + ": " + s);
            }
        };
    }

    public void pause() {
        player.pause();
    }

    public void resume() {
        player.resume();
    }
}
