package order.android.com.Bop.ui.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.graphics.Palette;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import order.android.com.Bop.BopApp;
import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.event.FavourateSongEvent;
import order.android.com.Bop.event.MetaChangedEvent;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.DaggerQuickControlsComponent;
import order.android.com.Bop.injector.component.QuickControlsComponent;
import order.android.com.Bop.injector.module.ActivityModule;
import order.android.com.Bop.injector.module.QuickControlsModule;
import order.android.com.Bop.mvp.contract.QuickControlsContract;
import order.android.com.Bop.ui.activity.MainActivity;
import order.android.com.Bop.ui.dialogs.PlayqueueDialog;
import order.android.com.Bop.ui.dialogs.ShuffleRepeat;
import order.android.com.Bop.util.ATEUtil;
import order.android.com.Bop.util.BopUtil;
import order.android.com.Bop.util.ColorUtil;
import order.android.com.Bop.util.DensityUtil;
import order.android.com.Bop.util.ForegroundImageView;
import order.android.com.Bop.util.LyricView;
import order.android.com.Bop.util.MusicProgressViewUpdateHelper;
import order.android.com.Bop.util.NavigationUtil;
import order.android.com.Bop.util.PaletteColorChangeListener;
import order.android.com.Bop.util.PanelSlideListener;
import order.android.com.Bop.util.PlayModePreferences;
import order.android.com.Bop.util.RxBus;
import order.android.com.Bop.util.ScrimUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class QuickControlsFragment extends Fragment implements QuickControlsContract.View, MusicProgressViewUpdateHelper.Callback {


    private static PaletteColorChangeListener sListener;
    @BindView(R.id.main_cl1)
    public View topContainer;
    protected PlayModePreferences sleepTimer;
    @Inject
    QuickControlsContract.Presenter mPresenter;
    @BindView(R.id.play_pause)
    order.android.com.Bop.util.PlayPauseView mPlayPauseView;
    @BindView(R.id.trackName)
    TextView mTitle;
    @BindView(R.id.artistAlbum)
    TextView mArtist;
    @BindView(R.id.cover_imageView)
    ForegroundImageView mAlbumArt;
    @BindView(R.id.rewind)
    ImageButton previous;
    @BindView(R.id.forward)
    ImageButton next;
    @BindView(R.id.seekBar3)
    SeekBar mSeekBar;
    @BindView(R.id.popup_menu_quick)
    ImageView popupMenu;
    @BindView(R.id.time1)
    TextView duration;
    @BindView(R.id.upIndicator)
    ImageView upIndicator;
    @BindView(R.id.playqueue_main2)
    ImageView playqueue;
    @BindView(R.id.shuffle_repeat_button)
    ImageButton shuffle_repeat_button;
    @BindView(R.id.time2)
    TextView duration2;
    SeekBar volumeSeekbar = null;
    AudioManager audioManager = null;
    @BindView(R.id.arrow_up_black)
    ImageView imageView123;
    @BindView(R.id.lyricview)
    LyricView mLyricView;
    private int blackWhiteColor;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private boolean mIsFavorite = false;
    private MusicProgressViewUpdateHelper progressViewUpdateHelper;
    private PanelSlideListener.Status mStatus = PanelSlideListener.Status.COLLAPSED;
    private PlayqueueDialog bottomDialogFragment;
    private ShuffleRepeat shuffleRepeatFragment;
    private Palette.Swatch mSwatch;
    private Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = MusicPlayer.position();
            mSeekBar.setProgress((int) position);
            mLyricView.setCurrentTimeMillis(position);
            if (MusicPlayer.isPlaying()) {
                mSeekBar.postDelayed(mUpdateProgress, 50);
            } else mSeekBar.removeCallbacks(this);

        }
    };

    public static void setPaletteColorChangeListener(PaletteColorChangeListener paletteColorChangeListener) {
        sListener = paletteColorChangeListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressViewUpdateHelper = new MusicProgressViewUpdateHelper(this);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        injectDependences();
        mPresenter.attachView(this);
    }

    private void injectDependences() {
        ApplicationComponent applicationComponent = ((BopApp) getActivity().getApplication())
                .getApplicationComponent();
        QuickControlsComponent quickControlsComponent = DaggerQuickControlsComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .quickControlsModule(new QuickControlsModule())
                .build();
        quickControlsComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) view.getParent().getParent();
        if (mStatus == PanelSlideListener.Status.COLLAPSED) {
            imageView123.setVisibility(View.VISIBLE);
        } else if (mStatus == PanelSlideListener.Status.EXPANDED) {
            imageView123.setVisibility(View.INVISIBLE);
        }
        sleepTimer = new PlayModePreferences(getActivity());
        setUpProgressSlider();
        setUpPopupMenu(popupMenu);


        mLyricView.setLineSpace(15.0f);
        mLyricView.setTextSize(17.0f);
        mLyricView.setPlayable(false);
        mLyricView.setDefaultColor(getResources().getColor(R.color.black));
        mLyricView.setTranslationY(DensityUtil.getScreenWidth(getActivity()) + DensityUtil.dip2px(getActivity(), 120));
        mLyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
            @Override
            public void onPlayerClicked(long progress, String content) {
                MusicPlayer.seek((long) progress);
                if (!MusicPlayer.isPlaying()) {
                    mPresenter.onPlayPauseClick();
                }
            }
        });
        mSeekBar.setSecondaryProgress(mSeekBar.getMax());

        mPlayPauseView.setDrawableColor(getResources().getColor(R.color.black));
        if (mPlayPauseView != null) {
            if (MusicPlayer.isPlaying())
                mPlayPauseView.Play();
            else mPlayPauseView.Pause();
        }
        mTitle.setSelected(true);
        mArtist.setSelected(true);
        initControls(volumeSeekbar, AudioManager.STREAM_MUSIC);
        subscribeFavourateSongEvent();
        subscribeMetaChangedEvent();
    }

    private void setUpPopupMenu(final ImageView popupMenu) {
        popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(getContext(), v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sleep_timer: {

                                if (MainActivity.r) {
                                    new order.android.com.Bop.ui.dialogs.SleepTimerDialog().show(getFragmentManager(), "SET_SLEEP_TIMER");

                                } else {
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Bop Pro feature")
                                            .icon(getResources().getDrawable(R.drawable.bazar))
                                            .content("Unlocks extra features, such as the lyrics, folder view, equalizer and the sleep timer")
                                            .positiveText("Buy or restore")
                                            .negativeText(R.string.cancel)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    if (((MainActivity)getActivity()).isNetworkConnected()) {
                                                        ((MainActivity)getActivity()).checkVersion();
                                                    } else {
                                                        Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }

                            }
                            break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.main_menu);

                menu.show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        RxBus.getInstance().unSubscribe(this);
    }


    @Override
    public void showLyric(File file) {
        if (file == null) {
            mLyricView.reset("No lyrics or VPN is disconnecting");
        } else {
            mLyricView.setLyricFile(file, "UTF-8");
        }
    }

    private void initControls(SeekBar seekBar, final int stream) {
        volumeSeekbar = (SeekBar) getView().findViewById(R.id.volumeSeekBar);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        volumeSeekbar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekbar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(stream, progress, AudioManager.FLAG_PLAY_SOUND);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void setPlayPauseButton(boolean isPlaying) {
        if (isPlaying) {
            mPlayPauseView.Play();
        } else {
            mPlayPauseView.Pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        progressViewUpdateHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        progressViewUpdateHelper.stop();
    }

    @Override
    public boolean getPlayPauseStatus() {
        return mPlayPauseView.isPlay();
    }

    @Override
    public void setPalette(Palette palette) {
        mSwatch = ColorUtil.getMostPopulousSwatch(palette);
        int paletteColor;
        if (mSwatch != null) {
            paletteColor = mSwatch.getRgb();
        } else {
            mSwatch = palette.getMutedSwatch() == null ? palette.getVibrantSwatch() : palette.getMutedSwatch();
            if (mSwatch != null) {
                paletteColor = mSwatch.getRgb();
            } else {
                paletteColor = ATEUtil.getThemeAlbumDefaultPaletteColor(getContext());
            }

        }
        blackWhiteColor = ColorUtil.getBlackWhiteColor(paletteColor);
        topContainer.setBackgroundColor(paletteColor);
        if (bottomDialogFragment != null && mSwatch != null) {
            bottomDialogFragment.setPaletteSwatch(mSwatch);
        }
        mLyricView.setHighLightTextColor(blackWhiteColor);
        mLyricView.setDefaultColor(blackWhiteColor);
        mLyricView.setTouchable(false);
        mLyricView.setHintColor(blackWhiteColor);
        //set albumart foreground
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mAlbumArt.setForeground(
                    ScrimUtil.makeCubicGradientScrimDrawable(
                            paletteColor, //颜色
                            8, //渐变层数
                            Gravity.CENTER_HORIZONTAL)); //起始方向

        }
        if (sListener != null) {
            sListener.onPaletteColorChange(paletteColor, blackWhiteColor);
        }
    }

    @Override
    public void startUpdateProgress() {
        mSeekBar.postDelayed(mUpdateProgress, 10);
    }

    @Override
    public void setProgressMax(int max) {
        mSeekBar.setMax(max);
    }

    @Override
    public ImageView getImageView(ImageView imageView) {
        return mAlbumArt;
    }

    @Override
    public TextView getTextView1(TextView textView) {
        return mTitle;
    }

    @Override
    public TextView getTextView2(TextView textView) {
        return mArtist;
    }

    @Override
    public void setAlbumArt(Context c, final ImageView v, final Bitmap albumArt) {
        BopUtil.ImageViewAnimatedChange(getActivity(), mAlbumArt, albumArt);

    }

    @Override
    public void setAlbumArt(Context c, final ImageView v, Drawable albumArt) {
        BopUtil.ImageViewAnimatedChange(getActivity(), mAlbumArt, albumArt);
    }

    @Override
    public void setTitle(Context c, TextView v, String title) {
        BopUtil.TextViewAnimatedChange(getActivity(), mTitle, title);
    }

    @Override
    public void setArtist(Context c, TextView v, String artist) {
        BopUtil.TextViewAnimatedChange(getActivity(), mArtist, artist);
    }

    @OnClick(R.id.play_pause)
    public void onPlayPauseClick() {
        mPresenter.onPlayPauseClick();
    }

    @OnClick(R.id.upIndicator)
    public void onUpIndicatorClick() {

        if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }

    }

    @OnClick(R.id.playqueue_main2)
    public void onPlayQueueClick() {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (bottomDialogFragment == null) {
            bottomDialogFragment = new PlayqueueDialog();
        }
        bottomDialogFragment.show(fm, "fragment_bottom_dialog");
        if (mSwatch != null) {
            bottomDialogFragment.setPaletteSwatch(mSwatch);

        }
    }


    @OnClick(R.id.forward)
    public void onNextClick() {
        mPresenter.onNextClick();
    }

    @OnClick(R.id.shuffle_repeat_button)
    public void onShuffleRepeatClick() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (shuffleRepeatFragment == null) {
            shuffleRepeatFragment = new ShuffleRepeat();
        }
        shuffleRepeatFragment.show(fm, "fragment_bottom_dialog");
        if (mSwatch != null) {
            shuffleRepeatFragment.setPaletteSwatch(mSwatch);

        }
    }

    @OnClick(R.id.rewind)
    public void onPreviousClick() {
        mPresenter.onPreviousClick();
    }

    @OnClick(R.id.cover_imageView)
    public void onImageViewClick() {

        new MaterialDialog.Builder(getActivity())
                .items(R.array.dialog).itemsColor(getResources().getColor(R.color.black)).typeface("proxima_nova_regular.ttf", "proxima_nova_regular.ttf")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (which == 1) {
                            if (mSlidingUpPanelLayout != null) {
                                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                NavigationUtil.navigateToAlbum(getActivity(), MusicPlayer.getCurrentAlbumId(),
                                        MusicPlayer.getAlbumName(), null);
                            }
                        } else if (which == 2) {
                            if (mSlidingUpPanelLayout != null) {
                                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                NavigationUtil.navigateToArtist(getActivity(), MusicPlayer.getCurrentArtistId(),
                                        MusicPlayer.getArtistName(), null);
                            }

                        } else if (which == 0) {

                            BopUtil.showAddPlaylistDialog(getActivity(), new long[]{MusicPlayer.getCurrentAudioId()});

                        } else if (which == 3) {
                            BopUtil.shareTrack(getActivity(), MusicPlayer.getCurrentAudioId());
                        } else if (which == 4) {
                            long[] deleteIds = {MusicPlayer.getCurrentAudioId()};
                            BopUtil.showDeleteDialog(getContext(), MusicPlayer.getTrackName(), deleteIds,
                                    new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        }
                                    });
                        }
                    }
                })
                .show();

    }

    private void subscribeFavourateSongEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(FavourateSongEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FavourateSongEvent>() {
                    @Override
                    public void call(FavourateSongEvent event) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void subscribeMetaChangedEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(MetaChangedEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MetaChangedEvent>() {
                    @Override
                    public void call(MetaChangedEvent event) {
                        mPresenter.updateNowPlayingCard();
                        mPresenter.loadLyric();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void setUpProgressSlider() {

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSeekBar.removeCallbacks(mUpdateProgress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /*MusicPlayer.seek(progress);
                onUpdateProgressViews((int) MusicPlayer.position(), (int) MusicPlayer.duration());*/
                MusicPlayer.seek((long) seekBar.getProgress());
                onUpdateProgressViews((int) MusicPlayer.position(), (int) MusicPlayer.duration());
                mSeekBar.postDelayed(mUpdateProgress, 10);
            }
        });
    }

    @Override
    public void onUpdateProgressViews(int progress, int total) {
        mSeekBar.setMax(total);
        mSeekBar.setProgress(progress);
        String time = BopUtil.makeShortTimeString(getActivity(), mSeekBar.getProgress() / 1000);
        duration.setText(time);
        String duration3 = BopUtil.getReadableDurationString(MusicPlayer.duration());
        duration2.setText(String.valueOf(duration3));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
