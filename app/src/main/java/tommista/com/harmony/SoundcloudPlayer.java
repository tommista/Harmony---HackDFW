package tommista.com.harmony;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import de.voidplus.soundcloud.SoundCloud;
import de.voidplus.soundcloud.Track;

/**
 * Created by User on 2/28/2015.
 */
public class SoundcloudPlayer
{
    MediaPlayer mediaPlayer;
    SoundCloud soundcloud;

    public SoundcloudPlayer (Context context, Uri uri) {
        soundcloud = new SoundCloud(
                "55de8cc1d6246dd72e0a78b1c70fd91a", //client id
                "ab4c14572d6c83b5ec1e333ce9de47c5" // client secret
        );
        mediaPlayer = MediaPlayer.create(context, uri);
    }

    public SoundcloudPlayer (Context context, int id_num) {
        soundcloud = new SoundCloud(
                "55de8cc1d6246dd72e0a78b1c70fd91a", //client id
                "ab4c14572d6c83b5ec1e333ce9de47c5" // client secret
        );

        soundcloud.get("tracks/{track_id}"); // /tracks/{track_id}
        Track track = soundcloud.getTrack(id_num);

        Uri trackUri = Uri.parse(track.getUri());
        mediaPlayer = MediaPlayer.create(context, trackUri);
    }

    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.stop();
    }

}
