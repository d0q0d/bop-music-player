package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.AlbumsContract;
import order.android.com.Bop.mvp.presenter.AlbumsPresenter;
import order.android.com.Bop.mvp.usecase.GetAlbums;
import order.android.com.Bop.mvp.repository.Repository;

@Module
public class AlbumsModule {

    @Provides
    AlbumsContract.Presenter getAlbumsPresenter(GetAlbums getAlbums) {
        return new AlbumsPresenter(getAlbums);
    }

    @Provides
    GetAlbums getAlbumsUsecase(Repository repository) {
        return new GetAlbums(repository);
    }
}
