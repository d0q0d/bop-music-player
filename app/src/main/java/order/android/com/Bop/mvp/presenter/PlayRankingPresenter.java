package order.android.com.Bop.mvp.presenter;

import java.util.List;

import order.android.com.Bop.mvp.contract.PlayRankingContract;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.usecase.GetSongs;
import order.android.com.Bop.util.Constants;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;



public class PlayRankingPresenter implements PlayRankingContract.Presenter {

    private GetSongs mUsecase;
    private PlayRankingContract.View mView;
    private CompositeSubscription mCompositeSubscription;

    public PlayRankingPresenter(GetSongs getSongs) {
        mUsecase = getSongs;
    }

    @Override
    public void attachView(PlayRankingContract.View view) {
        mView = view;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        loadRanking();
    }

    @Override
    public void unsubscribe() {
        mCompositeSubscription.clear();
    }

    @Override
    public void loadRanking() {
        mCompositeSubscription.clear();
        Subscription subscription = mUsecase.execute(new GetSongs.RequestValues(Constants.NAVIGATE_PLAYLIST_TOPPLAYED))
                .getSongList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songList) {
                        if (songList == null || songList.size() == 0) {
                            mView.showEmptyView();
                        } else {
                            mView.showRanking(songList);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }
}
