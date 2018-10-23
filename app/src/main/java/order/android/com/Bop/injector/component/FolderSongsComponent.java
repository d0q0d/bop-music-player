package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.FolderSongsModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.fragment.FolderSongsFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = FolderSongsModule.class)
public interface FolderSongsComponent {

    void inject(FolderSongsFragment folderSongsFragment);
}
