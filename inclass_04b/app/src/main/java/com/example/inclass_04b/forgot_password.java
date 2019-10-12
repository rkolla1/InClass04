package com.example.inclass_04b;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class forgot_password extends AppCompatActivity {

    private String em;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

       final Button otp = findViewById(R.id.btn_otp);

        final Button reset = findViewById(R.id.btn_forgot);
        Button cancel = findViewById(R.id.btn_forgotcancel);
        final EditText email = findViewById(R.id.forgot_email);
        final EditText forgot_otp = findViewById(R.id.forgot_otp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(forgot_password.this,MainActivity.class);
                startActivity(i);
            }
        });

        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://139.59.24.102:5000/forgot_password?email=" + email.getText().toString();
                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);

                            if(jo.has("verification_sent")){
                                Toast.makeText(getApplicationContext(),"Otp Sent", Toast.LENGTH_SHORT).show();
                                em = jo.getString("email");
                                reset.setEnabled(true);

                            }
                            else if(jo.has("reason")){
                                Toast.makeText(getApplicationContext(),jo.getString("reason"), Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e){
                            System.out.println(e);
                        }
                    }


                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
                rq.add(sr);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("email" + em);
                System.out.println("otp"+ forgot_otp.getText());
                String url = "http://139.59.24.102:5000//login_with_otp?email=" + em + "&otp=" + forgot_otp.getText();
                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.has("otp_verified")){
                                System.out.println(jo.getString("otp_verified"));
                                if(jo.getString("otp_verified").equals("true")){
                                    Toast.makeText(getApplicationContext(),"otp verified", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(forgot_password.this,reset_password.class);
                                    i.putExtra("email",em);
                                    startActivity(i);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Invalid Otp", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (Exception e){
                            System.out.println(e);
                        }
                    }


                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
                rq.add(sr);

            }
        });
    }
}
