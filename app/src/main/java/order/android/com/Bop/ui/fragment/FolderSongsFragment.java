package order.android.com.Bop.ui.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.DaggerFolderSongsComponent;
import order.android.com.Bop.injector.component.FolderSongsComponent;
import order.android.com.Bop.injector.module.FolderSongsModule;
import order.android.com.Bop.mvp.contract.FolderSongsContract;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.ui.adapter.SongsListAdapter;
import order.android.com.Bop.util.ATEUtil;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.DensityUtil;
import order.android.com.Bop.util.RxBus;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderSongsFragment extends Fragment implements FolderSongsContract.View{

    @Inject
    FolderSongsContract.Presenter mPresenter;
    @BindView(R.id.recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private SongsListAdapter mAdapter;
    private String path;

    public static FolderSongsFragment newInstance(String path) {

        Bundle args = new Bundle();
        args.putString(Constants.FOLDER_PATH, path);

        FolderSongsFragment fragment = new FolderSongsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();
        mPresenter.attachView(this);

        if (getArguments() != null) {
            path = getArguments().getString(Constants.FOLDER_PATH);
        }

        mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), null, Constants.NAVIGATE_ALLSONG, true);
    }

    private void injectDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication()).getApplicationComponent();
        FolderSongsComponent folderSongsComponent = DaggerFolderSongsComponent.builder()
                .applicationComponent(applicationComponent)
                .folderSongsModule(new FolderSongsModule())
                .build();
        folderSongsComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.folder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        mPresenter.loadSongs(path);
        subscribeMetaChangedEvent();
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
    public void showSongs(List<Song> songList) {
        mAdapter.setSongList(songList);
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
}
