package order.android.com.Bop.mvp.contract;

import java.util.List;

import order.android.com.Bop.mvp.model.Artist;
import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;


public interface ArtistContract {

    interface View extends BaseView {

        void showArtists(List<Artist> artists);

        void showEmptyView();
    }

    interface Presenter extends BasePresenter<View> {

        void loadArtists(String action);
    }
}
