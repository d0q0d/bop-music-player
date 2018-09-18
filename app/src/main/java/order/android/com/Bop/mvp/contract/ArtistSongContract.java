package order.android.com.Bop.mvp.contract;

import java.util.List;

import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;



public interface ArtistSongContract {

    interface View extends BaseView {

        void showSongs(List<Song> songList);
    }

    interface Presenter extends BasePresenter<View> {

        void subscribe(long artistID);

        void loadSongs(long artistID);
    }

}
