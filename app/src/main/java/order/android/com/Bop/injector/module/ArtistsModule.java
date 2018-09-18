package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.ArtistContract;
import order.android.com.Bop.mvp.presenter.ArtistPresenter;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.mvp.usecase.GetArtists;

@Module
public class ArtistsModule {

    @Provides
    ArtistContract.Presenter getArtistPresenter(GetArtists getArtists) {
        return new ArtistPresenter(getArtists);
    }

    @Provides
    GetArtists getArtistsUsecase(Repository repository) {
        return new GetArtists(repository);
    }
}
