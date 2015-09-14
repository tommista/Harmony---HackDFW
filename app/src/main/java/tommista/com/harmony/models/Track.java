package tommista.com.harmony.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.soundcloud.SoundcloudTrack;
import tommista.com.harmony.spotify.models.SpotifyTrack;

/**
 * Created by tbrown on 2/28/15.
 */
public class Track {

    public Track(String title, String artist){
        this.title = title;
        this.artist = artist;
        this.imageURL = "http://imgs.xkcd.com/comics/dress_color.png";
        this.isSpotifyTrack = true;
    }

    public Track(SpotifyTrack spotifyTrack){
        this.title = spotifyTrack.name;
        this.artist = spotifyTrack.artists.get(0).name;
        this.imageURL = spotifyTrack.album.images.get(0).url;
        this.trackId = spotifyTrack.id;
        this.isSpotifyTrack = true;
    }

    public Track(SoundcloudTrack soundcloudTrack){
        this.title = soundcloudTrack.songName;
        this.artist = soundcloudTrack.artist.artistName;
        this.imageURL = soundcloudTrack.artworkUrl;
        this.trackId = soundcloudTrack.uri;
        this.isSpotifyTrack = false;
    }

    public String trackId;
    public String title;
    public String artist;
    public String imageURL;
    public boolean isSpotifyTrack;

}
