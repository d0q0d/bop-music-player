package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.QuickControlsModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.QuickControlsFragment;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, QuickControlsModule.class})
public interface QuickControlsComponent {

    void inject(QuickControlsFragment quickControlsFragment);

}
