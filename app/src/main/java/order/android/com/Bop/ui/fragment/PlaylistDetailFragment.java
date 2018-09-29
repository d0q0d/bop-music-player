package order.android.com.Bop.ui.fragment;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import order.android.com.Bop.BopApp;
import order.android.com.Bop.R;
import order.android.com.Bop.event.MetaChangedEvent;
import order.android.com.Bop.event.PlaylistUpdateEvent;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.DaggerPlaylistSongComponent;
import order.android.com.Bop.injector.component.PlaylistSongComponent;
import order.android.com.Bop.injector.module.PlaylistSongModule;
import order.android.com.Bop.mvp.contract.PlaylistDetailContract;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.ui.adapter.PlaylistSongAdapter;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.RxBus;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistDetailFragment extends Fragment implements PlaylistDetailContract.View {

    @Inject
    PlaylistDetailContract.Presenter mPresenter;
    @BindView(R.id.toolbar_album_detail)
    Toolbar toolbar;
    @BindView(R.id.recyclerView_album_detail)
    FastScrollRecyclerView recyclerView;

    private Context mContext;
    private PlaylistSongAdapter mAdapter;
    private long playlistID = -1;
    private String playlistName;
    private long firstAlbumID = -1;

    public static PlaylistDetailFragment newInstance(long playlistID, String playlistName, boolean useTransition, String transitionName) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.PLAYLIST_ID, playlistID);
        args.putString(Constants.PLAYLIST_NAME, playlistName);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();
        mPresenter.attachView(this);

        if (getArguments() != null) {
            playlistName = getArguments().getString(Constants.PLAYLIST_NAME);
            firstAlbumID = getArguments().getLong(Constants.FIRST_ALBUM_ID);
            playlistID = getArguments().getLong(Constants.PLAYLIST_ID);
        }
        mContext = getActivity();
        mAdapter = new PlaylistSongAdapter(getContext(), playlistID, null);
    }

    private void injectDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication()).getApplicationComponent();
        PlaylistSongComponent playlistSongComponent = DaggerPlaylistSongComponent.builder()
                .applicationComponent(applicationComponent)
                .playlistSongModule(new PlaylistSongModule())
                .build();
        playlistSongComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_album_detail, container, false);
            Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);

        return root;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupToolbar();

        mPresenter.loadPlaylistSongs(playlistID);
        mPresenter.loadPlaylistArt(firstAlbumID);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                RxBus.getInstance().post(new PlaylistUpdateEvent());
                if (positionStart == 0) {
                    List<Song> songs = mAdapter.getSongList();
                    if (songs.size() == 0) {
                        firstAlbumID = -1;
                    } else {
                        firstAlbumID = songs.get(0).albumId;
                    }
                    mPresenter.loadPlaylistArt(firstAlbumID);
                }
            }
        });
        subscribeMetaChangedEvent();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.tab_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_search:
                SearchFragment searchFragment= new SearchFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.cl2, searchFragment,"findThisFragment")
                        .addToBackStack("search_to_fragment_recyclerview")
                        .commit();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(playlistName);
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
    public void showPlaylistSongs(List<Song> songList) {
        mAdapter.setSongList(songList);
    }

}
