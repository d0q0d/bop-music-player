package order.android.com.Bop.ui.dialogs;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.triggertrap.seekarc.SeekArc;

import butterknife.BindView;
import butterknife.ButterKnife;
import order.android.com.Bop.util.SleepTimerPreferences;

public class SleepTimerDialog extends DialogFragment {
    @BindView(order.android.com.Bop.R.id.seek_arc)
    SeekArc seekArc;
    @BindView(order.android.com.Bop.R.id.timer_display)
    TextView timerDisplay;

    private int seekArcProgress;
    private MaterialDialog materialDialog;
    private TimerUpdater timerUpdater;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        timerUpdater.cancel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        timerUpdater = new TimerUpdater();
        materialDialog = new MaterialDialog.Builder(getActivity())
                .title(getActivity().getResources().getString(order.android.com.Bop.R.string.action_sleep_timer))
                .positiveText(order.android.com.Bop.R.string.action_set)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (getActivity() == null) {
                            return;
                        }

                        final int minutes = seekArcProgress;

                        PendingIntent pi = makeTimerPendingIntent(PendingIntent.FLAG_CANCEL_CURRENT);

                        final long nextSleepTimerElapsedTime = SystemClock.elapsedRealtime() + minutes * 60 * 1000;
                        SleepTimerPreferences.getInstance(getActivity()).setNextSleepTimerElapsedRealtime(nextSleepTimerElapsedTime);
                        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleepTimerElapsedTime, pi);

                        Toast.makeText(getActivity(), getActivity().getResources().getString(order.android.com.Bop.R.string.sleep_timer_set, minutes), Toast.LENGTH_SHORT).show();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (getActivity() == null) {
                            return;
                        }
                        final PendingIntent previous = makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE);
                        if (previous != null) {
                            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            am.cancel(previous);
                            previous.cancel();
                            Toast.makeText(getActivity(), getActivity().getResources().getString(order.android.com.Bop.R.string.sleep_timer_canceled), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE) != null) {
                            timerUpdater.start();
                        }
                    }
                })
                .customView(order.android.com.Bop.R.layout.dialog_sleep_timer, false)
                .build();

        if (getActivity() == null || materialDialog.getCustomView() == null) {
            return materialDialog;
        }

        ButterKnife.bind(this, materialDialog.getCustomView());
        seekArc.post(new Runnable() {
            @Override
            public void run() {
                int width = seekArc.getWidth();
                int height = seekArc.getHeight();
                int small = Math.min(width, height);

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(seekArc.getLayoutParams());
                layoutParams.height = small;
                seekArc.setLayoutParams(layoutParams);
            }
        });

        seekArcProgress = SleepTimerPreferences.getInstance(getActivity()).getLastSleepTimerValue();
        updateTimeDisplayTime();
        seekArc.setProgress(seekArcProgress);

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(@NonNull SeekArc seekArc, int i, boolean b) {
                if (i < 1) {
                    seekArc.setProgress(1);
                    return;
                }
                seekArcProgress = i;
                updateTimeDisplayTime();
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                SleepTimerPreferences.getInstance(getActivity()).setLastSleepTimerValue(seekArcProgress);
            }
        });

        return materialDialog;
    }

    private void updateTimeDisplayTime() {
        timerDisplay.setText(seekArcProgress + " min");
    }

    private PendingIntent makeTimerPendingIntent(int flag) {
        return PendingIntent.getService(getActivity(), 0, makeTimerIntent(), flag);
    }

    private Intent makeTimerIntent() {
        return new Intent(getActivity(), order.android.com.Bop.MusicService.class)
                .setAction(order.android.com.Bop.MusicService.PAUSE_ACTION);
    }

    private class TimerUpdater extends CountDownTimer {
        public TimerUpdater() {
            super(SleepTimerPreferences.getInstance(getActivity()).getNextSleepTimerElapsedRealTime() - SystemClock.elapsedRealtime(), 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            materialDialog.setActionButton(DialogAction.NEUTRAL, materialDialog.getContext().getString(order.android.com.Bop.R.string.cancel_current_timer) + " (" + order.android.com.Bop.util.PreferencesUtility.getReadableDurationString(millisUntilFinished) + ")");
        }

        @Override
        public void onFinish() {
            materialDialog.setActionButton(DialogAction.NEUTRAL, null);
        }
    }

}