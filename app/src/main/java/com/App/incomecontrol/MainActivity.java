package com.App.incomecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView registarme;
    private EditText user;
    private  EditText clave;
    private FrameLayout netx;
    private String usuario;
    private String claveU;
    private String documento;
    private String celular;
    private String nombre;
    private String id;
    private  TextView txt_olvidaste_clave;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue request;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        consultarPreferencias();
        registarme= findViewById(R.id.txt_registrate);
        user= findViewById(R.id.user);
        clave= findViewById(R.id.password);
        netx= findViewById(R.id.img_netx);
        txt_olvidaste_clave= findViewById(R.id.txt_olvidaste_clave);
        request= Volley.newRequestQueue(this);
        registarme.setOnClickListener(this);
        netx.setOnClickListener(this);
        txt_olvidaste_clave.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if(v==registarme){

            Intent i = new Intent(MainActivity.this, registroUsuario.class);
            startActivity(i);
            finish();

        }else if (v== netx){
            if(validarDatos()){

                traerUsuario();

            }else{

                Toast.makeText(MainActivity.this, "Debe escribir todos los datos", Toast.LENGTH_LONG).show();
            }
        }else if(v==txt_olvidaste_clave){

            Intent i= new Intent(MainActivity.this, RecuperarClave.class);
            startActivity(i);
        }
    }


    private boolean validarDatos() {
        return (!(user.getText().toString().equalsIgnoreCase(""))
                && !(clave.getText().toString().equalsIgnoreCase("")));
    }
    private void consultarPreferencias() {

        String correo="";
        SharedPreferences preferencia=getSharedPreferences("datos_incomeControl", Context.MODE_PRIVATE);
        correo= preferencia.getString("correo","");

 if(!correo.equalsIgnoreCase("")){
     Intent i = new Intent(MainActivity.this, menu.class);
     startActivity(i);
     finish();

 }
    }

    private void traerUsuario() {
        String url="https://www.comprasinteligentes.co/app.ventura/WebService/modelo/getUsuarioEmail.php?correo="+user.getText().toString();
        jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()>0){
                    try {
                        usuario= response.getJSONObject(0).get("correo").toString();
                        claveU=response.getJSONObject(0).get("clave").toString();
                        documento= response.getJSONObject(0).get("documento").toString();
                        celular=response.getJSONObject(0).get("celular").toString();
                        nombre=response.getJSONObject(0).get("nombre").toString();
                        id=response.getJSONObject(0).get("id").toString();
                      validarUser();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "El usuario no existe", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Volley Error",""+ error);
                Toast.makeText(MainActivity.this, "No se puede conectar a la base de datos", Toast.LENGTH_LONG).show();

            }
        });
        request.add(jsonArrayRequest);
    }

    private void validarUser() {

        if(claveU.equals(clave.getText().toString())){

            guardarPreferencias();

            Intent i = new Intent(MainActivity.this, menu.class);
            startActivity(i);
            finish();

        }else{
            Log.e(TAG, "validarUser: "+" clave U "+claveU+ " clave "+clave.getText().toString() );
            Toast.makeText(MainActivity.this, "Clave incorrecta", Toast.LENGTH_LONG).show();

        }
    }

    private void guardarPreferencias() {
        SharedPreferences preferencia=getSharedPreferences("datos_incomeControl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencia.edit();
        editor.putString("nombre",nombre);
        editor.putString("correo",usuario);
        editor.putString("celular",celular);
        editor.putString("documento",documento);
        editor.putString("id",id);
        editor.putString("clave",clave.getText().toString());
        editor.commit();

    }
}


