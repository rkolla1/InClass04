package com.example.inclass_04b;

import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEdit;

    String token;
    String email;
    String jsondata;
    static String url = "https://z1cbskq9w2.execute-api.us-east-2.amazonaws.com/default";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mEdit = mPref.edit();


        token = mPref.getString("token","");



        System.out.println("token:"+token);
        if(!token.isEmpty()){
            System.out.println("here inside11");

            String log = url+"/tokenlogin?token="+token;
            RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
            StringRequest sr = new StringRequest(Request.Method.GET, log, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    String map_data = hash_gen(response);
                    jsondata=map_data.substring(1,map_data.length()-1);

                    try {
                        JSONObject jo = new JSONObject(jsondata);
                        System.out.println("vineel"+ jo);

                        if(jo.has("login") && jo.getString("login").equals("true")){
                            System.out.println("here inside");
                            Toast.makeText(getApplicationContext(),"Login successful",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this,list.class);
                            i.putExtra("response",response);
                            startActivity(i);
                            //finish();
                        }
                        else if(jo.has("message")){
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


        final EditText email = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signup);
        Button forgot = findViewById(R.id.forgot);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String log = url+"/restapi/?"+"email="+email.getText().toString()+"&password="+password.getText().toString();
                System.out.println(log);
                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                StringRequest sr = new StringRequest(Request.Method.GET, log, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("status","calling hash gen");
                            String map_data = hash_gen(response);
                            jsondata=map_data.substring(1,map_data.length()-1);

                            JSONObject jo = new JSONObject(jsondata);
                            Log.d("status","created the json object");



                            if(jo.has("login") && jo.getString("login").equals("true")){

                                mEdit.putString("token",jo.getString("token"));


                                mEdit.commit();
                                Toast.makeText(getApplicationContext(),"Login successful",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this,list.class);
                                i.putExtra("amount","100");
                                i.putExtra("pass",password.getText().toString());
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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Sign_Up.class);
                startActivity(i);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,forgot_password.class);
                startActivity(i);
            }
        });

    }
    public String hash_gen(String response){
        String s2=response.replaceAll("\\\\","");
        return s2;
    }
}


