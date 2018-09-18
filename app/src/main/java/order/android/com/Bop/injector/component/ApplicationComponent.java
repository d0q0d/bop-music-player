package order.android.com.Bop.injector.component;

import android.app.Application;

import dagger.Component;
import order.android.com.Bop.BopApp;
import order.android.com.Bop.injector.module.ApplicationModule;
import order.android.com.Bop.injector.scope.PerApplication;
import order.android.com.Bop.mvp.repository.Repository;


@PerApplication
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    Application application();

    BopApp listenerApplication();

    Repository repository();
}
