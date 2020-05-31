package com.App.incomecontrol.temporizador;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;

public class animationCheckCancell extends CountDownTimer {


    private LinearLayout animation;
    private LinearLayout linearQr;


    public animationCheckCancell(long millisInFuture, long countDownInterval, LinearLayout animation, LinearLayout linearQr) {
        super(millisInFuture, countDownInterval);
        this.animation = animation;
        this.linearQr = linearQr;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        linearQr.setVisibility(View.GONE);
        animation.setVisibility(View.VISIBLE);

    }

    @Override
    public void onFinish() {
        animation.setVisibility(View.GONE);
        linearQr.setVisibility(View.VISIBLE);
    }
}
