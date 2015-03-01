package tommista.com.harmony;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tbrown on 3/1/15.
 */
@Module(
        includes = {
        },
        injects = {
                HarmonyApp.class
        },
        complete = false,
        library = true
)

public class HarmonyModule {
    private final HarmonyApp app;

    public HarmonyModule(HarmonyApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }

}