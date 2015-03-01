package tommista.com.harmony;

import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.models.Track;

/**
 * Created by tbrown on 2/28/15.
 */
public class TrackPlayer {

    private static TrackPlayer instance;

    private PlaylistManager playlistManager;

    public static TrackPlayer getInstance(){
        if(instance == null){
            instance = new TrackPlayer();
        }
        return instance;
    }

    private TrackPlayer(){
        playlistManager = PlaylistManager.getInstance();
    }

    public Track getCurrentTrack(){
        return playlistManager.trackList.get(0);
    }

    public void playPauseTrack(){

    }

    public void previousTrack(){

    }

    public void nextTrack(){

    }

}
