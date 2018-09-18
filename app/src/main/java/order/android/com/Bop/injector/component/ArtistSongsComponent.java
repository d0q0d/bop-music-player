package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.ArtistSongModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.ArtistMusicFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ArtistSongModule.class)
public interface ArtistSongsComponent {

    void inject(ArtistMusicFragment artistMusicFragment);
}
