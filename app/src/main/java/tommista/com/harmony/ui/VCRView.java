package tommista.com.harmony.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import timber.log.Timber;
import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.TrackPlayer;

/**
 * Created by tbrown on 2/28/15.
 */
public class VCRView extends LinearLayout{

    public static VCRView instance;


    private Context context;

    private TextView rewindButton;
    private TextView playButton;
    private TextView forwardButton;

    private TrackPlayer trackPlayer;

    public VCRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        instance = this;
        this.context = context;
        trackPlayer = TrackPlayer.getInstance();
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        Timber.i("vcr inflated");

        rewindButton = (TextView) this.findViewById(R.id.rewind_button);
        playButton = (TextView) this.findViewById(R.id.play_button);
        forwardButton = (TextView) this.findViewById(R.id.forward_button);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "icomoon.ttf");
        rewindButton.setTypeface(font);
        playButton.setTypeface(font);
        forwardButton.setTypeface(font);

        rewindButton.setText("\ue600");
        //playButton.setText("\ue603");

        adjustPlayPause();

        forwardButton.setText("\ue601");


        rewindButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPlayer.previousTrack();
            }
        });

        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPlayer.playPauseTrack();
            }
        });

        forwardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPlayer.nextTrack();
            }
        });
    }

    public void adjustPlayPause(){

        HarmonyActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(trackPlayer.isPlaying()){
                        playButton.setText("\ue602");
                    }else{
                        playButton.setText("\ue603");
                    }
                }
            });
    }

}
