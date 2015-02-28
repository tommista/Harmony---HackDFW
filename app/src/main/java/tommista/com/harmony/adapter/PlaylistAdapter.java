package tommista.com.harmony.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import timber.log.Timber;
import tommista.com.harmony.R;
import tommista.com.harmony.models.Track;

/**
 * Created by tbrown on 2/28/15.
 */
public class PlaylistAdapter extends ArrayAdapter<Track> {


    public PlaylistAdapter(Context context, ArrayList<Track> tracks) {
        super(context, R.layout.playlist_track , tracks);
        Timber.i("asdf " + tracks.size());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Track track = getItem(position);

        Timber.i("Track: %s Artist: %s", track.title, track.artist);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.playlist_track, null);
        }

        Timber.i("convert view is null: " + (convertView == null));

        TextView songName = (TextView) convertView.findViewById(R.id.track_name);
        TextView artistName = (TextView) convertView.findViewById(R.id.artist_name);

        Timber.i("Track: %s Artist: %s", track.title, track.artist);

        songName.setText(track.title);
        artistName.setText(track.artist);

        return convertView;
    }
}
