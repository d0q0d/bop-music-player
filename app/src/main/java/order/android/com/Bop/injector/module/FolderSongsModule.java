package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.FolderSongsContract;
import order.android.com.Bop.mvp.presenter.FolderSongsPresenter;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.mvp.usecase.GetFolderSongs;

@Module
public class FolderSongsModule {

    @Provides
    GetFolderSongs getFolderSongsUsecase(Repository repository) {
        return new GetFolderSongs(repository);
    }

    @Provides
    FolderSongsContract.Presenter getFolderSongsPresenter(GetFolderSongs getFolderSongs) {
        return new FolderSongsPresenter(getFolderSongs);
    }
}
