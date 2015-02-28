package tommista.com.harmony.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.adapter.PlaylistAdapter;
import tommista.com.harmony.models.Track;

/**
 * Created by tbrown on 2/28/15.
 */
public class PlaylistView  extends LinearLayout{

    private Context context;
    private PlaylistAdapter playlistAdapter;
    private VCRView vcr;
    private Button gotoTrackButton;

    public PlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        final ListView listView = (ListView) this.findViewById(R.id.playlist_list_view);
        vcr = (VCRView) this.findViewById(R.id.playlist_vcr);
        gotoTrackButton = (Button) this.findViewById(R.id.goto_track_button);

        gotoTrackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HarmonyActivity.getInstance().setContentView(R.layout.track_view);
            }
        });

        ArrayList<Track> list = new ArrayList<>();
        list.add(new Track("test", "asdf"));
        list.add(new Track("asdf", "qwer"));

        playlistAdapter = new PlaylistAdapter(context, list);

        listView.setAdapter(playlistAdapter);

    }
}
