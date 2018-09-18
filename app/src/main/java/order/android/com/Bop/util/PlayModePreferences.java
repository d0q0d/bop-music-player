package order.android.com.Bop.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PlayModePreferences {
    private static final String APP_SHARED_PREFS = "myApp_preferences"; //  Name of the file -.xml
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public PlayModePreferences(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public int getItemIndex() {
        return appSharedPrefs.getInt("itemIndex", -1);
    }

    public void saveItemIndex(int i) {
        prefsEditor.putInt("itemIndex", i);
        prefsEditor.commit();
    }

    public int getItemIndexrepeat() {
        return appSharedPrefs.getInt("itemIndexrepeat", -1);
    }

    public void saveItemIndexrepeat(int i) {
        prefsEditor.putInt("itemIndexrepeat", i);
        prefsEditor.commit();
    }

    public int getItemIndexshuffle() {
        return appSharedPrefs.getInt("itemIndexshuffle", -1);
    }

    public void saveItemIndexshuffle(int i) {
        prefsEditor.putInt("itemIndexshuffle", i);
        prefsEditor.commit();
    }
}