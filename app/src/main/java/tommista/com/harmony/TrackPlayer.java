package tommista.com.harmony;

import timber.log.Timber;
import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.models.Track;
import tommista.com.harmony.spotify.SpotifyPlayer;

import static tommista.com.harmony.models.Track.TrackType.SPOTIFY;

/**
 * Created by tbrown on 2/28/15.
 */
public class TrackPlayer {

    private static TrackPlayer instance;

    private PlaylistManager playlistManager;
    private SpotifyPlayer spotifyPlayer;
    private int playingIndex;
    private boolean isPlaying;

    public static TrackPlayer getInstance(){
        if(instance == null){
            instance = new TrackPlayer();
        }
        return instance;
    }

    private TrackPlayer(){
        playlistManager = PlaylistManager.getInstance();
        playingIndex = 0;
        isPlaying = false;
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

        Track track = playlistManager.trackList.get(playingIndex);

        Timber.i("Playing track %s at position %d", track.title, index);

        if(track.trackType == SPOTIFY){
            spotifyPlayer = new SpotifyPlayer(HarmonyActivity.getInstance(), track.trackId);
            isPlaying = true;
        }else{

        }

    }

    public void playPauseTrack(){
        Track track = playlistManager.trackList.get(playingIndex);

        if(track.trackType == SPOTIFY){
            if(isPlaying){
                spotifyPlayer.pause();
            } else{
                spotifyPlayer.resume();
            }
        }else{

        }
    }

    public void previousTrack(){

    }

    public void nextTrack(){

    }

}
