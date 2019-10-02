package com.example.api;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class list extends AppCompatActivity implements list_Adapter.OnItemClick {

    ListView listView;
    ArrayList <products> arrayList;
    ArrayList <products> arrayList1;
    ArrayList<products> list = new ArrayList<>();

    int pendingNotifications = 0;
    TextView badgeCounter;
    MenuItem menuItem;
    Button check;

    double amount = 0.0;

    String email,token;
    String jsondata;


    private SharedPreferences mPref;
    private SharedPreferences.Editor mEdit;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mEdit = mPref.edit();

        token = mPref.getString("token","");
        email = mPref.getString("email","");





        listView = (ListView) findViewById(R.id.listview);
        check = findViewById(R.id.check);

        check.setVisibility(View.INVISIBLE);


        try {
            JSONObject object = new JSONObject(readJSON());
            JSONArray array = object.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {

                JSONObject jsonObject = array.getJSONObject(i);

                products model = new products();

                model.setName(jsonObject.getString("name"));
                //Log.d("hi", jsonObject.getString("name"));

                model.setPrice(jsonObject.getString("price"));
                model.setImage(jsonObject.getString("photo"));

                list.add(model);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("hi", String.valueOf(list));
        list_Adapter adapter = new list_Adapter(this, list, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                Log.d("bye", list.get(position).getPrice());
                Toast.makeText(getApplicationContext(), list.get(position).getPrice(), Toast.LENGTH_SHORT).show();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(list.this, shopping.class);
                myIntent.putExtra("amt", amount);
                startActivity(myIntent);
                finish();

            }
        });

    }

    public String readJSON() {
        String json = null;
        try {
            // Opening data.json file
            InputStream inputStream = getAssets().open("discount.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            inputStream.read(buffer);
            inputStream.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return json;
        }
        return json;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        getMenuInflater().inflate(R.menu.log_out, menu);


        menuItem = menu.findItem(R.id.cart_menu);

        // check if any pending notification

            // if no pending notification remove badge
            //menuItem.setActionView(null);


            // if notification than set the badge icon layout
            menuItem.setActionView(R.layout.action_bar_notification_icon);
            // get the view from the nav item
            View view = menuItem.getActionView();
            // get the text view of the action view for the nav item
            badgeCounter = view.findViewById(R.id.badge_counter);
            // set the pending notifications valu
            badgeCounter.setText(String.valueOf(pendingNotifications));


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.item1){
            Toast.makeText(this,"logout",Toast.LENGTH_LONG).show();
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        String url = " https://z1cbskq9w2.execute-api.us-east-2.amazonaws.com/default/logout?email=" + email + "&token=" + token;
        System.out.println(url);
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener <String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("lol"+ response);
                String map_data = hash_gen(response);
                jsondata=map_data.substring(1,map_data.length()-1);


                try {
                    JSONObject j = new JSONObject(jsondata);


                    if (j.has("logged_out") && j.getString("logged_out").equals("true")) {
                        Toast.makeText(getApplicationContext(), "Logging out", Toast.LENGTH_SHORT).show();
                        mEdit.clear();
                        System.out.println("lololo"+mEdit);
                        Intent i = new Intent(list.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
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



    @Override
    public void onClick(String value){
        check.setVisibility(View.VISIBLE);
        double p = Double.parseDouble(value);
        amount = amount + p;
        pendingNotifications +=1;
        badgeCounter.setText(String.valueOf(pendingNotifications));
        check.setText("Checkout:$" + amount);
        //Toast.makeText(getApplicationContext(), (int) p,Toast.LENGTH_SHORT).show();

    }

    public String hash_gen(String response){
        String s2=response.replaceAll("\\\\","");
        return s2;
    }
}
