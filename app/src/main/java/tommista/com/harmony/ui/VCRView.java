package tommista.com.harmony.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import tommista.com.harmony.R;

/**
 * Created by tbrown on 2/28/15.
 */
public class VCRView extends LinearLayout{

    private Context context;

    private Button rewindButton;
    private Button playButton;
    private Button forwardButton;

    public VCRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        rewindButton = (Button) this.findViewById(R.id.rewind_button);
        playButton = (Button) this.findViewById(R.id.play_button);
        forwardButton = (Button) this.findViewById(R.id.forward_button);

        rewindButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        forwardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
