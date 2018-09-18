package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.PlaylistDetailContract;
import order.android.com.Bop.mvp.presenter.PlaylistDetailPresenter;
import order.android.com.Bop.mvp.usecase.GetPlaylistSongs;
import order.android.com.Bop.mvp.repository.Repository;


@Module
public class PlaylistSongModule {

    @Provides
    GetPlaylistSongs getPlaylistSongsUsecase(Repository repository) {
        return new GetPlaylistSongs(repository);
    }

    @Provides
    PlaylistDetailContract.Presenter getPlaylistDetailPresenter(GetPlaylistSongs getPlaylistSongs) {
        return new PlaylistDetailPresenter(getPlaylistSongs);
    }
}
