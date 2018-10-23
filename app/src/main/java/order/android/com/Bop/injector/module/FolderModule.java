package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.FoldersContract;
import order.android.com.Bop.mvp.presenter.FolderPresenter;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.mvp.usecase.GetFolders;


@Module
public class FolderModule {

    @Provides
    GetFolders getFoldersUsecase(Repository repository) {
        return new GetFolders(repository);
    }

    @Provides
    FoldersContract.Presenter getFoldersPresenter(GetFolders getFolders) {
        return new FolderPresenter(getFolders);
    }
}
