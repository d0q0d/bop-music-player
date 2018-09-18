package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;

import order.android.com.Bop.mvp.contract.ArtistDetailContract;
import order.android.com.Bop.mvp.presenter.ArtistDetailPresenter;


@Module
public class ArtistInfoModule {

    @Provides
    ArtistDetailContract.Presenter getArtistDetailPresenter() {
        return new ArtistDetailPresenter();
    }
}
