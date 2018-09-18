package order.android.com.Bop.ui.fragment;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import order.android.com.Bop.BopApp;
import order.android.com.Bop.R;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.ArtistInfoComponent;
import order.android.com.Bop.injector.component.DaggerArtistInfoComponent;
import order.android.com.Bop.injector.module.ArtistInfoModule;
import order.android.com.Bop.mvp.contract.ArtistDetailContract;
import order.android.com.Bop.util.Constants;

public class ArtistDetailFragment extends Fragment implements ArtistDetailContract.View{

    @Inject
    ArtistDetailContract.Presenter mPresenter;
    @BindView(R.id.toolbar_artist_detail)
    Toolbar toolbar;
    private ArtistMusicFragment mArtistMusicFragment;
    private long artistID = -1;
    private String artistName = "";

    public static ArtistDetailFragment newInstance(long id, String name, boolean useTransition, String transitionName) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        args.putString(Constants.ARTIST_NAME, name);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injetDependences();
        mPresenter.attachView(this);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
            artistName = getArguments().getString(Constants.ARTIST_NAME);
        }

    }

    private void injetDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication()).getApplicationComponent();
        ArtistInfoComponent artistInfoComponent = DaggerArtistInfoComponent.builder()
                .applicationComponent(applicationComponent)
                .artistInfoModule(new ArtistInfoModule())
                .build();
        artistInfoComponent.injectForFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artist_detail, container, false);
        return root;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupToolbar();

        mArtistMusicFragment = ArtistMusicFragment.newInstance(artistID);
        getChildFragmentManager().beginTransaction().replace(R.id.container, mArtistMusicFragment).commit();
        mPresenter.subscribe(artistID);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(artistName);
    }

    @Override
    public void showArtistArt(Bitmap bitmap) {

    }

    @Override
    public void showArtistArt(Drawable drawable) {
        }
}
