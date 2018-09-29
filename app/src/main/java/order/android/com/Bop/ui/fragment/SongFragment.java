
package order.android.com.Bop.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

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
import order.android.com.Bop.event.MetaChangedEvent;
import order.android.com.Bop.event.RecentlyPlayEvent;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.DaggerSongsComponent;
import order.android.com.Bop.injector.component.SongsComponent;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.SongsModule;
import order.android.com.Bop.mvp.contract.SongsContract;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.ui.adapter.SongsListAdapter;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.PreferencesUtility;
import order.android.com.Bop.util.RxBus;
import order.android.com.Bop.util.SortOrder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SongFragment extends Fragment implements SongsContract.View {
    @Inject
    SongsContract.Presenter mPresenter;
    @BindView(R.id.main_recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.main_view_empty)
    ViewStub emptyView;
    private SongsListAdapter mAdapter;
    private PreferencesUtility mPreferences;
    private String action;


    public static SongFragment newInstance(String action) {

        Bundle args = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                args.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        SongFragment fragment = new SongFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();
        mPresenter.attachView(this);
        mPreferences = PreferencesUtility.getInstance(getActivity());
        if (getArguments() != null) {
            action = getArguments().getString(Constants.PLAYLIST_TYPE);
        }

        mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), null, action, true);
    }


    public void injectDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication()).getApplicationComponent();
        SongsComponent songsComponent = DaggerSongsComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .songsModule(new SongsModule())
                .build();
        songsComponent.inject(this);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST);
        mPresenter.loadSongs(action);
        subscribeMediaUpdateEvent();
        subscribeMetaChangedEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        RxBus.getInstance().unSubscribe(this);
    }

    private void subscribeMetaChangedEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(MetaChangedEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<MetaChangedEvent>() {
                    @Override
                    public void call(MetaChangedEvent event) {
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void showSongs(List<Song> songList) {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        mAdapter.setSongList(songList);
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
                        mPresenter.loadSongs(action);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

}
