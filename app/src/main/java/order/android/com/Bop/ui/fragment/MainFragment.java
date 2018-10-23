package order.android.com.Bop.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import order.android.com.Bop.R;
import order.android.com.Bop.util.BopUtil;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.PreferencesUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    public static ViewPager viewPager;
    public static ActionBar ab;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_tabs)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    private PreferencesUtility mPreferences;
    private String action;

    public static MainFragment newInstance(String action) {

        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                bundle.putString(Constants.PLAYLIST_TYPE, action);
                break;
            case Constants.NAVIGATE_PLAYLIST_FAVOURATE:
                bundle.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        mainFragment.setArguments(bundle);
        return mainFragment;
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());
        if (getArguments() != null) {
            action = getArguments().getString(Constants.PLAYLIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_chevron_right);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.library);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ab.setTitle("Artists");
                } else if (position == 1) {
                    ab.setTitle("Albums");
                } else if (position == 2) {
                    ab.setTitle("Songs");
                } else if (position == 3) {
                    ab.setTitle("Playlists");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            setTapIcon();

            viewPager.setCurrentItem(mPreferences.getStartPageIndex());
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());

        adapter.addFragment(ArtistFragment.newInstance(action));
        adapter.addFragment(AlbumFragment.newInstance(action));
        adapter.addFragment(SongFragment.newInstance(action));
        adapter.addFragment(PlaylistFragment.newInstance(action));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreferences.setStartPageIndex(viewPager.getCurrentItem());
    }

    private void setTapIcon() {
        int[] ids = {R.drawable.ic_account_circle, R.drawable.ic_album, R.drawable.ic_music_note, R.drawable.ic_playlist_add};
        int selected = Color.parseColor("#3c30db");
        int unselected = R.color.black;
        BopUtil.setupTabIcons(getActivity().getApplicationContext(), tabLayout, ids, tabLayout.getSelectedTabPosition(), selected, unselected);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        private boolean isTitle;

        public Adapter(FragmentManager fm) {
            super(fm);
        }

       /* public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }*/

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (this.isTitle)
                return mFragmentTitles.get(position);
            else
                return null;

        }
    }
}
