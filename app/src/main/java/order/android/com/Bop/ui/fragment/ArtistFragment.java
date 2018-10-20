package order.android.com.Bop.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import order.android.com.Bop.BopApp;
import order.android.com.Bop.R;
import order.android.com.Bop.event.FavourateSongEvent;
import order.android.com.Bop.event.MediaUpdateEvent;
import order.android.com.Bop.event.RecentlyPlayEvent;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.ArtistComponent;
import order.android.com.Bop.injector.component.DaggerArtistComponent;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.ArtistsModule;
import order.android.com.Bop.mvp.contract.ArtistContract;
import order.android.com.Bop.mvp.model.Artist;
import order.android.com.Bop.ui.adapter.ArtistAdapter;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.PreferencesUtility;
import order.android.com.Bop.util.RxBus;
import order.android.com.Bop.util.SortOrder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class ArtistFragment extends Fragment implements ArtistContract.View {

    @Inject
    ArtistContract.Presenter mPresenter;
    @BindView(R.id.main_recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.main_view_empty)
    View emptyView;
    private ArtistAdapter mAdapter;
    private RecyclerView.ItemDecoration itemDecoration;
    private String action;
    private PreferencesUtility mPreferences;
    public static ArtistFragment newInstance(String action) {

        Bundle args = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                args.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();
        mPresenter.attachView(this);
        if (getArguments() != null) {
            action = getArguments().getString(Constants.PLAYLIST_TYPE);
        }
        mAdapter = new ArtistAdapter(getActivity(), action);
    }

    private void injectDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication()).getApplicationComponent();
        ArtistComponent artistComponent = DaggerArtistComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .artistsModule(new ArtistsModule())
                .build();
        artistComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mPreferences = PreferencesUtility.getInstance(getActivity());
        mPreferences.setAlbumSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
        recyclerView.setAdapter(mAdapter);
        setItemDecoration();
        recyclerView.setHasFixedSize(true);
        mPresenter.loadArtists(action);
        subscribeMediaUpdateEvent();
        if (Constants.NAVIGATE_PLAYLIST_FAVOURATE.equals(action)) {
            subscribeFavourateSongEvent();
        } else if (Constants.NAVIGATE_PLAYLIST_RECENTPLAY.equals(action)) {
            subscribeRecentlyPlayEvent();
        } else {
            subscribeMediaUpdateEvent();
        }
    }
    private void setItemDecoration() {

        int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_card_album_grid);
        itemDecoration = new AlbumFragment.SpacesItemDecoration(spacingInPixels);

        recyclerView.addItemDecoration(itemDecoration);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        RxBus.getInstance().unSubscribe(this);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void showArtists(List<Artist> artists) {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        mAdapter.setArtistList(artists);
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void subscribeMediaUpdateEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(MediaUpdateEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(new Action1<MediaUpdateEvent>() {
                    @Override
                    public void call(MediaUpdateEvent event) {
                        mPresenter.loadArtists(action);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void subscribeFavourateSongEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(FavourateSongEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FavourateSongEvent>() {
                    @Override
                    public void call(FavourateSongEvent event) {
                        mPresenter.loadArtists(action);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void subscribeRecentlyPlayEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(RecentlyPlayEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RecentlyPlayEvent>() {
                    @Override
                    public void call(RecentlyPlayEvent event) {
                        mPresenter.loadArtists(action);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }
}
