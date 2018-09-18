package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.AlbumsModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.AlbumFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, AlbumsModule.class})
public interface AlbumsComponent {

    void inject(AlbumFragment albumFragment);
}
