package com.App.incomecontrol.temporizador;

import android.os.CountDownTimer;

public class LectorQr extends CountDownTimer {


    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public LectorQr(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onFinish() {

    }
    @Override
    public void onTick(long millisUntilFinished) {

    }


}
