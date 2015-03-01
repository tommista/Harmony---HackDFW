package tommista.com.harmony.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tommista.com.harmony.HarmonyActivity;
import tommista.com.harmony.R;
import tommista.com.harmony.TrackPlayer;
import tommista.com.harmony.managers.PlaylistManager;
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

        View trackItem = (View) convertView.findViewById(R.id.track_item);
        TextView songName = (TextView) convertView.findViewById(R.id.track_name);
        TextView artistName = (TextView) convertView.findViewById(R.id.artist_name);

        trackItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackPlayer.getInstance().playTrack(position);
                HarmonyActivity.getInstance().setContentView(R.layout.track_view);
            }
        });

        trackItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Create custom alert dialog confirming deletion.
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HarmonyActivity.getInstance());
                alertDialogBuilder.setTitle("Remove Song");

                if (TrackPlayer.getInstance().getPlayingIndex() == position) {
                    alertDialogBuilder.setMessage("Sorry, you cannot remove a song that is currently playing.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                } else {
                    alertDialogBuilder.setMessage("Are you sure you would like to remove " + track.title +"?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PlaylistManager.getInstance().deleteTrack(position);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                }

                alertDialogBuilder.create().show();

                return true;
            }
        });

        songName.setText(track.title);
        artistName.setText(track.artist);

        return convertView;
    }
}
