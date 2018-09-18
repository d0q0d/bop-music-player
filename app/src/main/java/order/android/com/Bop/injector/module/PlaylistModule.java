package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.PlaylistContract;
import order.android.com.Bop.mvp.presenter.PlaylistPresenter;
import order.android.com.Bop.mvp.usecase.GetAlbums;
import order.android.com.Bop.mvp.usecase.GetPlaylists;
import order.android.com.Bop.mvp.repository.Repository;

@Module
public class PlaylistModule {

    @Provides
    PlaylistContract.Presenter getPlaylistPresenter(GetPlaylists getPlaylists) {
        return new PlaylistPresenter(getPlaylists);
    }

    @Provides
    GetPlaylists getPlaylistsUsecase(Repository repository) {
        return new GetPlaylists(repository);
    }
}
