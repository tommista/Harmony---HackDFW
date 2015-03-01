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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import timber.log.Timber;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.TrackPlayer;

/**
 * Created by tbrown on 2/28/15.
 */
public class TrackView extends LinearLayout{

    private final BackgroundTarget target = new BackgroundTarget();
    public static TrackView instance;

    private Context context;
    private TextView songTextView;
    private TextView artistTextView;
    private VCRView vcrView;
    private ImageView imageView;
    private ImageView imageBackground;
    private TrackPlayer trackPlayer;
    private TextView shuffleButton;
    private TextView repeatButton;
    private TextView playlistbutton;

    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        instance = this;
        this.context = context;
        trackPlayer = TrackPlayer.getInstance();

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,new IntentFilter("nextTrackIntent"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            HarmonyActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    songTextView = (TextView) findViewById(R.id.song_name);
                    artistTextView = (TextView) findViewById(R.id.artist_name);
                    vcrView = (VCRView) findViewById(R.id.track_vcr);
                    imageView = (ImageView) findViewById(R.id.image_view);
                    imageBackground = (ImageView) findViewById(R.id.image_background);

                    Typeface font = Typeface.createFromAsset(HarmonyActivity.getInstance().getAssets(), "icomoon.ttf");
                    shuffleButton.setTypeface(font);
                    repeatButton.setTypeface(font);

                    shuffleButton.setText("\ue60a");
                    repeatButton.setText("\ue605");

                    shuffleButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            trackPlayer.setShuffle(!trackPlayer.isShuffle);
                            adjustShuffle();
                        }
                    });

                    repeatButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            trackPlayer.setRepeat(!trackPlayer.isRepeat);
                            adjustRepeat();
                        }
                    });

                    songTextView.setText(trackPlayer.getCurrentTrack().title);
                    artistTextView.setText(trackPlayer.getCurrentTrack().artist);

                    Picasso.with(HarmonyActivity.getInstance()).load(trackPlayer.getCurrentTrack().imageURL).into(target);
                    adjustRepeat();
                    adjustShuffle();

                    Picasso.with(HarmonyActivity.getInstance()).load(trackPlayer.getCurrentTrack().imageURL).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Timber.i("Picasso success");
                        }

                        @Override
                        public void onError() {
                            Timber.i("Picasso failure :(");
                        }
                    });

                }
            });

        }
    };

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();


        songTextView = (TextView) this.findViewById(R.id.song_name);
        artistTextView = (TextView) this.findViewById(R.id.artist_name);
        vcrView = (VCRView) this.findViewById(R.id.track_vcr);
        imageView = (ImageView) this.findViewById(R.id.image_view);
        shuffleButton = (TextView) this.findViewById(R.id.shuffle_button);
        repeatButton = (TextView) this.findViewById(R.id.repeat_button);
        playlistbutton = (TextView) this.findViewById(R.id.goto_playlist_button);

        Typeface font = Typeface.createFromAsset(HarmonyActivity.getInstance().getAssets(), "icomoon.ttf");
        shuffleButton.setTypeface(font);
        repeatButton.setTypeface(font);
        playlistbutton.setTypeface(font);

        shuffleButton.setText("\ue60a");
        repeatButton.setText("\ue605");
        playlistbutton.setText("\ue608");

        shuffleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPlayer.setShuffle(!trackPlayer.isShuffle);
                adjustShuffle();
            }
        });

        repeatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPlayer.setRepeat(!trackPlayer.isRepeat);
                adjustRepeat();
            }
        });

        playlistbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HarmonyActivity.getInstance().setContentView(R.layout.playlist_view);
            }
        });

        imageBackground = (ImageView)findViewById(R.id.image_background);

        songTextView.setText(trackPlayer.getCurrentTrack().title);
        songTextView.setHorizontallyScrolling(true);
        songTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songTextView.setFocusableInTouchMode(true);
        songTextView.setFreezesText(true);
        songTextView.setSingleLine(true);
        songTextView.setMarqueeRepeatLimit(-1);
        songTextView.setFocusable(true);
        songTextView.setSelected(true);

        artistTextView.setText(trackPlayer.getCurrentTrack().artist);
        artistTextView.setHorizontallyScrolling(true);
        artistTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        artistTextView.setFocusableInTouchMode(true);
        artistTextView.setFreezesText(true);
        artistTextView.setSingleLine(true);
        artistTextView.setMarqueeRepeatLimit(-1);
        artistTextView.setFocusable(true);
        artistTextView.setSelected(true);

        Picasso.with(context).load(trackPlayer.getCurrentTrack().imageURL).into(target);

        adjustShuffle();
        adjustRepeat();

    }

    private class BackgroundTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Timber.i("Picasso success");
            imageView.setImageBitmap(bitmap);

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

            imageBackground.setImageBitmap(blur);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Timber.i("Picasso failure :(");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    public void adjustShuffle(){

        HarmonyActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(trackPlayer.isShuffle){
                    shuffleButton.setTextColor(context.getResources().getColor(R.color.orange));
                }else{
                    shuffleButton.setTextColor(context.getResources().getColor(R.color.gray));
                }
            }
        });
    }

    public void adjustRepeat(){

        HarmonyActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(trackPlayer.isRepeat){
                    repeatButton.setTextColor(context.getResources().getColor(R.color.orange));
                }else{
                    repeatButton.setTextColor(context.getResources().getColor(R.color.gray));
                }
            }
        });
    }
}
