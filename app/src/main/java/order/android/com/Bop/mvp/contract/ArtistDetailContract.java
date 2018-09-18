package order.android.com.Bop.mvp.contract;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;

public interface ArtistDetailContract {

    interface View extends BaseView {

        Context getContext();

        void showArtistArt(Bitmap bitmap);

        void showArtistArt(Drawable drawable);

    }

    interface Presenter extends BasePresenter<View> {

        void subscribe(long artistID);

        void loadArtistArt(long artistID);
    }

}
