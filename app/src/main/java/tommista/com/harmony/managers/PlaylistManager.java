package tommista.com.harmony.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;
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
    }

    public void addTrack(Track track){
        trackList.add(track);
        serializeList();
    }

    public void loadList(){
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        SharedPreferences prefs = HarmonyActivity.getInstance().getSharedPreferences("tommista.com.harmony", Context.MODE_PRIVATE);
        String json = prefs.getString("jsonData", "");

        if(json.isEmpty()){
            trackList = new ArrayList<Track>();
        }else{
            trackList = new ArrayList<Track>(Arrays.asList(gson.fromJson(json, Track[].class)));
        }

        Timber.i("loadList");

        sendTrackAddedMessage();
    }

    public void serializeList(){
        Activity activity = HarmonyActivity.getInstance();
        SharedPreferences prefs = activity.getSharedPreferences("tommista.com.harmony", Context.MODE_PRIVATE);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        String json = gson.toJson(trackList);

        prefs.edit().putString("jsonData", json).apply();
    }

    private void sendTrackAddedMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("newTrackIntent");
        LocalBroadcastManager.getInstance(HarmonyActivity.getInstance()).sendBroadcast(intent);
    }

}
