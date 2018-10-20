package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;

import order.android.com.Bop.mvp.contract.ArtistDetailContract;
import order.android.com.Bop.mvp.presenter.ArtistDetailPresenter;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.mvp.usecase.GetArtistInfo;


@Module
public class ArtistInfoModule {

    @Provides
    GetArtistInfo getArtistInfoUsecase(Repository repository) {
        return new GetArtistInfo(repository);
    }


    @Provides
    ArtistDetailContract.Presenter getArtistDetailPresenter() {
        return new ArtistDetailPresenter();
    }
}
