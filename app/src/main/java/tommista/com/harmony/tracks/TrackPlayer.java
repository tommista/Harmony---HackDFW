package tommista.com.harmony.tracks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Random;

import timber.log.Timber;
import tommista.com.harmony.Constants;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.HarmonyApp;
import tommista.com.harmony.managers.PlaylistManager;
import tommista.com.harmony.models.Track;
import tommista.com.harmony.soundcloud.SoundcloudPlayer;
import tommista.com.harmony.spotify.EndTrackCallback;
import tommista.com.harmony.spotify.SpotifyPlayer;
import tommista.com.harmony.ui.VCRView;

/**
 * Created by tbrown on 2/28/15.
 */
public class TrackPlayer implements AudioManager.OnAudioFocusChangeListener {

    private static TrackPlayer instance;

    private PlaylistManager playlistManager;
    private SpotifyPlayer spotifyPlayer;
    private SoundcloudPlayer soundcloudPlayer;
    private AudioManager manager;
    private int playingIndex;
    public boolean isShuffle;
    public boolean isRepeat;
    private Random rand;
    private boolean released = false;
    private boolean wasPlayingAtTransientLoss = false;

    private Bitmap bitmap;

    public static TrackPlayer getInstance() {
        if (instance == null) {
            instance = new TrackPlayer();
        }
        return instance;
    }

    private TrackPlayer() {
        playlistManager = PlaylistManager.getInstance();
        playingIndex = 0;
        isShuffle = false;
        isRepeat = false;
        rand = new Random(4);
        manager = (AudioManager) HarmonyActivity.getInstance().getSystemService(Context.AUDIO_SERVICE);

        soundcloudPlayer = SoundcloudPlayer.getInstance();
        soundcloudPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Timber.i("Move next.");
                nextTrack();
            }
        });

        spotifyPlayer = new SpotifyPlayer(HarmonyActivity.getInstance(), new EndTrackCallback() {
            @Override
            public void trackEnded() {
                Timber.i("Move next.");
                nextTrack();
            }
        });
    }

    public Track getCurrentTrack() {
        if (playlistManager.trackList.size() == 0) {
            return new Track("Empty", "Empty");
        } else {
            return playlistManager.trackList.get(playingIndex);
        }
    }

    public void playTrack(int index) {

        pauseAll();

        if (index >= playlistManager.trackList.size()) {

            if (isRepeat) {
                index = 0;
            } else {
                playingIndex = index - 1;
                adjustVCR();
                return;
            }
        } else if (index < 0) {
            if (isRepeat) {
                index = playlistManager.trackList.size() - 1;
            } else {
                index = 0;
            }
        }

        playingIndex = index;

        Track track = playlistManager.trackList.get(playingIndex);

        if (checkHasAudioFocus()) {
            if (track.isSpotifyTrack) {
                spotifyPlayer.start(track.trackId);
            } else {
                soundcloudPlayer.start(track.trackId);
            }
        }

        adjustVCR();

        sendNextTrackMessage();
    }

    public void playPauseTrack() {
        if (playingIndex >= playlistManager.trackList.size()) {
            playingIndex = playlistManager.trackList.size() - 1;
        }

        Track track = playlistManager.trackList.get(playingIndex);

        if (checkHasAudioFocus()) {
            if (isPlaying()) {
                pauseAll();
            } else {
                if (track.isSpotifyTrack) {
                    if (spotifyPlayer.getState() == State.PAUSED) {
                        spotifyPlayer.resume();
                    } else {
                        playTrack(playingIndex);
                    }
                } else {
                    if (soundcloudPlayer.getState() == State.PAUSED) {
                        soundcloudPlayer.play();
                    } else {
                        playTrack(playingIndex);
                    }
                }
            }
        }

        adjustVCR();
    }

    private void adjustVCR() {
        if (VCRView.instance != null) {
            VCRView.instance.adjustPlayPause();
        }
    }

    public void previousTrack() {
        pauseAll();
        soundcloudPlayer.stopPreparing();

        decrementIndex();

        playTrack(playingIndex);
    }

    public Track getPreviousTrack() {
        int tempIndex = playingIndex;

        tempIndex--;

        if (tempIndex < 0) {
            if (isRepeat) {
                tempIndex = playlistManager.trackList.size() - 1;
            } else {
                tempIndex = 0;
            }
        }

        return playlistManager.trackList.get(tempIndex);
    }

    public void nextTrack() {
        pauseAll();
        soundcloudPlayer.stopPreparing();

        incrementIndex();

        playTrack(playingIndex);

    }

    public Track getNextTrack() {
        int tempIndex = playingIndex;

        tempIndex++;

        if (tempIndex >= playlistManager.trackList.size()) {
            if (isRepeat) {
                tempIndex = 0;
            } else {
                tempIndex = playlistManager.trackList.size() - 1;
            }
        }

        return playlistManager.trackList.get(tempIndex);
    }

    private void sendNextTrackMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("nextTrackIntent");
        LocalBroadcastManager.getInstance(HarmonyActivity.getInstance()).sendBroadcast(intent);

        HarmonyApp app = (HarmonyApp) HarmonyActivity.getInstance().getApplication();
        if (app.isServiceRunning(ForegroundService.class)) {
            Intent startIntent = new Intent(HarmonyActivity.getInstance(), ForegroundService.class);
            startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
            HarmonyActivity.getInstance().startService(startIntent);
        }
    }

    public void incrementIndex() {
        if (isShuffle) {
            playingIndex = rand.nextInt(playlistManager.trackList.size());
        } else {
            playingIndex++;
        }
    }

    public void decrementIndex() {
        if (isShuffle) {
            playingIndex = rand.nextInt(playlistManager.trackList.size());
        } else {
            playingIndex--;
        }
    }

    public boolean isPlaying() {
        return spotifyPlayer.getState() == State.PLAYING ||
                spotifyPlayer.getState() == State.PREPARING ||
                soundcloudPlayer.getState() == State.PLAYING ||
                soundcloudPlayer.getState() == State.PREPARING;
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setShuffle(boolean isShuffle) {
        this.isShuffle = isShuffle;
    }

    public void setRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public boolean checkHasAudioFocus() {
        int result = manager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    void pauseAll() {
        spotifyPlayer.pause();
        soundcloudPlayer.pause();
    }

    public void release() {
        if (!released) {
            soundcloudPlayer.release();
            spotifyPlayer.release();
        }
        released = true;
        instance = null;
    }


    @Override
    public void onAudioFocusChange(int focusChange) {

        if (spotifyPlayer.getState() != State.DESTROYED) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Timber.i("FOCUS LOST TRANSIENT");
                    if (isPlaying()) {
                        pauseAll();
                        wasPlayingAtTransientLoss = isPlaying();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_GAIN:
                    Timber.i("FOCUS GAINED");
                    Track track = getCurrentTrack();

                    if (wasPlayingAtTransientLoss) {
                        if (track.isSpotifyTrack) {
                            spotifyPlayer.resume();
                        } else {
                            soundcloudPlayer.play();
                        }
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    Timber.i("FOCUS LOST");
                    pauseAll();
                    break;

                default:
                    Timber.i("AUDIO STATE CHANGE NOT HANDLED.");
                    break;
            }

            if(wasPlayingAtTransientLoss) {
                HarmonyApp app = (HarmonyApp) HarmonyActivity.getInstance().getApplication();

                if (app.isServiceRunning(ForegroundService.class)) {
                    Intent startIntent = new Intent(HarmonyActivity.getInstance(), ForegroundService.class);
                    startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
                    HarmonyActivity.getInstance().startService(startIntent);
                }

                adjustVCR();
            }
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
