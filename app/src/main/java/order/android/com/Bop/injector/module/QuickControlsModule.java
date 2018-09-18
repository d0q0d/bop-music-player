package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.QuickControlsContract;
import order.android.com.Bop.mvp.presenter.QuickControlsPresenter;

@Module
public class QuickControlsModule {

    @Provides
    QuickControlsContract.Presenter getQuickControlsPresenter() {
        return new QuickControlsPresenter();
    }
}
