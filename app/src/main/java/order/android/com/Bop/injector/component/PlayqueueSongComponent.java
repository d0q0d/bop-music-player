package order.android.com.Bop.injector.component;

import dagger.Component;
import order.android.com.Bop.injector.module.PlayqueueSongModule;
import order.android.com.Bop.injector.scope.PerActivity;
import order.android.com.Bop.ui.dialogs.PlayqueueDialog;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = PlayqueueSongModule.class)
public interface PlayqueueSongComponent {

    void inject(PlayqueueDialog playqueueDialog);
}
