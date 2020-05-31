package com.App.incomecontrol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class RecuperarClave extends AppCompatActivity implements View.OnClickListener {
    TextView txt_correo;
    Button btn_siguiente;
    JsonArrayRequest jsonArrayRequest;
    StringRequest stringRequest;
    RequestQueue request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_clave);
        txt_correo= findViewById(R.id.txt_correo);
        btn_siguiente=findViewById(R.id.btn_siguiente);
        btn_siguiente.setOnClickListener(this);
        request= Volley.newRequestQueue(this);

    }

    @Override
    public void onClick(View v) {

        if(v==btn_siguiente){
            if(isEmailValid(this.txt_correo.getText().toString())){
                consultarCorreo();
            }else{
                Toast.makeText(RecuperarClave.this, "Email invalido, intente nuevamente.", Toast.LENGTH_LONG).show();
            }

        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void consultarCorreo() {

        String url="http://app.dasscol.com/WebService/modelo/getUsuario_correo.php?correo="+txt_correo.getText().toString();
        jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if(response.length()>0){

                    enviarEmail(response);
                    try {
                        Log.e("Response recuperar ", ""+response.getJSONObject(0).get("clave").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(RecuperarClave.this, "El usuario no se encuentra registrado", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Volley Error",""+ error);

                Toast.makeText(RecuperarClave.this, "No se puede conectar a la base de datos", Toast.LENGTH_LONG).show();

            }
        });
        request.add(jsonArrayRequest);

    }
    private void enviarEmail(final JSONArray response2) {


        String url="http://app.dasscol.com/WebService/modelo/sendEmail.php?";
        final ProgressDialog loading = ProgressDialog.show(this, "Un momento...", "Espere por favor");

        this.stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Log.e("Respuesta", "Esta es la respuesta: "+ response);

                new AlertDialog.Builder(RecuperarClave.this)
                        .setMessage("¡Listo! enviamos un Email con su clave.")
                        .setPositiveButton("ok)", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();

                            }
                        })
                        .show();

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

                String email, asunto, mensaje;
                email= RecuperarClave.this.txt_correo.getText().toString();
                asunto= "Hola, su clave para inicicar al App AccesControl es: ";
                mensaje="";
                try {
                    mensaje= "Su clave para iniciar sesion es: "+response2.getJSONObject(0).get("clave").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Map<String,String> parametros= new HashMap<>();
                parametros.put("email",email);
                parametros.put("asunto",asunto);
                parametros.put("mensaje",mensaje);


                return parametros;
            }
        };

        request.add(stringRequest);

    }
}
