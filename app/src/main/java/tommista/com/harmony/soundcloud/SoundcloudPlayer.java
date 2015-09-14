package tommista.com.harmony.soundcloud;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

import timber.log.Timber;
import tommista.com.harmony.Constants;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.HarmonyApp;
import tommista.com.harmony.models.Track;
import tommista.com.harmony.tracks.ForegroundService;
import tommista.com.harmony.tracks.State;
import tommista.com.harmony.tracks.TrackPlayer;
import tommista.com.harmony.ui.VCRView;

/**
 * Created by tbrown on 3/1/15.
 */
public class SoundcloudPlayer {

    private MediaPlayer mediaPlayer;
    private static SoundcloudPlayer instance;

    boolean allowPrepare = true;
    private State state = State.CREATED;

    private SoundcloudPlayer(){
        mediaPlayer = new MediaPlayer();
    }

    public static SoundcloudPlayer getInstance() {
        if(instance == null) {
            instance = new SoundcloudPlayer();
        }
        return instance;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mediaPlayer.setOnCompletionListener(listener);
    }

    public void start(String streamUri) {

        allowPrepare = true;

        streamUri += "/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a";

        Timber.i("SoundcloudPlayer: " + streamUri);

        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(streamUri);
            mediaPlayer.prepareAsync();
            changeState(State.PREPARING);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if(TrackPlayer.getInstance().checkHasAudioFocus()){
                        if (state != State.CREATED) {
                            mediaPlayer.start();
                            changeState(State.PLAYING);
                        }
                    }
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void stopPreparing() {
        changeState(State.CREATED);
        mediaPlayer.reset();
    }

    public void pause(){
        if(mediaPlayer.isPlaying() && state == State.PLAYING){
            mediaPlayer.pause();
            changeState(State.PAUSED);
        }
    }

    public void play(){
        if(!mediaPlayer.isPlaying() && state == State.PAUSED){
            mediaPlayer.start();
            changeState(State.PLAYING);
        }
    }

    public void release() {
        mediaPlayer.release();
        changeState(State.DESTROYED);
        instance = null;
    }

    public State getState() {
        return state;
    }

    private void changeState(State state) {
        this.state = state;
        Timber.i(state.toString());
    }
}