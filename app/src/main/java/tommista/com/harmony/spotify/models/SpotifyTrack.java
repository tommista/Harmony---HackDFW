package tommista.com.harmony.spotify.models;

import java.util.Map;

/**
 * <a href="https://developer.spotify.com/web-api/object-model/#track-object-full">Track object model</a>
 */
public class SpotifyTrack extends TrackSimple {
    public AlbumSimple album;
    public Map<String, String> external_ids;
    public Integer popularity;
}