package tommista.com.harmony.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import timber.log.Timber;
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
    private ImageView imageBackground;
    private TrackPlayer trackPlayer;

    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        trackPlayer = TrackPlayer.getInstance();
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();


        songTextView = (TextView) this.findViewById(R.id.song_name);
        artistTextView = (TextView) this.findViewById(R.id.artist_name);
        vcrView = (VCRView) this.findViewById(R.id.track_vcr);
        imageView = (ImageView) this.findViewById(R.id.image_view);

        imageBackground = (ImageView)findViewById(R.id.image_background);

        songTextView.setText(trackPlayer.getCurrentTrack().title);
        songTextView.setHorizontallyScrolling(true);
        songTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songTextView.setFocusableInTouchMode(true);
        songTextView.setFreezesText(true);
        songTextView.setSingleLine(true);
        songTextView.setMarqueeRepeatLimit(-1);
        songTextView.setFocusable(true);
        songTextView.setSelected(true);

        artistTextView.setText(trackPlayer.getCurrentTrack().artist);
        artistTextView.setHorizontallyScrolling(true);
        artistTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        artistTextView.setFocusableInTouchMode(true);
        artistTextView.setFreezesText(true);
        artistTextView.setSingleLine(true);
        artistTextView.setMarqueeRepeatLimit(-1);
        artistTextView.setFocusable(true);
        artistTextView.setSelected(true);

        Picasso.with(context).load(trackPlayer.getCurrentTrack().imageURL).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Timber.i("Picasso success");
                imageView.setImageBitmap(bitmap);

                Bitmap blur = bitmap.copy(bitmap.getConfig(), true);

                final RenderScript rs = RenderScript.create(context);
                final Allocation input = Allocation.createFromBitmap(rs, blur, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
                final Allocation output = Allocation.createTyped(rs, input.getType());
                final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                script.setRadius(25.f /* e.g. 3.f */);
                script.setInput(input);
                script.forEach(output);
                output.copyTo(blur);

                imageBackground.setImageBitmap(blur);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Timber.i("Picasso failure :(");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }
}
