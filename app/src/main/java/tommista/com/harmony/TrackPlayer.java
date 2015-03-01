package tommista.com.harmony;

import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.models.Track;

/**
 * Created by tbrown on 2/28/15.
 */
public class TrackPlayer {

    private static TrackPlayer instance;

    private PlaylistManager playlistManager;
    private int playingIndex;

    public static TrackPlayer getInstance(){
        if(instance == null){
            instance = new TrackPlayer();
        }
        return instance;
    }

    private TrackPlayer(){
        playlistManager = PlaylistManager.getInstance();
        playingIndex = 0;
    }

    public Track getCurrentTrack(){
        //return playlistManager.trackList.get(0);
        if(playlistManager.trackList.size() == 0){
            return new Track("Empty", "Empty");
        }else{
            return playlistManager.trackList.get(playingIndex);
        }
    }

    public void playTrack(int index){
        playPauseTrack();
        playingIndex = index;
    }

    public void playPauseTrack(){

    }

    public void previousTrack(){

    }

    public void nextTrack(){

    }

}
