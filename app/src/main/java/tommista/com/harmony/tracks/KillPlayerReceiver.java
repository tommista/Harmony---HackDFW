package tommista.com.harmony.tracks;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tommista.com.harmony.Constants;
import tommista.com.harmony.HarmonyActivity;

/**
 * Created by Jacob on 9/13/15.
 */
public class KillPlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TrackPlayer.getInstance().pauseAll();

        Intent stopIntent = new Intent(context, ForegroundService.class);
        stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
        context.startService(stopIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    }
}
