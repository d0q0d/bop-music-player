package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.ArtistInfoModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.adapter.ArtistAdapter;
import order.android.com.Bop.ui.fragment.ArtistDetailFragment;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ArtistInfoModule.class)
public interface ArtistInfoComponent {

    void injectForAdapter(ArtistAdapter artistAdapter);

    void injectForFragment(ArtistDetailFragment fragment);
}
