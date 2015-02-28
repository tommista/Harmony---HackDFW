package tommista.com.harmony.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.models.Track;

/**
 * Created by tbrown on 2/28/15.
 */
public class PlaylistManager {

    public static PlaylistManager instance;

    public ArrayList<Track> trackList;

    public static PlaylistManager getInstance(){
        if(instance == null){
            instance = new PlaylistManager();
        }
        return instance;
    }

    public PlaylistManager(){
        trackList = new ArrayList<>();
        trackList.add(new Track("test", "asdf"));
        trackList.add(new Track("asdf", "qwer"));
    }

    public void serializeList(){
        SharedPreferences prefs = HarmonyActivity.getInstance().getSharedPreferences("tommista.com.harmony", Context.MODE_PRIVATE);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        String json = gson.toJson(trackList);

        prefs.edit().putString("jsonData", json).apply();

    }

}
