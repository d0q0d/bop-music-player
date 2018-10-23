package order.android.com.Bop.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class SplashActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // checkShowIntro();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*
        private boolean checkShowIntro() {
            if (!PreferencesUtility.getInstance(this).introShown()) {
                PreferencesUtility.getInstance(this).setIntroShown();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivityForResult(new Intent(SplashActivity.this, AppIntroActivity.class), APP_INTRO_REQUEST);
                    }
                }, 50);
                return true;
            }
            return false;
        }*/
}
