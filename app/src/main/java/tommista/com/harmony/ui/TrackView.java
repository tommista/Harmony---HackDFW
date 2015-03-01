package tommista.com.harmony.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import timber.log.Timber;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.TrackPlayer;

/**
 * Created by tbrown on 2/28/15.
 */
public class TrackView extends LinearLayout{

    public static TrackView instance;

    private Context context;
    private TextView songTextView;
    private TextView artistTextView;
    private VCRView vcrView;
    private ImageView imageView;
    private TrackPlayer trackPlayer;
    private TextView shuffleButton;
    private TextView repeatButton;

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

        Picasso.with(context).load(trackPlayer.getCurrentTrack().imageURL).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Timber.i("Picasso success");
            }

            @Override
            public void onError() {
                Timber.i("Picasso failure :(");
            }
        });

        adjustShuffle();
        adjustRepeat();

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
