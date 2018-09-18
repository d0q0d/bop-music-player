package order.android.com.Bop.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;

import order.android.com.Bop.R;
import order.android.com.Bop.ui.activity.MainActivity;
import order.android.com.Bop.ui.fragment.AlbumDetailFragment;
import order.android.com.Bop.ui.fragment.ArtistDetailFragment;
import order.android.com.Bop.ui.fragment.PlaylistDetailFragment;


public class NavigationUtil {

    @TargetApi(21)
    public static void navigateToAlbum(Activity context, long albumID, String albumName, Pair<View, String> transitionViews) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        fragment = AlbumDetailFragment.newInstance(albumID, albumName, false, null);
        transaction.setCustomAnimations(R.anim.fade_in_slow, R.anim.fade_out_slow,R.anim.fade_in_slow, R.anim.fade_out_slow);
        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();

    }

    @TargetApi(21)
    public static void navigateToArtist(Activity context, long artistID, String name, Pair<View, String> transitionViews) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        transaction.setCustomAnimations(R.anim.fade_in_slow, R.anim.fade_out_slow,R.anim.fade_in_slow, R.anim.fade_out_slow);
        fragment = ArtistDetailFragment.newInstance(artistID, name, false, null);
        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();
    }

    @TargetApi(21)
    public static void navigateToPlaylistDetail(Activity context, long playlistID, String playlistName, Pair<View, String> transitionViews) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        transaction.setCustomAnimations(R.anim.fade_in_slow, R.anim.fade_out_slow,R.anim.fade_in_slow, R.anim.fade_out_slow);
        fragment =  PlaylistDetailFragment.newInstance(playlistID, playlistName, true, transitionViews.second);
        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();
    }


    public static Intent getNowPlayingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_LIBRARY);
        return intent;
    }

}
