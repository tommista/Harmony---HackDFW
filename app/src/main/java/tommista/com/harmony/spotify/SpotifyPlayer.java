package tommista.com.harmony.spotify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import timber.log.Timber;
import tommista.com.harmony.Constants;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.HarmonyApp;
import tommista.com.harmony.R;
import tommista.com.harmony.tracks.ForegroundService;
import tommista.com.harmony.tracks.State;

/**
 * Created by Jacob on 2/28/15.
 */
public class SpotifyPlayer {

    private Context context;
    private Player player;
    private EndTrackCallback callback;
    private State state = State.CREATED;

    private ConnectionStateCallback connectionStateCallback;
    private PlayerNotificationCallback playerNotificationCallback;

    public SpotifyPlayer(Context context, EndTrackCallback callback) {
        this.context = context;
        this.callback = callback;

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
                Timber.i("Initialized.");
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

                switch (eventType) {

                    case END_OF_CONTEXT:
                        pause();
                        callback.trackEnded();
                        break;

                    case PAUSE:

                    case LOST_PERMISSION:
                        pause();
                        changeState(State.PAUSED);

                        HarmonyApp app = (HarmonyApp) HarmonyActivity.getInstance().getApplication();
                        if (app.isServiceRunning(ForegroundService.class)) {
                            Intent startIntent = new Intent(HarmonyActivity.getInstance(), ForegroundService.class);
                            startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
                            HarmonyActivity.getInstance().startService(startIntent);
                        }
                        break;
                }

                Timber.i("DEFAULT: %s  %s  %s  %s", eventType.name(), playerState.playing, playerState.positionInMs, playerState.durationInMs);
            }

            @Override
            public void onPlaybackError(ErrorType errorType, String s) {
                Timber.i("Playback Error:\n" + errorType.name() + ": " + s);
            }
        };
    }

    public void start(String songID) {
        changeState(State.PREPARING);
        player.play("spotify:track:" + songID);
        changeState(State.PLAYING);
    }

    public void pause() {
        player.pause();
        changeState(State.PAUSED);
    }

    public void resume() {
        player.resume();
        changeState(State.PLAYING);
    }

    public State getState() {
        return state;
    }

    public void changeState(State state) {
        this.state = state;
        Timber.i(state.toString());
    }

    public void release() {
        Spotify.destroyPlayer(this);
        changeState(State.DESTROYED);
    }
}
