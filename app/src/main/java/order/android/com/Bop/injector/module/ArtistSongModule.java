package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;

import order.android.com.Bop.mvp.contract.ArtistSongContract;
import order.android.com.Bop.mvp.presenter.ArtistSongPresenter;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.mvp.usecase.GetArtistSongs;

@Module
public class ArtistSongModule {

    @Provides
    GetArtistSongs getArtistSongsUsecase(Repository repository) {
        return new GetArtistSongs(repository);
    }

    @Provides
    ArtistSongContract.Presenter getArtistSongPresenter(GetArtistSongs getArtistSongs) {
        return new ArtistSongPresenter(getArtistSongs);
    }
}
