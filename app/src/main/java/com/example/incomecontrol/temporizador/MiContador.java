package com.example.incomecontrol.temporizador;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.incomecontrol.menu;
import com.example.incomecontrol.registroUsuario;

public class MiContador extends CountDownTimer implements View.OnClickListener {

    LinearLayout mostrar;
    ImageView close;

    public MiContador(long millisInFuture, long countDownInterval, View vista, View pub) {
        super(millisInFuture, countDownInterval);
        mostrar= (LinearLayout) vista;
        close= (ImageView) pub;
        close.setOnClickListener(this);
    }

    @Override
    public void onFinish() {
        //Lo que quieras hacer al finalizar

      //  mostrar.setText("Paro..!");

        close.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        //texto a mostrar en cuenta regresiva en un textview
        //mostrar.setText((millisUntilFinished/1000+""));

        mostrar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        if(v== close){
            mostrar.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);
        }
    }
}