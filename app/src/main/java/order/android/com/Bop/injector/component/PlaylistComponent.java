package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.PlaylistModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.PlaylistFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class,PlaylistModule.class})
public interface PlaylistComponent {

    void inject(PlaylistFragment playlistFragment);
}
