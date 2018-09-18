package order.android.com.Bop.mvp.contract;

import java.util.List;

import order.android.com.Bop.mvp.model.Album;
import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;


public interface AlbumsContract {

    interface View extends BaseView{
        void showAlbums(List<Album> albumList);

        void showEmptyView();
    }

    interface Presenter extends BasePresenter<View>{

        void loadAlbums(String action);


    }
}
