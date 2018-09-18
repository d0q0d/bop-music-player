package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.PlayqueueSongContract;
import order.android.com.Bop.mvp.presenter.PlayqueueSongPresenter;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.mvp.usecase.GetSongs;


@Module
public class PlayqueueSongModule {

    @Provides
    GetSongs getSongsUsecase(Repository repository) {
        return new GetSongs(repository);
    }

    @Provides
    PlayqueueSongContract.Presenter getPlayqueueSongUsecase(GetSongs getSongs) {
        return new PlayqueueSongPresenter(getSongs);
    }
}
