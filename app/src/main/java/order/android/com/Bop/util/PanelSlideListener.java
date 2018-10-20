package order.android.com.Bop.util;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.event.MetaChangedEvent;
import order.android.com.Bop.ui.fragment.QuickControlsFragment;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PanelSlideListener implements SlidingUpPanelLayout.PanelSlideListener {

    private static final String TAG = PanelSlideListener.class.getSimpleName();

    private final SlidingUpPanelLayout mPanelLayout;
    private final Context mContext;
    NavigationView navigationView;
    DrawerLayout mDrawerLayout;
    CardView cardView;
    ImageView imageView;
    private View nowPlayingCard;
    private RelativeLayout toolbar;
    private LyricView lyricView;
    private SeekBar seekbar;
    private int screenWidth;
    private int screenHeight;
    private int lyricLineHeight;
    private int lyricFullHeight;
    private int lyricLineStartTranslationY;
    private int lyricLineEndTranslationY;
    private int lyricFullTranslationY;
    private ForegroundImageView albumImage;
    private Status mStatus = Status.COLLAPSED;
private  Runnable runnable=new Runnable() {
    @Override
    public void run() {
    lyricView.setVisibility(View.GONE);
    }
};private  Runnable runnable2=new Runnable() {
    @Override
    public void run() {
        animateToNormal();
    }
};
    public PanelSlideListener(SlidingUpPanelLayout slidingUpPanelLayout, NavigationView navigationView1, DrawerLayout drawerLayout1) {
        navigationView = navigationView1;
        mDrawerLayout = drawerLayout1;
        mPanelLayout = slidingUpPanelLayout;
        nowPlayingCard = mPanelLayout.findViewById(R.id.main_cl1);
        mContext = mPanelLayout.getContext();
        toolbar = (RelativeLayout) nowPlayingCard.findViewById(R.id.toolbar_album_detail);
        screenWidth = DensityUtil.getScreenWidth(mContext);
        screenHeight = DensityUtil.getScreenHeight(mContext);
        lyricView = (LyricView) nowPlayingCard.findViewById(R.id.lyricview);
        cardView = nowPlayingCard.findViewById(R.id.cover_cl2);
        seekbar = (SeekBar) mPanelLayout.findViewById(R.id.seekBar3);
        imageView = nowPlayingCard.findViewById(R.id.arrow_up_black);
        albumImage = (ForegroundImageView) nowPlayingCard.findViewById(R.id.cover_imageView);
        QuickControlsFragment.setPaletteColorChangeListener(new PaletteColorChangeListener() {
            @Override
            public void onPaletteColorChange(int paletteColor, int blackWhiteColor) {
                switch (mStatus) {
                    case FULLSCREEN:
                }
            }
        });


        if (MusicPlayer.getQueueSize() == 0) {
            mPanelLayout.setTouchEnabled(false);
        }
        caculateLyricView();
        subscribeMetaChangedEvent();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        lyricView.setTranslationY(lyricLineStartTranslationY - (lyricLineStartTranslationY - lyricLineEndTranslationY) * slideOffset);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

        if (previousState == SlidingUpPanelLayout.PanelState.COLLAPSED) {

            navigationView.getMenu().findItem(R.id.nowPlaying).setChecked(true);
        } else if (previousState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            navigationView.getMenu().findItem(R.id.nowPlaying).setChecked(false);
            if (mStatus == Status.FULLSCREEN) {
                /*panel.postDelayed(runnable,10);
                panel.postDelayed(runnable2,50);*/
                animateToNormal();
            }
        }

        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mStatus = Status.EXPANDED;
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toolbarSlideIn();
            nowPlayingCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mStatus == Status.EXPANDED) {
                        animateToFullscreen();
                    } else if (mStatus == Status.FULLSCREEN) {
                       /* v.postDelayed(runnable,10);
                       v.postDelayed(runnable2,50);*/
                       animateToNormal();
                    } else {
                        mPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }
            });
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            mStatus = Status.COLLAPSED;
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            nowPlayingCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPanelLayout.isTouchEnabled()) {
                        mPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                }
            });
        } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
            toolbar.setVisibility(View.INVISIBLE);
        }

    }

    private void toolbarSlideIn() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void caculateLyricView() {
        lyricLineHeight = DensityUtil.dip2px(mContext, 32);
        lyricFullHeight = screenHeight + 900;

        lyricLineStartTranslationY = screenHeight;
        lyricLineEndTranslationY = lyricLineHeight / 2;
        lyricFullTranslationY = DensityUtil.dip2px(mContext, 32);

    }
    private void animateToFullscreen() {
        lyricView.setHighLightTextColor(mContext.getResources().getColor(R.color.color1));
        lyricView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
        Animation lyricAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ConstraintLayout.LayoutParams lyricLayout = (ConstraintLayout.LayoutParams) lyricView.getLayoutParams();
                lyricLayout.height = (int) (lyricLineHeight + (lyricFullHeight - lyricLineHeight) * interpolatedTime);
                lyricView.setLayoutParams(lyricLayout);
                lyricView.setTranslationY(lyricLineEndTranslationY - (lyricLineEndTranslationY - lyricFullTranslationY) * interpolatedTime);
            }
        };
        lyricAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                lyricView.setPlayable(true);
                lyricView.setTouchable(true);
                lyricView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animateToNormal();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        lyricAnimation.setDuration(300);
        lyricView.startAnimation(lyricAnimation);

        mStatus = Status.FULLSCREEN;
    }

    private void animateToNormal() {
        lyricView.setHighLightTextColor(mContext.getResources().getColor(R.color.white2));
        toolbar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);

        //adjust lyricview
        Animation lyricAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) lyricView.getLayoutParams();
                layoutParams.height = (int) (lyricFullHeight - (lyricFullHeight - lyricLineHeight) * interpolatedTime);
                lyricView.setLayoutParams(layoutParams);
                lyricView.setTranslationY(lyricFullTranslationY + (lyricLineEndTranslationY - lyricFullTranslationY) * interpolatedTime);
                //lyricView.setTranslationY(0);
            }
        };
        lyricAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                lyricView.setPlayable(false);
                lyricView.setTouchable(false);
                lyricView.setClickable(false);
                lyricView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        lyricAnimation.setDuration(300);
        lyricView.setPlayable(false);
        lyricView.startAnimation(lyricAnimation);

        mStatus = Status.EXPANDED;
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
                        if (TextUtils.isEmpty(MusicPlayer.getTrackName()) || TextUtils.isEmpty(MusicPlayer.getArtistName())) {
                            if (mStatus == Status.EXPANDED || mStatus == Status.FULLSCREEN) {
                                mPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                            }
                            mPanelLayout.setTouchEnabled(false);
                        } else {
                            mPanelLayout.setTouchEnabled(true);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }


    public enum Status {
        EXPANDED,
        COLLAPSED,
        FULLSCREEN
    }
}
