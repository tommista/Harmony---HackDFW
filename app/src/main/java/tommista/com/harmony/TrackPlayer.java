package tommista.com.harmony;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Random;

import timber.log.Timber;
import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.models.Track;
import tommista.com.harmony.soundcloud.SoundcloudPlayer;
import tommista.com.harmony.spotify.EndTrackCallback;
import tommista.com.harmony.spotify.SpotifyPlayer;
import tommista.com.harmony.ui.VCRView;

/**
 * Created by tbrown on 2/28/15.
 */
public class TrackPlayer {

    private static TrackPlayer instance;

    private PlaylistManager playlistManager;
    private SpotifyPlayer spotifyPlayer;
    private SoundcloudPlayer soundcloudPlayer;
    private int playingIndex;
    private boolean isPlaying;
    public boolean isShuffle;
    public boolean isRepeat;
    private Random rand;

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
        isShuffle = false;
        isRepeat = false;
        rand = new Random(4);
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

            if(isRepeat){
                index = 0;
            }else{
                playingIndex = index - 1;
                return;
            }
        } else if(index < 0){
            index = 0;
        }

        playingIndex = index;

        //HarmonyActivity.getInstance().setContentView(R.layout.track_view);

        Track track = playlistManager.trackList.get(playingIndex);

        if(track.isSpotifyTrack){
            Timber.i("Playing spotify track %s at position %d", track.title, index);
            spotifyPlayer = new SpotifyPlayer(HarmonyActivity.getInstance(), track.trackId, new EndTrackCallback() {
                @Override
                public void trackEnded() {
                    isPlaying = false;
                    incrementIndex();
                    playTrack(playingIndex);
                }
            });
        }else{
            Timber.i("Playing soundcloud track %s at position %d", track.title, index);
            if(soundcloudPlayer != null) {
                soundcloudPlayer.clear();
            }
            soundcloudPlayer = new SoundcloudPlayer(track.trackId, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    incrementIndex();
                    playTrack(playingIndex);
                }
            });
        }

        isPlaying = true;

        if(VCRView.instance != null){
            VCRView.instance.adjustPlayPause();
        }

        sendNextTrackMessage();

    }

    public void playPauseTrack(){
        if (playingIndex >= playlistManager.trackList.size())
            playingIndex = playlistManager.trackList.size() - 1;
        
        Track track = playlistManager.trackList.get(playingIndex);

        Timber.i("playPauseTrack begin isPlaying: " + isPlaying);

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
            if(isPlaying){
                isPlaying = false;
                soundcloudPlayer.pause();
            } else{
                if(soundcloudPlayer != null){
                    isPlaying = true;
                    soundcloudPlayer.resume();
                }else{
                    isPlaying = false;
                    playTrack(playingIndex);
                }
            }
        }

        if(VCRView.instance != null){
            VCRView.instance.adjustPlayPause();
        }

        Timber.i("playPauseTrack end isPlaying: " + isPlaying);
    }

    public void previousTrack(){
        //PlaylistManager.getInstance().serializeList();

        if(isPlaying){
            playPauseTrack();
        }

        decrementIndex();

        playTrack(playingIndex);

    }

    public void nextTrack(){

        if(isPlaying){
            playPauseTrack();
        }

        incrementIndex();

        playTrack(playingIndex);

    }

    private void sendNextTrackMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("nextTrackIntent");
        LocalBroadcastManager.getInstance(HarmonyActivity.getInstance()).sendBroadcast(intent);
    }

    public void incrementIndex(){
        if(isShuffle){
            playingIndex = rand.nextInt(playlistManager.trackList.size());
            Timber.i("Randomly selected: " + playingIndex);
        }else{
            playingIndex++;
        }
    }

    public void decrementIndex(){
        if(isShuffle){
            playingIndex = rand.nextInt(playlistManager.trackList.size());
        }else{
            playingIndex--;
        }
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setShuffle(boolean isShuffle){
        this.isShuffle = isShuffle;
    }

    public void setRepeat(boolean isRepeat){
        this.isRepeat = isRepeat;
    }
}
