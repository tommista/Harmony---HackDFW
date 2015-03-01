package tommista.com.harmony.soundcloud;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kyle on 2/28/2015.
 */
public class SoundcloudTrack {
    public SoundcloudTrack(SoundcloudTrack soundcloudTrack){
        this.trackId = soundcloudTrack.trackId;
        this.songName = soundcloudTrack.songName;
        this.uri = soundcloudTrack.uri;
        this.artworkUrl = soundcloudTrack.artworkUrl;
        this.streamable = soundcloudTrack.streamable;
        this.artist.artistName = soundcloudTrack.artist.artistName;
    }

    @SerializedName("id")
    public String trackId;

    @SerializedName("title")
    public String songName;

    @SerializedName("streamable")
    public boolean streamable;

    @SerializedName("uri")
    public String uri;

    @SerializedName("artwork_url")
    public String artworkUrl;

    @SerializedName("user")
    public SoundcloudArtist artist;

    @SerializedName("duration")
    public long durationMillis;

    @Override
    public String toString() {
        return this.artist.artistName;
    }

}