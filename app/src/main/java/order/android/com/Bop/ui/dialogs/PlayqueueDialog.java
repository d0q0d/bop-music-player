package order.android.com.Bop.ui.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import order.android.com.Bop.BopApp;
import order.android.com.Bop.R;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.DaggerPlayqueueSongComponent;
import order.android.com.Bop.injector.component.PlayqueueSongComponent;
import order.android.com.Bop.injector.module.PlayqueueSongModule;
import order.android.com.Bop.mvp.contract.PlayqueueSongContract;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.ui.adapter.PlayqueueSongsAdapter;

public class PlayqueueDialog extends DialogFragment implements PlayqueueSongContract.View {

    @Inject
    PlayqueueSongContract.Presenter mPresenter;
    @BindView(R.id.recycler_view_songs)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.bottomsheet)
    LinearLayout root;

    private PlayqueueSongsAdapter mAdapter;
    private Palette.Swatch mSwatch;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.AnimationPlayQueue);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();
        mPresenter.attachView(this);
        mAdapter = new PlayqueueSongsAdapter((AppCompatActivity) getActivity(), null);
    }

    private void injectDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication()).getApplicationComponent();
        PlayqueueSongComponent playqueueSongComponent = DaggerPlayqueueSongComponent.builder()
                .applicationComponent(applicationComponent)
                .playqueueSongModule(new PlayqueueSongModule())
                .build();
        playqueueSongComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_playqueue, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (mSwatch != null) {
            root.setBackgroundColor(mSwatch.getRgb());
            mAdapter.setPaletteSwatch(mSwatch);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (mAdapter.getItemCount() == 0) {
                    dismiss();
                }
            }
        });

        mPresenter.subscribe();


    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void showSongs(List<Song> songs) {
        mAdapter.setSongList(songs);
    }

    @Override
    public void showEmptyView() {

    }

    public void setPaletteSwatch(Palette.Swatch swatch) {
        if (swatch == null) {
            return;
        }
        mSwatch = swatch;
        if (root != null) {
            root.setBackgroundColor(mSwatch.getRgb());
            mAdapter.setPaletteSwatch(mSwatch);
        }
    }

    public void dismiss() {
        getDialog().dismiss();
    }
}
