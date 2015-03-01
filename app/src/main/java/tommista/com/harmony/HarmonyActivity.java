package tommista.com.harmony;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.net.URI;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class HarmonyActivity extends ActionBarActivity implements MediaPlayer.OnPreparedListener {
    private  MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harmony);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//        try{
//            mMediaPlayer.setDataSource("http://api.soundcloud.com/tracks/7399237/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a");
//            Log.i("URLLLL", "should have set url");
//            //https://api.soundcloud.com/tracks/7399237/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a
//            //"http://api.soundcloud.com/tracks/7399237/stream?client_id=XXX"
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
//            Log.i("playing", "illegal argument exception");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i("playing", "ioexception");
//        }
//
//        try {
//            mMediaPlayer.prepare();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String shareURL = "Long Way Down - SoundCloud Listen to Long Way Down by Robert DeLong #np on #SoundCloud http://soundcloud.com/robertdelong/long-way-down";
        String url = shareURL.substring(shareURL.indexOf("http"),shareURL.length());
        String clientId = "55de8cc1d6246dd72e0a78b1c70fd91a";
        Log.i("substring",url);


        // Testing soundcloud api
        SoundcloudAPI.getInstance().soundcloudService.resolveData(url, clientId, new Callback<SoundcloudTrack>() {
            @Override
            public void success(SoundcloudTrack soundcloudTrack, Response response) {
                try {
                    mMediaPlayer.setDataSource(soundcloudTrack.uri);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    Log.d(":)", "Success");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("SoundcloudAPI Error", "The fuck bro?");
                error.printStackTrace();
            }
        });


        }
    public void onPrepared(MediaPlayer mediaPlayer){
        //mMediaPlayer.start();
        Log.i("playing", "should be playing the song");
    }










    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_harmony, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
