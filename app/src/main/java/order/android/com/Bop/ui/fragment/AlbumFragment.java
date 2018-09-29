package order.android.com.Bop.ui.fragment;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import order.android.com.Bop.event.MediaUpdateEvent;
import order.android.com.Bop.injector.component.AlbumsComponent;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.DaggerAlbumsComponent;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.AlbumsModule;
import order.android.com.Bop.mvp.contract.AlbumsContract;
import order.android.com.Bop.mvp.model.Album;
import order.android.com.Bop.ui.adapter.AlbumAdapter;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.PreferencesUtility;
import order.android.com.Bop.util.RxBus;
import order.android.com.Bop.util.SortOrder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AlbumFragment extends Fragment implements AlbumsContract.View {


    @Inject
    AlbumsContract.Presenter mPresenter;
    @BindView(R.id.main_recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.main_view_empty)
    View emptyView;
    private AlbumAdapter mAdapter;
    private RecyclerView.ItemDecoration itemDecoration;
    private PreferencesUtility mPreferences;
    private GridLayoutManager gridLayoutManager;
    private String action;

    public static AlbumFragment newInstance(String action) {

        Bundle args = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                args.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        AlbumFragment fragment = new AlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();
        mPresenter.attachView(this);
        gridLayoutManager=new GridLayoutManager(getActivity(),2);
        mPreferences = PreferencesUtility.getInstance(getActivity());
        if (getArguments() != null) {
            action = getArguments().getString(Constants.PLAYLIST_TYPE);
        }
        mAdapter = new AlbumAdapter(getActivity(),action);

    }

    private void injectDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication()).getApplicationComponent();
        AlbumsComponent albumsComponent = DaggerAlbumsComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .albumsModule(new AlbumsModule())
                .build();
        albumsComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);
        setItemDecoration();
        mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z);
        mPresenter.loadAlbums(action);
        subscribeMediaUpdateEvent();

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
    public void showAlbums(List<Album> albumList) {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        mAdapter.setAlbumsList(albumList);
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void setItemDecoration() {

        int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_card_album_grid);
        itemDecoration = new SpacesItemDecoration(spacingInPixels);

        recyclerView.addItemDecoration(itemDecoration);
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
                        mPresenter.loadAlbums(action);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position % 2 == 0) {
                outRect.left = 0;
                outRect.top = space;
                outRect.right = space / 2;
            } else {
                outRect.left = space / 2;
                outRect.top = space;
                outRect.right = 0;
            }
        }
    }

}
