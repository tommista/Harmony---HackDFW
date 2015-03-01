package tommista.com.harmony;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.*;

import retrofit.RestAdapter;
import tommista.com.harmony.SoundcloudService;

/**
 * Created by kyle on 2/28/2015.
 */
public class SoundcloudAPI {
    private static final String SERVER_ADDRESS = "http://api.soundcloud.com";
    private static SoundcloudAPI instance;
    private final RestAdapter restAdapter;
    public Gson gson;

    public SoundcloudService soundcloudService;
    public static SoundcloudAPI getInstance (){
        if(instance ==null)
            instance =  new SoundcloudAPI();
        Log.d("debug", instance.toString());
        return instance;
    }

    private SoundcloudAPI () {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVER_ADDRESS)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        soundcloudService = restAdapter.create(SoundcloudService.class);
    }
}
