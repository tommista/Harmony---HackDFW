package tommista.com.harmony.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
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

    private Context context;
    private TextView songTextView;
    private TextView artistTextView;
    private VCRView vcrView;
    private ImageView imageView;
    private TrackPlayer trackPlayer;

    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

                    songTextView.setText(trackPlayer.getCurrentTrack().title);
                    artistTextView.setText(trackPlayer.getCurrentTrack().artist);

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

    }
}
