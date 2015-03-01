package tommista.com.harmony;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import tommista.com.harmony.spotify.SpotifyAuthenticator;
import tommista.com.harmony.spotify.SpotifyPlayer;


public class HarmonyActivity extends ActionBarActivity {

    private static final int SPOTIFY_REQUEST_CODE = 1337;

    boolean playing = true;

    private SpotifyPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harmony);

        SpotifyAuthenticator.authenticate(this, SPOTIFY_REQUEST_CODE);

        player = new SpotifyPlayer(this, "2TpxZ7JUBn3uw46aR7qd6V");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SPOTIFY_REQUEST_CODE) {
            SpotifyAuthenticator.handleResponse(this, resultCode, intent);
        }
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

    public void changeState(View view) {
        if(playing) {
            player.pause();
            ((Button)view).setText("Play");
        }
        else {
            player.resume();
            ((Button)view).setText("Pause");
        }

        playing = !playing;
    }
}
