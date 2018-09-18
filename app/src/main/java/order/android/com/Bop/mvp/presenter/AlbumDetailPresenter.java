package order.android.com.Bop.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import order.android.com.Bop.R;
import order.android.com.Bop.mvp.contract.AlbumDetailContract;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.usecase.GetAlbumSongs;
import order.android.com.Bop.util.BopUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class AlbumDetailPresenter implements AlbumDetailContract.Presenter {

    private GetAlbumSongs mUsecase;
    private AlbumDetailContract.View mView;
    private CompositeSubscription mCompositeSubscription;

    public AlbumDetailPresenter(GetAlbumSongs getAlbumSongs) {
        mUsecase = getAlbumSongs;
    }

    @Override
    public void subscribe(long albumID) {
        loadAlbumArt(albumID);
        loadAlbumSongs(albumID);
    }

    @Override
    public void attachView(AlbumDetailContract.View view) {
        mView = view;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        throw new RuntimeException("please call subscribe(long albumID)");
    }

    @Override
    public void unsubscribe() {
        mCompositeSubscription.clear();
    }

    @Override
    public void loadAlbumSongs(long albumID) {
        mCompositeSubscription.clear();
        Subscription subscription = mUsecase.execute(new GetAlbumSongs.RequestValues(albumID))
                .getSongList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Song>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Song> songList) {
                        mView.showAlbumSongs(songList);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void loadAlbumArt(long albumID) {
        Glide.with(mView.getContext())
                .load(BopUtil.getAlbumArtUri(albumID))
                .asBitmap()
                .animate(android.R.anim.fade_in)
                .priority(Priority.IMMEDIATE)
                .error(R.drawable.defult_album_art)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    }
                });
    }
}
