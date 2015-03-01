package tommista.com.harmony;
import retrofit.Callback;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.Path;

/**
 * Created by kyle on 2/28/2015.
 */
public interface SoundcloudService {
    @GET("/resolve.json")
    void resolveData(@Query(value="url",encodeValue = false) String url, @Query(value = "clientId", encodeValue = false) String clientId, Callback<SoundcloudTrack> cb);

//    @GET("/tracks/{trackId}.json?client_id=55de8cc1d6246dd72e0a78b1c70fd91a")
//    void getTrackData(@Path("trackId") String trackId, Callback<SoundcloudTrack> cb);
}
