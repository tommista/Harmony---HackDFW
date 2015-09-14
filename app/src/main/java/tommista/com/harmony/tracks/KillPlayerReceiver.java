package tommista.com.harmony.tracks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jacob on 9/13/15.
 */
public class KillPlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TrackPlayer.getInstance().pauseAll();
    }
}
