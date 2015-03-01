package tommista.com.harmony;

import com.google.gson.annotations.SerializedName;
import com.google.gson.*;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by kyle on 2/28/2015.
 */
public class SoundcloudTrack {
    public SoundcloudTrack(SoundcloudTrack soundcloudTrack){
        this.trackId = soundcloudTrack.trackId;
        this.songName = soundcloudTrack.songName;
        this.uri = soundcloudTrack.uri;
        this.artworkUrl = soundcloudTrack.artworkUrl;
        this.artist.artistName = soundcloudTrack.artist.artistName;
    }

    @SerializedName("id")
    public String trackId;

    @SerializedName("title")
    public String songName;

    @SerializedName("uri")
    public String uri;

    @SerializedName("artwork_url")
    public String artworkUrl;

    @SerializedName("user")
    public Artist artist;

    private class Artist {
        @SerializedName("username")
        public String artistName;
    }

    @SerializedName("duration")
    public long durationMillis;

    @Override
    public String toString() {
        return this.artist.artistName;
    }

}
