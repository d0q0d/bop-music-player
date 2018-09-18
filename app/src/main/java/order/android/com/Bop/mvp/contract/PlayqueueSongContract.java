package order.android.com.Bop.mvp.contract;

import android.content.Context;

import java.util.List;

import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;



public interface PlayqueueSongContract {

    interface View extends BaseView {

        Context getContext();

        void showSongs(List<Song> songs);

        void showEmptyView();

    }

    interface Presenter extends BasePresenter<View> {

        void loadSongs();
    }
}
