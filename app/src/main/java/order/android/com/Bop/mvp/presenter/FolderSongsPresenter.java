package order.android.com.Bop.mvp.presenter;

import java.util.List;

import order.android.com.Bop.mvp.contract.FolderSongsContract;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.usecase.GetFolderSongs;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FolderSongsPresenter implements FolderSongsContract.Presenter{

    private GetFolderSongs mUsecase;
    private FolderSongsContract.View mView;
    private CompositeSubscription mCompositeSubscription;

    public FolderSongsPresenter(GetFolderSongs getFolderSongs) {
        mUsecase = getFolderSongs;
    }

    @Override
    public void attachView(FolderSongsContract.View view) {
        mView = view;
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
    public void loadSongs(String path) {
        mCompositeSubscription.clear();
        Subscription subscription = mUsecase.execute(new GetFolderSongs.RequestValues(path))
                .getSongList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songList) {
                        mView.showSongs(songList);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

}
