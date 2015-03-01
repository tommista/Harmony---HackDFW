package tommista.com.harmony.models;

import tommista.com.harmony.spotify.models.SpotifyTrack;

import static tommista.com.harmony.models.Track.TrackType.*;

/**
 * Created by tbrown on 2/28/15.
 */
public class Track {

    public enum TrackType{
        SPOTIFY, SOUNDCLOUD
    }

    public Track(String title, String artist){
        this.title = title;
        this.artist = artist;
        this.imageURL = "http://imgs.xkcd.com/comics/dress_color.png";
        this.trackType = SPOTIFY;
    }

    public Track(SpotifyTrack spotifyTrack){
        this.title = spotifyTrack.name;
        this.artist = spotifyTrack.artists.get(0).name;
        this.imageURL = spotifyTrack.album.images.get(0).url;
        this.trackId = spotifyTrack.id;
        this.trackType = SPOTIFY;
    }

    public String trackId;
    public String title;
    public String artist;
    public String imageURL;
    public TrackType trackType;

}
