package tommista.com.harmony.tracks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import timber.log.Timber;
import tommista.com.harmony.Constants;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.models.Track;

public class ForegroundService extends Service {
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            Timber.i("Action = " + intent.getDataString() + " " + intent.getAction());

            if (!intent.getAction().equals(Constants.ACTION.STOP_FOREGROUND_ACTION)) {

                final TrackPlayer player = TrackPlayer.getInstance();

                Target target = new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Timber.i("Bitmap loaded.");
                        handleIntent(player, bitmap, intent);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Timber.i("Bitmap failed.");
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                        handleIntent(player, bitmap, intent);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Track notifcationTrack;

                if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
                    notifcationTrack = player.getNextTrack();
                } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
                    notifcationTrack = player.getPreviousTrack();
                } else {
                    notifcationTrack = player.getCurrentTrack();
                }

                Picasso.with(this).load(notifcationTrack.imageURL).into(target);
            } else {
                Timber.i("Stopped.");
                stopForeground(true);
                stopSelf(startId);
            }
        } else {
            String source = null == intent ? "Intent" : "Action";
            Timber.e(source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags));
        }

        return START_STICKY;
    }

    public void handleIntent(TrackPlayer player, Bitmap bitmap, Intent intent) {

        final String action = intent.getAction();

        switch (action) {
            case Constants.ACTION.START_FOREGROUND_ACTION:
                Timber.i("Received Start Foreground Intent");

                if (player.isPlaying()) {
                    startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            createNotification(
                                    player.getCurrentTrack(),
                                    true,
                                    bitmap));
                } else {
                    stopForeground(false);
                    NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            createNotification(
                                    player.getCurrentTrack(),
                                    false,
                                    bitmap));
                }


                break;

            case Constants.ACTION.PREV_ACTION:
                Timber.i("Clicked Previous.");
                player.previousTrack();

                if (player.isPlaying()) {
                    startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            createNotification(
                                    player.getCurrentTrack(),
                                    true,
                                    bitmap));
                } else {
                    stopForeground(false);
                    NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            createNotification(
                                    player.getCurrentTrack(),
                                    false,
                                    bitmap));
                }

                break;

            case Constants.ACTION.PLAY_ACTION:
                Timber.i("Clicked Play/Pause.");
                player.playPauseTrack();

                if (player.isPlaying()) {
                    startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            createNotification(
                                    player.getCurrentTrack(),
                                    true,
                                    bitmap));
                } else {
                    stopForeground(false);
                    NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            createNotification(
                                    player.getCurrentTrack(),
                                    player.isPlaying(),
                                    bitmap));
                }
                break;

            case Constants.ACTION.NEXT_ACTION:
                Timber.i("Clicked Next.");
                player.nextTrack();

                if (player.isPlaying()) {
                    startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            createNotification(
                                    player.getCurrentTrack(),
                                    player.isPlaying(),
                                    bitmap));
                } else {
                    stopForeground(false);
                    NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            createNotification(
                                    player.getCurrentTrack(),
                                    false,
                                    bitmap));
                }
                break;

            default:
                Timber.i("Unexpected action = " + intent.getAction());
                break;
        }
    }

    public Notification createNotification(Track track, boolean isPlaying, Bitmap image) {

        // Open App
        Intent notificationIntent = new Intent(this, HarmonyActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Press previous
        Intent previousIntent = new Intent(this, ForegroundService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent pendingPreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        // Press play/pause
        Intent playIntent = new Intent(this, ForegroundService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        // Press next
        Intent nextIntent = new Intent(this, ForegroundService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent deleteIntent = new Intent(this, KillPlayerReceiver.class);
        PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, deleteIntent, 0);

        Notification notification;

        if (track == null) {
            notification = new NotificationCompat.Builder(this)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle("Harmony")
                    .setTicker("Harmony")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setSmallIcon(R.drawable.ic_launcher_white)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .addAction(android.R.drawable.ic_media_play, "", pendingPlayIntent)
                    .setOngoing(true)
                    .setDeleteIntent(pendingDeleteIntent)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setTicker("Harmony")
                    .setContentTitle(track.title)
                    .setContentText(track.artist)
                    .setLargeIcon(image)
                    .setSmallIcon(R.drawable.ic_launcher_white)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .addAction(android.R.drawable.ic_media_previous, "", pendingPreviousIntent)
                    .addAction((isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play), "", pendingPlayIntent)
                    .addAction(android.R.drawable.ic_media_next, "", pendingNextIntent)
                    .setOngoing(isPlaying)
                    .setDeleteIntent(pendingDeleteIntent)
                    .build();
        }

        return notification;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("Destroyed.");
    }
}