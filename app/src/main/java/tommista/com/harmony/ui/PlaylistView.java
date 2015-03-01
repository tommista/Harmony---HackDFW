package tommista.com.harmony.ui;

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
import tommista.com.harmony.TrackPlayer;
import tommista.com.harmony.adapter.PlaylistAdapter;
import tommista.com.harmony.managers.PlaylistManager;

/**
 * Created by tbrown on 2/28/15.
 */
public class PlaylistView  extends FrameLayout{

    private Context context;
    private PlaylistAdapter playlistAdapter;
    private VCRView vcr;
    private TextView gotoTrackButton;
    private ImageView imageView;
    private BackgroundTarget target = new BackgroundTarget();

    public PlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LocalBroadcastManager.getInstance(context).registerReceiver(newTrackReceiver,new IntentFilter("newTrackIntent"));
        LocalBroadcastManager.getInstance(context).registerReceiver(nextTrackReceiver,new IntentFilter("nextTrackIntent"));
    }

    private BroadcastReceiver newTrackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            /*HarmonyActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playlistAdapter.notifyDataSetChanged();
                }
            });*/

            HarmonyActivity.getInstance().setContentView(R.layout.playlist_view);

            Timber.i("newTrackReceiver");

        }
    };

    private BroadcastReceiver nextTrackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {

            HarmonyActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String currentTrackUri = TrackPlayer.getInstance().getCurrentTrack().imageURL;

                    Picasso.with(context).load(currentTrackUri).into(target);
                }
            });

        }
    };


    private class BackgroundTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Bitmap blur = bitmap.copy(bitmap.getConfig(), true);

            final RenderScript rs = RenderScript.create(HarmonyActivity.getInstance());
            final Allocation input = Allocation.createFromBitmap(rs, blur, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(25.f /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(blur);

            HarmonyActivity.getInstance().setBitmap(blur);

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
    protected void onFinishInflate(){
        super.onFinishInflate();

        final ListView listView = (ListView) this.findViewById(R.id.playlist_list_view);
        vcr = (VCRView) this.findViewById(R.id.playlist_vcr);
        gotoTrackButton = (TextView) this.findViewById(R.id.goto_track_button);
        imageView = (ImageView) this.findViewById(R.id.image_view);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "icomoon.ttf");
        gotoTrackButton.setTypeface(font);
        gotoTrackButton.setText("\ue60d");

        if(HarmonyActivity.getInstance().bitmap != null){
            imageView.setImageBitmap(HarmonyActivity.getInstance().bitmap);
        }

        gotoTrackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HarmonyActivity.getInstance().setContentView(R.layout.track_view);
            }
        });


        playlistAdapter = new PlaylistAdapter(context, PlaylistManager.getInstance().trackList);

        listView.setAdapter(playlistAdapter);

    }
}
