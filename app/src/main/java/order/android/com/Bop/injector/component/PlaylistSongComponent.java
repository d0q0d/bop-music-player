package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.PlaylistSongModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.PlaylistDetailFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = PlaylistSongModule.class)
public interface PlaylistSongComponent {

    void inject(PlaylistDetailFragment playlistDetailFragment);
}
