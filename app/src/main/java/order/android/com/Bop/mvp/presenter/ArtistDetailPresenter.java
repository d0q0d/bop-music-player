package order.android.com.Bop.mvp.presenter;

import order.android.com.Bop.mvp.contract.ArtistDetailContract;
import rx.subscriptions.CompositeSubscription;


public class ArtistDetailPresenter implements ArtistDetailContract.Presenter {

    private ArtistDetailContract.View mView;
    private CompositeSubscription mCompositeSubscription;

    public ArtistDetailPresenter() {

    }

    @Override
    public void attachView(ArtistDetailContract.View view) {
        mView = view;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        throw new RuntimeException("please call subscribe(long artistID)");
    }

    @Override
    public void subscribe(long artistID) {
        loadArtistArt(artistID);
    }

    @Override
    public void loadArtistArt(long artistID) {

    }

    @Override
    public void unsubscribe() {
        mCompositeSubscription.clear();
    }


}
