package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.SongsModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.SongFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, SongsModule.class})
public interface SongsComponent {

    void inject(SongFragment songsFragment);
}
