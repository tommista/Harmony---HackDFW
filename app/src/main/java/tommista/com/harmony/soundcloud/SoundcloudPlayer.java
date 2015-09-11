package tommista.com.harmony.soundcloud;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

import timber.log.Timber;
import tommista.com.harmony.HarmonyActivity;

/**
 * Created by tbrown on 3/1/15.
 */
public class SoundcloudPlayer {

    private MediaPlayer mediaPlayer;

    public SoundcloudPlayer(String streamUri, MediaPlayer.OnCompletionListener callback){

        streamUri += "/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a";

        Timber.i("SoundcloudPlayer: " + streamUri);

        mediaPlayer = new MediaPlayer();

        try{

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(streamUri);
            mediaPlayer.setOnCompletionListener(callback);
            Timber.i("@@@@ Pre prepare");
            mediaPlayer.prepareAsync();
            Timber.i("@@@@ Mid");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    Timber.i("@@@@ Post Start");
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }


    }

    public void pause(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void resume(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void clear() {
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();

    }
}