package order.android.com.Bop.mvp.contract;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;


public interface QuickControlsContract {

    interface View extends BaseView {

        Context getContext();

        ImageView getImageView(ImageView imageView);

        TextView getTextView1(TextView textView);

        TextView getTextView2(TextView textView);

        void setAlbumArt(Context c, final ImageView v, Bitmap albumArt);

        void setAlbumArt(Context c, final ImageView v, Drawable albumArt);

        void setTitle(Context c, final TextView v, String title);

        void setArtist(Context c, final TextView v, String artist);

        void setPlayPauseButton(boolean isPlaying);

        boolean getPlayPauseStatus();

        void startUpdateProgress();

        void setProgressMax(int max);

    }

    interface Presenter extends BasePresenter<View> {

        void onPlayPauseClick();

        void onPreviousClick();

        void onNextClick();

        void updateNowPlayingCard();


    }
}
