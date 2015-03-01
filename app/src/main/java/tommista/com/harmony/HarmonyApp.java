package tommista.com.harmony;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by tbrown on 3/1/15.
 */
public class HarmonyApp extends Application{

    private ObjectGraph objectGraph;

    public static HarmonyApp get(Context context) {
        return (HarmonyApp) context.getApplicationContext();
    }

    @Override public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO eventually put remote logging into a tree and put here.
        }

        buildObjectGraphAndInject();
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(new HarmonyModule(this));
        objectGraph.inject(this);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}
