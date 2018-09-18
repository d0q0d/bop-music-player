package order.android.com.Bop.injector.module;

import dagger.Module;
import dagger.Provides;
import order.android.com.Bop.mvp.contract.SearchContract;
import order.android.com.Bop.mvp.presenter.SearchPresenter;
import order.android.com.Bop.mvp.usecase.GetSearchResult;
import order.android.com.Bop.mvp.repository.Repository;


@Module
public class SearchModule {

    @Provides
    SearchContract.Presenter getSearchPresenter(GetSearchResult getSearchResult) {
        return new SearchPresenter(getSearchResult);
    }

    @Provides
    GetSearchResult getSearchResultUsecase(Repository repository) {
        return new GetSearchResult(repository);
    }
}
