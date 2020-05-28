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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.incomecontrol.temporizador.MiContador;
import com.example.incomecontrol.temporizador.animationCheckCancell;
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

    private static final String TAG = "menu";
    private Toolbar toolbar;
   private ImageView codQr;
   private TextView txtQr;
   private String resultQr;
   private String id_user="";
   private LinearLayout linearQr;
   private ImageView imgPublicidad;
   private LottieAnimationView qr_animation;
    private LottieAnimationView check_animation;
    private LottieAnimationView cancell_animation;
   private Button btn_scan;
   private String id_local;
   private IntentIntegrator intentIntegrator;

   private StringRequest stringRequest;
    private JsonArrayRequest jsonArrayRequest;
   private RequestQueue request;
   private LinearLayout acomp;
    private LinearLayout linear_qrchi;
    private LinearLayout linear_check;
    private LinearLayout linear_cancel;
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
        acomp=findViewById(R.id.linear_acom);
        txt_acomp=findViewById(R.id.txt_acomp);
        linearQr=findViewById(R.id.linearQr);
        linear_qrchi=findViewById(R.id.linear_qrchi);
        linear_check=findViewById(R.id.linear_check);
        linear_cancel=findViewById(R.id.linear_cancel);
        imgPublicidad=findViewById(R.id.imgPublicidad);
        qr_animation=findViewById(R.id.animation_lottie_qr);
        btn_scan=findViewById(R.id.btn_scanQr);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.color_white));
        toolbar.setTitle("Escanear Codigo");
        setSupportActionBar(toolbar);
        codQr.setOnClickListener(this);
        qr_animation.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
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

            intentIntegrator=   new IntentIntegrator(menu.this);
           // intentIntegrator.setTimeout(8000);
            intentIntegrator.setPrompt("Escanea el código");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        }else if (v==qr_animation){

            intentIntegrator=   new IntentIntegrator(menu.this);
        //   intentIntegrator.setTimeout(8000);
            intentIntegrator.setPrompt("Escanea el código");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();

        }else if (v== btn_scan){

            intentIntegrator=   new IntentIntegrator(menu.this);
          //  intentIntegrator.setTimeout(8000);
            intentIntegrator.setPrompt("Escanea el código");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()!=null){
                resultQr=result.getContents();
                traeridLocal();


            }else{
                Toast.makeText(menu.this, "Error al escanear", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void traeridLocal() {

        Log.e(TAG, "traeridLocal: Qr: "+resultQr );
        Log.e("Entro", "Guardar en Bd");
        String url="http://app.dasscol.com/WebService/modelo/getLocalId.php?id="+resultQr;
        jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("Entro", "response en Bd");
                if(response.length()>0){

                    try {

                        id_local=  response.getJSONObject(response.length()-1).get("id").toString();
                        guardarBD();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    final animationCheckCancell animation = new animationCheckCancell(2000,1000,linear_cancel, linear_qrchi);
                    animation.start();
                    Toast.makeText(getApplicationContext(), "EL local no existe", Toast.LENGTH_LONG).show();
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

    private void guardarBD() {

        Log.e("Entro", "Guardar en Bd");
        String url="http://app.dasscol.com/WebService/modelo/getEventId.php?id="+id_user;
        jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("Entro", "response en Bd");
                if(response.length()>0){

                    Log.e("Entro", "response mayor a 0");
                    try {

                        String tipo=  response.getJSONObject(response.length()-1).get("tipo").toString();
                        String id_localActual=response.getJSONObject(response.length()-1).get("id_entradas").toString();
                       if(tipo.equalsIgnoreCase("Entrada")){
                           if(id_localActual.equalsIgnoreCase(id_local)){
                               guardarBD2("Salida");
                           }else{

                               final animationCheckCancell animation = new animationCheckCancell(2000,1000,linear_cancel, linear_qrchi);
                               animation.start();
                               Toast.makeText(menu.this, "Local salida no coincide con local de ingreso", Toast.LENGTH_LONG).show();
                           }

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
         String url="http://app.dasscol.com/WebService/modelo/setEvents.php?";
         final ProgressDialog loading = ProgressDialog.show(this, "Registrando entrada...", "Espere por favor");

         this.stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 Log.e("response", ""+response);
                 Toast.makeText(menu.this, "Registro de "+tipo2+" se realizo con exito", Toast.LENGTH_LONG).show();

                 if(tipo2.equalsIgnoreCase("Entrada")){
                     final MiContador timer = new MiContador(5000,1000,linearQr, imgPublicidad);
                     timer.start();
                 }else{
                     final animationCheckCancell animation = new animationCheckCancell(2000,1000,linear_check, linear_qrchi);
                     animation.start();
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
                 parametros.put("entrada",id_local);
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

    return "0";

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

