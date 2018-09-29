package order.android.com.Bop.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import order.android.com.Bop.BopApp;
import order.android.com.Bop.R;
import order.android.com.Bop.event.PlaylistUpdateEvent;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.DaggerPlaylistComponent;
import order.android.com.Bop.injector.component.PlaylistComponent;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.PlaylistModule;
import order.android.com.Bop.mvp.contract.PlaylistContract;
import order.android.com.Bop.mvp.model.Playlist;
import order.android.com.Bop.ui.adapter.PlaylistAdapter;
import order.android.com.Bop.ui.dialogs.CreatePlaylistDialog;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.RxBus;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class PlaylistFragment extends Fragment implements PlaylistContract.View {

    @Inject
    PlaylistContract.Presenter mPresenter;
    @BindView(R.id.main_recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.main_view_empty)
    View emptyView;
    @BindView(R.id.playlist_new)
    View linearLayout_PlayLists;
    LinearLayout linearLayout;
    private PlaylistAdapter mAdapter;
    private RecyclerView.ItemDecoration itemDecoration;

    public static PlaylistFragment newInstance(String action) {

        Bundle args = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                args.putString(Constants.PLAYLIST_TYPE, action);
                break;
            case Constants.NAVIGATE_PLAYLIST_FAVOURATE:
                args.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        PlaylistFragment fragment = new PlaylistFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();
        mPresenter.attachView(this);

        mAdapter = new PlaylistAdapter(getActivity(), null);

    }


    private void injectDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication()).getApplicationComponent();
        PlaylistComponent playlistComponent = DaggerPlaylistComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .playlistModule(new PlaylistModule())
                .build();
        playlistComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycelerview_playlist, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        linearLayout = new LinearLayout(getActivity());
        mPresenter.subscribe();
        subscribePlaylistUpdateEvent();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        RxBus.getInstance().unSubscribe(this);
    }

    @Override
    public void showPlaylist(List<Playlist> playlists) {
        emptyView.setVisibility(View.GONE);
        mAdapter.setPlaylist(playlists);
    }

    @Override
    public void showEmptyView() {

    }


    private void subscribePlaylistUpdateEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(PlaylistUpdateEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PlaylistUpdateEvent>() {
                    @Override
                    public void call(PlaylistUpdateEvent event) {
                        mPresenter.loadPlaylist();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @OnClick(R.id.playlist_new)
    public void newPlayList() {
        FragmentActivity activity =(getActivity());
        android.support.v4.app.FragmentManager fm =activity.getSupportFragmentManager();
        CreatePlaylistDialog.newInstance().show(fm, "CREATE_PLAYLIST");
    }
}
