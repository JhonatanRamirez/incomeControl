package com.example.incomecontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.incomecontrol.temporizador.MiContador;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class menu extends AppCompatActivity implements View.OnClickListener {

   private Toolbar toolbar;
   private ImageView codQr;
   private TextView txtQr;
   private String resultQr;
   private String id_user="";
   private LinearLayout linearQr;
   private ImageView imgPublicidad;

   private StringRequest stringRequest;
    private JsonArrayRequest jsonArrayRequest;
   private RequestQueue request;
   private Switch acom;
   private LinearLayout acomp;
   private EditText txt_acomp;
   private Date d;
   private Timestamp ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        traerIdUserPreference();
        toolbar=findViewById(R.id.toolbar);
        codQr=findViewById(R.id.img_qr);
        txtQr=findViewById(R.id.txt_qr);
        acom=findViewById(R.id.switch_a);
        acomp=findViewById(R.id.linear_acom);
        txt_acomp=findViewById(R.id.txt_acomp);
        linearQr=findViewById(R.id.linearQr);
        imgPublicidad=findViewById(R.id.imgPublicidad);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.color_white));
        toolbar.setTitle("Escanear Codigo");
        setSupportActionBar(toolbar);
        codQr.setOnClickListener(this);
        acom.setOnClickListener(this);
        this.request= Volley.newRequestQueue(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.cerrar_sesion){

            borrarPreferencias();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if(v==codQr){

            new IntentIntegrator(menu.this).initiateScan();
        }else if( v== acom){

            if(acom.isChecked()){
                acomp.setVisibility(View.VISIBLE);

            }else{
                acomp.setVisibility(View.GONE);
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()!=null){
                resultQr=result.getContents();
                guardarBD();

            }else{
                Toast.makeText(menu.this, "Error al escanear", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void guardarBD() {

        Log.e("Entro", "Guardar en Bd");
        String url="http://covid19.dasscol.com/WebService/modelo/getEventId.php?id="+id_user;
        jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("Entro", "response en Bd");
                if(response.length()>0){

                    Log.e("Entro", "response mayor a 0");
                    try {

                        String tipo=  response.getJSONObject(response.length()-1).get("tipo").toString();
                       if(tipo.equalsIgnoreCase("Entrada")){
                           guardarBD2("Salida");
                       }else{
                           guardarBD2("Entrada");
                       }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.e("Entro", "response menor a 0");
                    guardarBD2("Entrada");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Volley Error",""+ error);

                Toast.makeText(menu.this, "No se puede conectar a la base de datos", Toast.LENGTH_LONG).show();

            }
        });
        request.add(jsonArrayRequest);

    }

     private void guardarBD2(final String tipo2){
         String url="http://covid19.dasscol.com/WebService/modelo/setEvents.php?";
         final ProgressDialog loading = ProgressDialog.show(this, "Registrando Persona...", "Espere por favor");

         this.stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 Log.e("response", ""+response);
                 Toast.makeText(menu.this, "Registro de "+tipo2+" se realizo con exito", Toast.LENGTH_LONG).show();
                 if(tipo2.equalsIgnoreCase("Entrada")){
                     final MiContador timer = new MiContador(5000,1000,linearQr, imgPublicidad);
                     timer.start();
                 }

                 loading.dismiss();
             }
         }, new Response.ErrorListener() {


             @Override
             public void onErrorResponse(VolleyError error) {
                 Log.e("Error", "Este es el error "+ error);
                 loading.dismiss();
             }
         }

         ){

             @Override
             protected Map<String,String> getParams() throws AuthFailureError {

                 d = new Date();
                 ts = new Timestamp(d.getTime());
                 Map<String,String> parametros= new HashMap<>();
                 parametros.put("entrada",resultQr);
                 parametros.put("id_user",id_user);
                 parametros.put("tipo",tipo2);
                 parametros.put("fecha",ts.toString());
                 parametros.put("acom",traerAcompanante());
                 return parametros;
             }
         };

         request.add(stringRequest);
     }

    private String traerAcompanante() {

        if(acom.isChecked()){
            if(!(txt_acomp.getText().toString().equalsIgnoreCase(""))){

                return txt_acomp.getText().toString();
            }else{
                return "0";
            }
        }else{
          return "0";
        }

    }


    private void borrarPreferencias(){

        SharedPreferences preferences= getSharedPreferences("datos_incomeControl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(this, "cerro sesion", Toast.LENGTH_LONG).show();
        pasarPantalla();
    }

    private void pasarPantalla() {
        Intent i= new Intent(menu.this, MainActivity.class);
        startActivity(i);
        finish();
    }
    private void traerIdUserPreference() {

        SharedPreferences preferencia=getSharedPreferences("datos_incomeControl", Context.MODE_PRIVATE);
        id_user= preferencia.getString("id","");
    }
}

