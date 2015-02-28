package tommista.com.harmony.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import tommista.com.harmony.R;
import tommista.com.harmony.models.Track;

/**
 * Created by tbrown on 2/28/15.
 */
public class PlaylistAdapter extends ArrayAdapter<Track> {


    public PlaylistAdapter(Context context, ArrayList<Track> tracks) {
        super(context, R.layout.playlist_track , tracks);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Track track = getItem(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.playlist_track, null);
        }

        /*TextView handle_text= (TextView) convertView.findViewById(R.id.handle_text);
        Button button = (Button) convertView.findViewById(R.id.del_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handles.remove(handle);
                notifyDataSetChanged();
            }
        });
        handle_text.setText(handle.getTwitterHandle());*/
        return convertView;
    }
}
