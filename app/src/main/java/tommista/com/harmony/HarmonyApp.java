package tommista.com.harmony;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

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

        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        buildObjectGraphAndInject();
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(new HarmonyModule(this));
        objectGraph.inject(this);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}
