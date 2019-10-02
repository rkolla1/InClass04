package com.example.api;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

public class login extends AppCompatActivity {

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEdit;

    String email,token;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mEdit = mPref.edit();

        token = mPref.getString("token","");


        String response = getIntent().getExtras().getString("response");
        final String pass = getIntent().getExtras().getString("pass");


        final EditText username = findViewById(R.id.username_login);
        final EditText address = findViewById(R.id.address_login);
        final EditText age = findViewById(R.id.age_login);
        final EditText weight = findViewById(R.id.weight_login);
        final EditText password = findViewById(R.id.password_login);
        Button update = findViewById(R.id.btn_update);
        Button reset = findViewById(R.id.reset);
        Button logout = findViewById(R.id.logout);

        try {
            final JSONObject j = new JSONObject(response);
            username.setText(j.getString("username"));
            address.setText(j.getString("address"));
            age.setText(j.getString("age"));
            weight.setText(j.getString("weight"));
            password.setText(pass);
            email = j.getString("email");
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http:139.59.24.102:5000/modify_user?username="+ username.getText().toString() + "&password=" + password.getText().toString() + "&age=" +
                            age.getText().toString()+"&weight=" + weight.getText().toString() + "&address="+ address.getText().toString()+"&email=" + email;
                    System.out.println(url);
                    RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                    StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject j = new JSONObject(response);
                                if (j.has("Updated")) {
                                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),j.getString("reason"),Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                               Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                        }
                    });
                    rq.add(sr);
                }
            });
        }
        catch (Exception e){
            System.out.println(e);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = " https://z1cbskq9w2.execute-api.us-east-2.amazonaws.com/default/logout?email="+email +"?token="+ token;
                System.out.println(url);
                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject j = new JSONObject(response);
                            if (j.has("logged_out")) {
                                Toast.makeText(getApplicationContext(), "Logging out", Toast.LENGTH_SHORT).show();
                                mEdit.clear();
                                Intent i = new Intent(login.this,MainActivity.class);
                                startActivity(i);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
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
