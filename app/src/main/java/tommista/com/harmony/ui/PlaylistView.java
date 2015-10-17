package tommista.com.harmony.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import timber.log.Timber;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.models.Track;
import tommista.com.harmony.tracks.TrackPlayer;
import tommista.com.harmony.adapter.PlaylistAdapter;
import tommista.com.harmony.managers.PlaylistManager;

/**
 * Created by tbrown on 2/28/15.
 */
public class PlaylistView extends FrameLayout {

    private final BroadcastReceiver nextTrackReceiver;
    private final BroadcastReceiver newTrackReceiver;
    private HarmonyActivity context;
    private PlaylistAdapter playlistAdapter;
    private VCRView vcr;
    private TextView gotoTrackButton;
    private ImageView imageView;
    private RenderScript rs;
    private ScriptIntrinsicBlur script;
    private BackgroundTarget target = new BackgroundTarget();

    public PlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (HarmonyActivity) context;

        rs = RenderScript.create(context);
        script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        newTrackReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                PlaylistView.this.context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playlistAdapter.notifyDataSetChanged();
                    }
                });

                PlaylistView.this.context.setContentView(R.layout.playlist_view);

                Timber.i("newTrackReceiver");

            }
        };

        nextTrackReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                final Track track = TrackPlayer.getInstance().getCurrentTrack();

                PlaylistView.this.context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String currentTrackUri = track.imageURL;

                        Picasso.with(context).load(currentTrackUri).into(target);
                    }
                });

            }
        };


        LocalBroadcastManager.getInstance(context).registerReceiver(newTrackReceiver, new IntentFilter("newTrackIntent"));
        LocalBroadcastManager.getInstance(context).registerReceiver(nextTrackReceiver, new IntentFilter("nextTrackIntent"));
    }


    private class BackgroundTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Bitmap blur = bitmap.copy(bitmap.getConfig(), true);

            final Allocation input = Allocation.createFromBitmap(rs, blur, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            script.setRadius(25.f /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(blur);

            TrackPlayer.getInstance().setBitmap(blur);

            imageView.setImageBitmap(blur);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Timber.i("Picasso failure :(");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final ListView listView = (ListView) this.findViewById(R.id.playlist_list_view);
        vcr = (VCRView) this.findViewById(R.id.playlist_vcr);
        gotoTrackButton = (TextView) this.findViewById(R.id.goto_track_button);
        imageView = (ImageView) this.findViewById(R.id.image_view);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "icomoon.ttf");
        gotoTrackButton.setTypeface(font);
        gotoTrackButton.setText("\ue60d");

        if (TrackPlayer.getInstance().getBitmap() != null) {
            imageView.setImageBitmap(TrackPlayer.getInstance().getBitmap());
        }

        gotoTrackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistView.this.context.setContentView(R.layout.track_view);
            }
        });


        playlistAdapter = new PlaylistAdapter(context, PlaylistManager.getInstance().trackList);

        listView.setAdapter(playlistAdapter);

    }

    @Override
    protected void onDetachedFromWindow() {

        Timber.i("Detached");
        imageView.setImageBitmap(null);

        LocalBroadcastManager.getInstance(context).unregisterReceiver(newTrackReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(nextTrackReceiver);

        super.onDetachedFromWindow();
    }
}
