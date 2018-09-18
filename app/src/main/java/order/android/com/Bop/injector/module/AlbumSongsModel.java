package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.AlbumDetailContract;
import order.android.com.Bop.mvp.presenter.AlbumDetailPresenter;
import order.android.com.Bop.mvp.usecase.GetAlbumSongs;
import order.android.com.Bop.mvp.repository.Repository;


@Module
public class AlbumSongsModel {

    @Provides
    GetAlbumSongs getAlbumSongUsecase(Repository repository) {
        return new GetAlbumSongs(repository);
    }

    @Provides
    AlbumDetailContract.Presenter getAlbumDetailPresenter(GetAlbumSongs getAlbumSongs) {
        return new AlbumDetailPresenter(getAlbumSongs);
    }
}
