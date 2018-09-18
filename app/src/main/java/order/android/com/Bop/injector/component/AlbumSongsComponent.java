package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.AlbumSongsModel;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.AlbumDetailFragment;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = AlbumSongsModel.class)
public interface AlbumSongsComponent {

    void inject(AlbumDetailFragment albumDetailFragment);

}
