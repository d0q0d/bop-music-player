package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.FolderModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.FoldersFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = FolderModule.class)
public interface FoldersComponent {

    void inject(FoldersFragment foldersFragment);
}
