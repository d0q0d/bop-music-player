package order.android.com.Bop.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.mvp.contract.QuickControlsContract;
import order.android.com.Bop.mvp.usecase.GetLyric;
import order.android.com.Bop.util.BopUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class QuickControlsPresenter implements QuickControlsContract.Presenter ,QuickControlsContract.View{

    private GetLyric mGetLyric;
    private CompositeSubscription mCompositeSubscription;
    private QuickControlsContract.View mView;
    Context context;
    ImageView imageView1;
    TextView textView1;
    TextView textView2;

    private boolean mDuetoplaypause = false;

    public QuickControlsPresenter(GetLyric getLyric) {
        this.mGetLyric = getLyric;
    }

    @Override
    public void attachView(QuickControlsContract.View view) {
        this.mView = view;
        mCompositeSubscription = new CompositeSubscription();

    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mCompositeSubscription.clear();
    }

    @Override
    public void onPlayPauseClick() {
        mDuetoplaypause = true;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(final Void... unused) {
                boolean isPlaying = MusicPlayer.isPlaying();
                MusicPlayer.playOrPause();
                return isPlaying;
            }

            @Override
            protected void onPostExecute(Boolean isPlaying) {
                if (isPlaying) {
                    mView.setPlayPauseButton(false);
                } else {
                    mView.setPlayPauseButton(true);
                }
            }
        }.execute();

    }

    @Override
    public void onPreviousClick() {
        MusicPlayer.previous(mView.getContext(), true);
    }

    @Override
    public void loadLyric() {
        mCompositeSubscription.clear();
        String title = MusicPlayer.getTrackName();
        String artist = MusicPlayer.getArtistName();
        long duration = MusicPlayer.duration();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(artist)) {
            return;
        }
        Subscription subscription = mGetLyric.execute(new GetLyric.RequestValues(title, artist, duration))
                .getLyricFile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showLyric(null);
                    }

                    @Override
                    public void onNext(File file) {
                        if (file == null) {
                            mView.showLyric(null);
                        } else {
                            mView.showLyric(file);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onNextClick() {
        MusicPlayer.next();
    }


    @Override
    public void updateNowPlayingCard() {
        if (MusicPlayer.isPlaying()) {
            if (!mView.getPlayPauseStatus()) {
                mView.setPlayPauseButton(true);
            }
        } else {
            if (mView.getPlayPauseStatus()) {
                mView.setPlayPauseButton(false);
            }
        }

        final String title = MusicPlayer.getTrackName();
        final String artist = MusicPlayer.getArtistName();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(artist)) {
            mView.setTitle(getContext(),textView1,mView.getContext().getResources().getString(R.string.app_name));
            mView.setArtist(getContext(),textView2,"");
        } else {
            mView.setTitle(getContext(),textView1,title);
            mView.setArtist(getContext(),textView2,artist);
        }

        if (!mDuetoplaypause) {
            Glide.with(mView.getContext())
                    .load(BopUtil.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString())
                    .asBitmap()
                    .error(R.drawable.defult_album_art)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            mView.setAlbumArt(getContext(),imageView1,errorDrawable);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mView.setAlbumArt(getContext(),imageView1,resource);
                        }
                    });
        }

        mDuetoplaypause = false;
        mView.setProgressMax((int) MusicPlayer.duration());
        mView.startUpdateProgress();
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public ImageView getImageView(ImageView imageView) {
        imageView1=imageView;
        return imageView;
    }

    @Override
    public TextView getTextView1(TextView textView) {
        textView1=textView;
        return textView;
    }
    @Override
    public TextView getTextView2(TextView textView) {
        textView2=textView;
        return textView;
    }

    @Override
    public void setAlbumArt(Context c, ImageView v, Bitmap albumArt) {

    }

    @Override
    public void setAlbumArt(Context c, ImageView v, Drawable albumArt) {

    }

    @Override
    public void setTitle(Context c, TextView v, String title) {

    }

    @Override
    public void setArtist(Context c, TextView v, String artist) {

    }

    @Override
    public void setPlayPauseButton(boolean isPlaying) {

    }

    @Override
    public boolean getPlayPauseStatus() {
        return false;
    }

    @Override
    public void setPalette(Palette palette) {

    }

    @Override
    public void showLyric(File file) {

    }

    @Override
    public void startUpdateProgress() {

    }

    @Override
    public void setProgressMax(int max) {

    }
}
