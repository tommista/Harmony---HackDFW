package tommista.com.harmony;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;


public class HarmonyActivity extends ActionBarActivity implements MediaPlayer.OnPreparedListener {
    private  MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harmony);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            mMediaPlayer.setDataSource("https://api.soundcloud.com/tracks/7399237/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a");
            Log.i("URLLLL", "should have set url");

            //"http://api.soundcloud.com/tracks/7399237/stream?client_id=XXX"
            mMediaPlayer.prepare();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            Log.i("playing", "illegalargument exception");


        } catch (IOException e) {
            e.printStackTrace();
            Log.i("playing", "ioexception");


        }


        }
    public void onPrepared(MediaPlayer mediaPlayer){
        mMediaPlayer.start();
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
