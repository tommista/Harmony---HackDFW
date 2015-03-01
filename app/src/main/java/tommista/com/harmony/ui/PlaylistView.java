package tommista.com.harmony.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.adapter.PlaylistAdapter;
import tommista.com.harmony.managers.PlaylistManager;

/**
 * Created by tbrown on 2/28/15.
 */
public class PlaylistView  extends LinearLayout{

    private Context context;
    private PlaylistAdapter playlistAdapter;
    private VCRView vcr;
    private TextView gotoTrackButton;

    public PlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        final ListView listView = (ListView) this.findViewById(R.id.playlist_list_view);
        vcr = (VCRView) this.findViewById(R.id.playlist_vcr);
        gotoTrackButton = (TextView) this.findViewById(R.id.goto_track_button);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "icomoon.ttf");
        gotoTrackButton.setTypeface(font);
        gotoTrackButton.setText("\ue608");

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
