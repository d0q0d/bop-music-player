package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.SongsContract;
import order.android.com.Bop.mvp.presenter.SongsPresenter;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.mvp.usecase.GetSongs;

@Module
public class SongsModule {

    @Provides
    SongsContract.Presenter getSongsPresenter(GetSongs getSongs) {
        return new SongsPresenter(getSongs);
    }

    @Provides
    GetSongs getSongsUsecase(Repository repository) {
        return new GetSongs(repository);
    }
}
