package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.SearchModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.SearchFragment;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = SearchModule.class)
public interface SearchComponent {

    void inject(SearchFragment searchFragment);
}
