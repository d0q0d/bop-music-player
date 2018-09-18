package order.android.com.Bop.mvp.contract;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.util.List;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;

public interface AlbumDetailContract {

    interface View extends BaseView {

        Context getContext();

        void showAlbumSongs(List<Song> songList);

    }

    interface Presenter extends BasePresenter<View> {

        void subscribe(long albumID);

        void loadAlbumSongs(long albumID);

        void loadAlbumArt(long albumID);
    }
}
