package tommista.com.harmony.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

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

    public PlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        final ListView listView = (ListView) this.findViewById(R.id.playlist_list_view);
        //vcr = (VCRView) this.findViewById(R.id.playlist_vcr);

        ArrayList<Track> list = new ArrayList<>();
        list.add(new Track("test", "asdf"));
        list.add(new Track("asdf", "qwer"));

        playlistAdapter = new PlaylistAdapter(context, list);

        listView.setAdapter(playlistAdapter);

    }
}
