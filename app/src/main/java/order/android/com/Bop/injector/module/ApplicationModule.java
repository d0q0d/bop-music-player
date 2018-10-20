package order.android.com.Bop.injector.module;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.BopApp;
import order.android.com.Bop.injector.scope.PerApplication;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.mvp.repository.RepositoryImpl;

@Module
public class ApplicationModule {
    private final BopApp mBopApp;

    public ApplicationModule(BopApp bopApp) {
        this.mBopApp = bopApp;
    }

    @Provides
    @PerApplication
    public BopApp provideAlexaApp() {
        return mBopApp;
    }

    @Provides
    @PerApplication
    public Application provideApplication() {
        return mBopApp;
    }


}
