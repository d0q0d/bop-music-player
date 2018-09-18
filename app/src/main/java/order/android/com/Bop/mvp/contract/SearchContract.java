package order.android.com.Bop.mvp.contract;

import java.util.List;

import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;


public interface SearchContract {

    interface View extends BaseView {

        void showSearchResult(List<Object> list);

        void showEmptyView();
    }

    interface Presenter extends BasePresenter<View> {

        void search(String queryString);
    }

}
