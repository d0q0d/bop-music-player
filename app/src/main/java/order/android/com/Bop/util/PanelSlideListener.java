package order.android.com.Bop.util;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.event.MetaChangedEvent;
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
    private View nowPlayingCard;
    private RelativeLayout toolbar;
    private IntEvaluator intEvaluator = new IntEvaluator();
    private FloatEvaluator floatEvaluator = new FloatEvaluator();
    private Status mStatus = Status.COLLAPSED;

    public PanelSlideListener(SlidingUpPanelLayout slidingUpPanelLayout, NavigationView navigationView1, DrawerLayout drawerLayout1) {
        navigationView = navigationView1;
        mDrawerLayout = drawerLayout1;
        mPanelLayout = slidingUpPanelLayout;
        nowPlayingCard = mPanelLayout.findViewById(R.id.main_cl1);
        toolbar = (RelativeLayout) nowPlayingCard.findViewById(R.id.toolbar_album_detail);
        mContext = mPanelLayout.getContext();
        if (MusicPlayer.getQueueSize() == 0) {
            mPanelLayout.setTouchEnabled(false);
        }
        subscribeMetaChangedEvent();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

        if (previousState == SlidingUpPanelLayout.PanelState.COLLAPSED) {

            navigationView.getMenu().findItem(R.id.nowPlaying).setChecked(true);
        } else if (previousState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            navigationView.getMenu().findItem(R.id.nowPlaying).setChecked(false);
            if (mStatus == Status.FULLSCREEN) {
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
                    } else if (mStatus == Status.FULLSCREEN) {
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
