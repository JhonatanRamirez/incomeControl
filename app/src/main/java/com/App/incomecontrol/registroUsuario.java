package com.App.incomecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registroUsuario extends AppCompatActivity implements View.OnClickListener{

   private Button registro;
   private EditText nombre;
    private EditText correo;
    private EditText celular;
    private EditText documento;
    private EditText clave;
    private EditText claverep;
    private StringRequest stringRequest;
    private RequestQueue request;
    private JsonArrayRequest jsonArrayRequest;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        registro= findViewById(R.id.btn_registro);
        nombre=findViewById(R.id.txt_nombre);
        correo=findViewById(R.id.txt_correo);
        celular=findViewById(R.id.txt_celular);
        documento=findViewById(R.id.txt_documento);
        clave=findViewById(R.id.txt_clave);
        claverep=findViewById(R.id.txt_claverep);
        this.request= Volley.newRequestQueue(this);
        registro.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==registro){
            if(validarCampos()){
                //Se debe hacer la validacion de que el correo no exista en la base de datos
                if(isEmailValid(this.correo.getText().toString())){
                    emailNoExiste();
                }else{
                    Toast.makeText(registroUsuario.this, "Email invalido, intente nuevamente.", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(registroUsuario.this, "Debe registrar todos los datos", Toast.LENGTH_LONG).show();
            }

        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void emailNoExiste() {

        String url="https://app.dasscol.co/WebService/modelo/getUsuarioEmail.php?correo="+correo.getText().toString();
        jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if(response.length()>0){
                    Toast.makeText(registroUsuario.this, "El correo ya se encuentra registrado en la base de datos.", Toast.LENGTH_LONG).show();
                }else{

                      getValiteCC();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Volley Error",""+ error);
                Toast.makeText(registroUsuario.this, "No se puede conectar a la base de datos", Toast.LENGTH_LONG).show();

            }
        });
        request.add(jsonArrayRequest);


    }

    private void getValiteCC() {
        String url="https://app.dasscol.co/WebService/modelo/getUsuarioDocument.php?cc="+documento.getText().toString();
        jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if(response.length()>0){
                    Toast.makeText(registroUsuario.this, "El documento ya se encuentra registrado en la base de datos.", Toast.LENGTH_LONG).show();
                }else{
                    if(clave.getText().toString().equals(claverep.getText().toString())){

                        guardarBD();
                    }else{
                        Toast.makeText(registroUsuario.this, "Las claves no coinciden", Toast.LENGTH_LONG).show();
                        clave.setText("");
                        claverep.setText("");
                        clave.setSelected(true);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Volley Error",""+ error);
                Toast.makeText(registroUsuario.this, "No se puede conectar a la base de datos", Toast.LENGTH_LONG).show();

            }
        });
        request.add(jsonArrayRequest);
    }

    private boolean validarCampos() {

        return(!(nombre.getText().toString().equalsIgnoreCase(""))
            && !(correo.getText().toString().equalsIgnoreCase(""))
                && !(celular.getText().toString().equalsIgnoreCase(""))
                    && !(documento.getText().toString().equalsIgnoreCase(""))
                        &&!(clave.getText().toString().equalsIgnoreCase("")));

    }

    private void guardarBD() {
        String url="https://app.dasscol.co/WebService/modelo/setUsuario.php?";
        final ProgressDialog loading = ProgressDialog.show(this, "Registrando Persona...", "Espere por favor");

        this.stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Log.e("Respuesta de guardarBD", "Esta es la respuesta: "+response);
                getIdUser();
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

                Map<String,String> parametros= new HashMap<>();
                parametros.put("nombre",nombre.getText().toString());
                parametros.put("correo",correo.getText().toString());
                parametros.put("celular",celular.getText().toString());
                parametros.put("documento",documento.getText().toString());
                parametros.put("clave",clave.getText().toString());
                return parametros;
            }
        };

        request.add(stringRequest);
    }

    private void iniciarTemporizador() {
        final ProgressDialog loading = ProgressDialog.show(this, "Un momento...", "Estamos calculando su ubicación");
        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

            }
        }.start();
    }

    private void getIdUser() {

        String url="https://app.dasscol.co/WebService/modelo/getUsuarioEmail.php?correo="+correo.getText().toString();
        jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()>0){

                    try {

                        id= response.getJSONObject(0).get("id").toString();
                        guardarPreferencias();
                        Intent i= new Intent(registroUsuario.this, menu.class);
                        startActivity(i);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(registroUsuario.this, "El usuario no existe", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Volley Error",""+ error);

                Toast.makeText(registroUsuario.this, "No se puede conectar a la base de datos", Toast.LENGTH_LONG).show();

            }
        });
        request.add(jsonArrayRequest);

    }

    private void guardarPreferencias() {
        SharedPreferences preferencia=getSharedPreferences("datos_incomeControl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencia.edit();
        editor.putString("nombre",nombre.getText().toString());
        editor.putString("correo",correo.getText().toString());
        editor.putString("celular",celular.getText().toString());
        editor.putString("documento",documento.getText().toString());
        editor.putString("clave",clave.getText().toString());
        editor.putString("id",id);
        editor.commit();

    }
}
