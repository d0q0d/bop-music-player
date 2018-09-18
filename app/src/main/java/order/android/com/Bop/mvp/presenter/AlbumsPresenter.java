package order.android.com.Bop.mvp.presenter;
import java.util.List;
import order.android.com.Bop.mvp.contract.AlbumsContract;
import order.android.com.Bop.mvp.model.Album;
import order.android.com.Bop.mvp.usecase.GetAlbums;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AlbumsPresenter implements AlbumsContract.Presenter{

    private AlbumsContract.View mView;
    private GetAlbums mUsecase;
    private CompositeSubscription mCompositeSubscription;

    public AlbumsPresenter(GetAlbums getAlbums) {
        mUsecase = getAlbums;
    }

    @Override
    public void attachView(AlbumsContract.View view) {
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
    public void loadAlbums(String action) {
        mCompositeSubscription.clear();
        Subscription subscription = mUsecase.execute(new GetAlbums.RequestValues(action))
                .getSongList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Album>>() {
                    @Override
                    public void call(List<Album> albumList) {
                        if (albumList == null || albumList.size() == 0) {
                            mView.showEmptyView();
                        }else {
                            mView.showAlbums(albumList);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }
}
