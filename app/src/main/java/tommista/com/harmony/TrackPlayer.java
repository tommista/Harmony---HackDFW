package tommista.com.harmony;

/**
 * Created by tbrown on 2/28/15.
 */
public class TrackPlayer {

    public static TrackPlayer instance;

    public static TrackPlayer getInstance(){
        if(instance == null){
            instance = new TrackPlayer();
        }
        return instance;
    }

    private TrackPlayer(){

    }

    public void playPauseTrack(){

    }

    public void previousTrack(){

    }

    public void nextTrack(){

    }

}
