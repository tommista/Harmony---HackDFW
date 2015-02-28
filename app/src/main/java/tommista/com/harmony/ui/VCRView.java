package tommista.com.harmony.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by tbrown on 2/28/15.
 */
public class VCRView extends LinearLayout{

    private Context context;

    public VCRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
    }

}
