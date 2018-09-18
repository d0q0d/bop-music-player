package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.ArtistsModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.ArtistFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, ArtistsModule.class})
public interface ArtistComponent {

    void inject(ArtistFragment artistFragment);
}
