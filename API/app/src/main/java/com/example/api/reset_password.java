package com.example.api;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class reset_password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        final String email = getIntent().getExtras().getString("email");
        final EditText pass = findViewById(R.id.pass);
        final EditText retype = findViewById(R.id.re_typepass);
        Button reeset = findViewById(R.id.reset);

        reeset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://139.59.24.102:5000/reset_pass?email=" + email + "&password="+ pass.getText().toString() + "&confirm_pass=" + retype.getText().toString();
                System.out.println(url);
                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.has("reset_password")){
                                Toast.makeText(getApplicationContext(),"password reset successfully",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(reset_password.this,MainActivity.class);
                                startActivity(i);
                            }
                            else if(jo.has("reason")){
                                Toast.makeText(getApplicationContext(),jo.getString("reason"),Toast.LENGTH_SHORT).show();
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
