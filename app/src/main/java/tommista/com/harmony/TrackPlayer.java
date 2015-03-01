package tommista.com.harmony;

import timber.log.Timber;
import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.models.Track;
import tommista.com.harmony.spotify.EndTrackCallback;
import tommista.com.harmony.spotify.SpotifyPlayer;

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

        if(isPlaying){
            playPauseTrack();
        }

        if(index >= playlistManager.trackList.size()){
            index = 0;
        }

        playingIndex = index;

        HarmonyActivity.getInstance().setContentView(R.layout.track_view);

        Track track = playlistManager.trackList.get(playingIndex);

        if(track.isSpotifyTrack){
            Timber.i("Playing spotify track %s at position %d", track.title, index);
            spotifyPlayer = new SpotifyPlayer(HarmonyActivity.getInstance(), track.trackId, new EndTrackCallback() {
                @Override
                public void trackEnded() {
                    isPlaying = false;
                    playingIndex++;
                    playTrack(playingIndex);
                }
            });
        }else{

        }

        isPlaying = true;

    }

    public void playPauseTrack(){
        Track track = playlistManager.trackList.get(playingIndex);

        if(track.isSpotifyTrack){
            if(isPlaying){
                isPlaying = false;
                spotifyPlayer.pause();
            } else{
                if(spotifyPlayer != null){
                    isPlaying = true;
                    spotifyPlayer.resume();
                }else{
                    isPlaying = false;
                    playTrack(playingIndex);
                }
            }
        }else{

        }
    }

    public void previousTrack(){
        PlaylistManager.getInstance().serializeList();
    }

    public void nextTrack(){

        if(isPlaying){
            playPauseTrack();
        }

        playingIndex++;

        playTrack(playingIndex);

    }

}
