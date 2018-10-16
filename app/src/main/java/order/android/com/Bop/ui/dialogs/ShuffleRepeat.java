package order.android.com.Bop.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.MusicService;
import order.android.com.Bop.R;
import order.android.com.Bop.ui.activity.MainActivity;
import order.android.com.Bop.util.PlayModePreferences;

public class ShuffleRepeat extends android.support.v4.app.DialogFragment {
    @BindView(R.id.rootShuffleRepeat)
    ConstraintLayout rootShuffleRepeat;
    @BindView(R.id.radioRealButtonGroupRepeat)
    RadioRealButtonGroup radioRealButtonGroupReapet;
    @BindView(R.id.radioRealButtonGroupShuffle)
    RadioRealButtonGroup radioRealButtonGroupShuffle;
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shuffle_repeat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (mSwatch != null) {
            rootShuffleRepeat.setBackgroundColor(mSwatch.getRgb());
        }
        radioRealButtonGroupShuffle.setPosition(MainActivity.shuffleState);
        radioRealButtonGroupReapet.setPosition(MainActivity.repeatState);
        radioRealButtonGroupReapet.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {

                if (position == 0) {
                    MainActivity.repeatMain.saveItemIndexrepeat(position);
                    MainActivity.mPlayMode = MainActivity.PlayMode.REPEATOFF;
                    MusicPlayer.setRepeatMode(MusicService.REPEAT_NONE);

                } else if (position == 1) {
                    MainActivity.repeatMain.saveItemIndexrepeat(position);
                    MainActivity.mPlayMode = MainActivity.PlayMode.REPEATONE;
                    MusicPlayer.setRepeatMode(MusicService.REPEAT_CURRENT);


                } else if (position == 2) {
                    MainActivity.repeatMain.saveItemIndexrepeat(position);
                    MainActivity.mPlayMode = MainActivity.PlayMode.REPEATALL;
                    MusicPlayer.setRepeatMode(MusicService.REPEAT_ALL);
                }
            }
        });
        radioRealButtonGroupShuffle.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (position == 0) {
                    MainActivity.shuffleMain.saveItemIndexshuffle(position);
                    MainActivity.mPlayMode = MainActivity.PlayMode.SHUFFLEOFF;
                    MusicPlayer.setShuffleMode(MusicService.SHUFFLE_NONE);
                } else if (position == 1) {
                    MainActivity.shuffleMain.saveItemIndexshuffle(position);
                    MainActivity.mPlayMode = MainActivity.PlayMode.SHUFFLEALL;
                    MusicPlayer.setShuffleMode(MusicService.SHUFFLE_AUTO);
                }
            }
        });

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
    }

    public void setPaletteSwatch(Palette.Swatch swatch) {
        if (swatch == null) {
            return;
        }
        mSwatch = swatch;
        if (rootShuffleRepeat != null) {
            rootShuffleRepeat.setBackgroundColor(mSwatch.getRgb());
        }
    }

    public void dismiss() {
        getDialog().dismiss();
    }

}
