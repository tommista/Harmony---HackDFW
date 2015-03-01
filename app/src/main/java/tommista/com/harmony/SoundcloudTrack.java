package tommista.com.harmony;

import com.google.gson.annotations.SerializedName;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by kyle on 2/28/2015.
 */
public class SoundcloudTrack {
    public SoundcloudTrack(SoundcloudTrack soundcloudTrack){
        this.trackId = soundcloudTrack.trackId;




    }

    @SerializedName("id")
    public String trackId;



}
