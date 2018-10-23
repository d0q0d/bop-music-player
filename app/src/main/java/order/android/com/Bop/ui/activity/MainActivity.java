package order.android.com.Bop.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.MusicService;
import order.android.com.Bop.R;
import order.android.com.Bop.event.MetaChangedEvent;
import order.android.com.Bop.ui.fragment.FoldersFragment;
import order.android.com.Bop.ui.fragment.MainFragment;
import order.android.com.Bop.ui.fragment.SearchFragment;
import order.android.com.Bop.util.BopUtil;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.NavigationUtil;
import order.android.com.Bop.util.PanelSlideListener;
import order.android.com.Bop.util.PlayModePreferences;
import order.android.com.Bop.util.PreferencesUtility;
import order.android.com.Bop.util.RxBus;
import order.android.com.Bop.util.Utilbazar.IabHelper;
import order.android.com.Bop.util.Utilbazar.IabResult;
import order.android.com.Bop.util.Utilbazar.Inventory;
import order.android.com.Bop.util.Utilbazar.Purchase;
import order.android.com.Bop.util.permission.PermissionCallback;
import order.android.com.Bop.util.permission.PermissionManager;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener {
    public static final int APP_INTRO_REQUEST = 100;
    private static final String PRO_VERSION = "pro_version";
    private static final int RCODE = 1001;
    public static PlayModePreferences repeatMain;
    public static PlayModePreferences shuffleMain;
    public static PreferencesUtility pvMain;
    public static int repeatState;
    public static int shuffleState;
    public static PlayMode mPlayMode;
    public static boolean r;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout panelLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    String base64Key;
    SeekBar seekBar;
    PackageManager packageManager;
    int x = 0;
    ProgressDialog progressDialog;
    private PanelSlideListener.Status mStatus = PanelSlideListener.Status.COLLAPSED;
    private String action;
    private Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    private Handler navDrawerRunnable = new Handler();
    private Runnable runnable;
    private PanelSlideListener mPanelSlideListener;
    private boolean listenerSeted = false;
    private IabHelper iabHelper;
    private Runnable navigateNowPlaying = new Runnable() {
        @Override
        public void run() {
            if (MusicPlayer.getQueueSize() == 0) {
                panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } else {
                if (mStatus == PanelSlideListener.Status.COLLAPSED) {
                    panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    navigationView.getMenu().findItem(R.id.nowPlaying).setChecked(true);
                } else if (mStatus == PanelSlideListener.Status.EXPANDED) {
                    panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    navigationView.getMenu().findItem(R.id.nowPlaying).setChecked(true);
                }
            }


        }
    };
    private Runnable navigateNowplaying = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nowPlaying).setChecked(false);
            Fragment fragment = MainFragment.newInstance(Constants.NAVIGATE_ALLSONG);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();

        }
    };
    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadEverything();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };
    private Runnable navigatePlaylist = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.playlists).setChecked(false);
            x = 3;
            MainFragment.viewPager.setCurrentItem(x);

        }
    };

    private Runnable navigateAlbum = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.albums).setChecked(false);
            x = 1;
            MainFragment.viewPager.setCurrentItem(x);
        }
    };
    private Runnable navigateSong = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.songs).setChecked(false);
            x = 2;
            MainFragment.viewPager.setCurrentItem(x);

        }
    };
    private Runnable navigateArtist = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.artists).setChecked(false);
            x = 0;
            MainFragment.viewPager.setCurrentItem(x);
        }
    };
    private Runnable navigateEqualizer = new Runnable() {
        public void run() {
            NavigationUtil.navigateToEqualizer(MainActivity.this);
        }
    };

    private Runnable navigateSearch = new Runnable() {
        public void run() {
            Fragment fragment = new SearchFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.add(R.id.fragment_container, fragment);
            transaction.addToBackStack(null).commit();
        }
    };
    private Runnable navigateFeedback = new Runnable() {
        public void run() {
            final Intent mail = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "bopmusicplayer@gmail.com", null));
            mail.putExtra(Intent.EXTRA_SUBJECT, "^^^Your Feedback^^^");
            mail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getBaseContext().startActivity(mail);
        }
    };

    private Runnable navigateFolders = new Runnable() {

        public void run() {
            navigationView.getMenu().findItem(R.id.folder).setChecked(false);
            Fragment fragment = new FoldersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            // transaction.replace(R.id.fragment_container, fragment).commit();
            transaction.add(R.id.fragment_container, fragment);
            transaction.addToBackStack(null).commit();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        action = getIntent().getAction();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            this.getWindow().setStatusBarColor(Color.WHITE);
        }
        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateNowplaying);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_ALLSONG, navigateSong);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);

        navDrawerRunnable.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupDrawerContent(navigationView);
            }
        }, 700);


        if (BopUtil.isMarshmallow()) {
            checkPermissionAndThenLoad();
        } else {
            loadEverything();
        }

        addBackstackListener();

        if (Intent.ACTION_VIEW.equals(action)) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.clearQueue();
                    MusicPlayer.openFile(getIntent().getData().getPath());
                    MusicPlayer.playOrPause();
                }
            }, 350);
        }
        packageManager = getPackageManager();
        base64Key = "MIHNMA0" + GetMiddleBit() + "wEAAQ==";
        pvMain = new PreferencesUtility(this);
        r = pvMain.getPV();
        repeatMain = new PlayModePreferences(this);
        shuffleMain = new PlayModePreferences(this);
        progressDialog = new ProgressDialog(this);
        //checkShowIntro();
        //checkVersion();
        subscribeMetaChangedEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        repeatState = repeatMain.getItemIndexrepeat();
        shuffleState = shuffleMain.getItemIndexshuffle();
        if (repeatState == 0) {
            mPlayMode = PlayMode.REPEATOFF;
            MusicPlayer.setRepeatMode(MusicService.REPEAT_NONE);
        } else if (repeatState == 1) {
            mPlayMode = PlayMode.REPEATONE;
            MusicPlayer.setRepeatMode(MusicService.REPEAT_CURRENT);
        } else if (repeatState == 2) {
            mPlayMode = PlayMode.REPEATALL;
            MusicPlayer.setRepeatMode(MusicService.REPEAT_ALL);
        }
        if (shuffleState == 0) {
            mPlayMode = PlayMode.SHUFFLEOFF;
            MusicPlayer.setShuffleMode(MusicService.SHUFFLE_NONE);
        } else if (shuffleState == 1) {
            mPlayMode = PlayMode.SHUFFLEALL;
            MusicPlayer.setShuffleMode(MusicService.SHUFFLE_AUTO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unSubscribe(this);
        if (mPanelSlideListener != null) {
            RxBus.getInstance().unSubscribe(mPanelSlideListener);
        }
        if (iabHelper != null) {
            iabHelper.dispose();
            iabHelper = null;
        }
    }

    private void loadEverything() {
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateNowplaying.run();
        }

        new initQuickControls().execute("");
    }

    private void checkPermissionAndThenLoad() {
        //check for permission
        if (PermissionManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            if (PermissionManager.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, "Bop will need to read and write external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PermissionManager.askForPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                PermissionManager.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadstorageCallback);
            }
        }
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
    }


    private void updatePosition(final MenuItem menuItem) {
        runnable = null;

        switch (menuItem.getItemId()) {
            case R.id.nowPlaying:
                runnable = navigateNowPlaying;
                break;
            case R.id.search:
                runnable = navigateSearch;
                break;
            case R.id.playlists:
                runnable = navigatePlaylist;
                break;
            case R.id.artists:
                runnable = navigateArtist;
                break;
            case R.id.albums:
                runnable = navigateAlbum;
                break;
            case R.id.songs:
                runnable = navigateSong;
                break;
            case R.id.equalizer:
                if (!r) {
                    new MaterialDialog.Builder(this)
                            .title("Bop Pro feature")
                            .icon(getResources().getDrawable(R.drawable.bazar))
                            .content("Unlocks extra features, such as the lyrics, folder view, equalizer and the sleep timer")
                            .positiveText("Buy or restore")
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (isNetworkConnected()) {
                                        checkVersion();
                                    } else {
                                        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
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
                } else {
                    runnable = navigateEqualizer;
                }

                break;
            case R.id.folder:
                if (!r) {
                    new MaterialDialog.Builder(this)
                            .title("Bop Pro feature")
                            .icon(getResources().getDrawable(R.drawable.bazar))
                            .content("Unlocks extra features, such as the lyrics, folder view, equalizer and the sleep timer")
                            .positiveText("Buy or restore")
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (isNetworkConnected()) {
                                        checkVersion();
                                    } else {
                                        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
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
                } else {
                    runnable = navigateFolders;
                }

                break;
            case R.id.feedback:
                runnable = navigateFeedback;
                break;
        }

        if (runnable != null) {
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            seekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
            int index = seekBar.getProgress();
            seekBar.setProgress(index + 1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            seekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
            int index = seekBar.getProgress();
            seekBar.setProgress(index - 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return (currentFragment instanceof MainFragment);
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {
                if (panelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else if (isNavigatingMain()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else super.onBackPressed();
                return true;
            }


            case R.id.tab_search:
                navigateSearch.run();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (panelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            navigationView.getMenu().findItem(R.id.nowPlaying).setChecked(false);
            panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!listenerSeted && panelLayout.findViewById(R.id.main_cl1) != null) {
            mPanelSlideListener = new PanelSlideListener(panelLayout, navigationView, mDrawerLayout);
            panelLayout.addPanelSlideListener(mPanelSlideListener);
            listenerSeted = true;
        }
    }/*private boolean checkShowIntro() {
        if (!PreferencesUtility.getInstance(this).introShown()) {
            PreferencesUtility.getInstance(this).setIntroShown();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(new Intent(MainActivity.this, AppIntroActivity.class), APP_INTRO_REQUEST);
                }
            }, 50);
            return true;
        }
        return false;
    }
*/

    private void subscribeMetaChangedEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(MetaChangedEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<MetaChangedEvent>() {
                    @Override
                    public void call(MetaChangedEvent event) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void checkVersion() {
        if (isPackageInstalled("com.farsitel.bazaar", this)) {
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Please wait");
            progressDialog.show();
            iabHelper = new IabHelper(this, base64Key);
            iabHelper.startSetup(this);
        } else {
            Snackbar.make(panelLayout, "First Install Bazaar then install this app from that", Snackbar.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this, "Bazar not installed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            List<String> list = new ArrayList<>();
            list.add(PRO_VERSION);
            iabHelper.queryInventoryAsync(true, list, this);
        } else {
            Log.i("Tag", "onIabSetupFinished: Setup not complete ");
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isSuccess()) {
            Purchase purchase = inv.getPurchase(PRO_VERSION);
            if (purchase != null) {
                Toast.makeText(MainActivity.this, "Restored", Toast.LENGTH_SHORT).show();
                changeTOPRO();
                progressDialog.dismiss();

            } else {
                Log.i("Tag", "onQueryInventoryFinished: Query failed");
                progressDialog.dismiss();
                purchaseProVersion();
            }
        }

    }

    public void changeTOPRO() {
        pvMain.setPV(true);
        r = pvMain.getPV();
    }


    public void purchaseProVersion() {
        iabHelper.launchPurchaseFlow(MainActivity.this, PRO_VERSION, RCODE, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isSuccess()) {
                    if (info != null) {
                        changeTOPRO();
                        Toast.makeText(getApplicationContext(), "Upgrade to pro successful", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Purchase failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RCODE) {
            iabHelper.handleActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String GetMiddleBit() {

        return "GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDIhmPNNC5gHI6SyEUaCriWL80OAi73kV8P+6NNd77q+zXFjXi0vlFwNyC7vg1LyavEERf0q5DHII7Oii1O8XPQndlhQx2kELQn+7xDdAKnKSc1Rmojk9+x+11ULfchVlQiqx9w/FBjLhqNSygMp0VZmiRCFA92QLXRLa67f5Bt3ie3+Szm8ttXcRt6yqyOPshOjOdII2ptWiVDxIswkSFR1aDgNRWJ2Iuzpk6pCn8CA";
    }

    /* public boolean isInternetAvailable() {
         try {
             InetAddress ipAddr = InetAddress.getByName("google.com");
             //You can replace it with your name
             return !ipAddr.equals("");

         } catch (Exception e) {
             return false;
         }*/
    public boolean isNetworkConnected() { // check internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }

    public enum PlayMode {
        REPEATALL,
        REPEATONE,
        REPEATOFF,
        SHUFFLEOFF,
        SHUFFLEALL
    }
}

