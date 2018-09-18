package order.android.com.Bop.injector.module;

import android.app.Activity;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.injector.scope.PerActivity;

@Module
public class ActivityModule {
    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @PerActivity
    public Context provideContext(){
        return mActivity;
    }
}
