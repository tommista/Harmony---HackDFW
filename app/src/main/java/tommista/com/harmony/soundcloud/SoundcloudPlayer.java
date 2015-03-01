package tommista.com.harmony.soundcloud;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by tbrown on 3/1/15.
 */
public class SoundcloudPlayer {

    private MediaPlayer mediaPlayer;

    public SoundcloudPlayer(String streamUri){

        streamUri += "/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a";

        mediaPlayer = new MediaPlayer();

        try{
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(streamUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(IOException e){
            e.printStackTrace();
        }


    }

    public void pause(){

    }

    public void resume(){

    }

}
