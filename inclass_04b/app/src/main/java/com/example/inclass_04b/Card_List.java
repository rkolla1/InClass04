package com.example.inclass_04b;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.stripe.android.model.Customer;
import com.stripe.android.model.PaymentMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Card_List extends AppCompatActivity {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEdit;
    int amt = 0;
    String Customer_token;
    ArrayList<Card_details> c_d1;
    String base_url="https://z1cbskq9w2.execute-api.us-east-2.amazonaws.com/default/retrieve?token=";
    String base_pay_url=" https://z1cbskq9w2.execute-api.us-east-2.amazonaws.com/default/pay";
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card__list);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        Customer_token=mPref.getString("token","");
        c_d1 = new ArrayList<Card_details>();
        listView=findViewById(R.id.l_view);
        list_parsing(Customer_token);
        System.out.println("list_length"+c_d1.size());
        Intent i1=getIntent();
        amt = (int)(i1.getExtras().getDouble("amount"));

        final Button add_card=findViewById(R.id.Add_Card);
        add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Card_List.this,New_Card.class);
                intent.putExtra("amount", amt);
                startActivity(intent);
            }
        });
    }
    private void list_parsing(final String token){
        RequestQueue queue= Volley.newRequestQueue(this);
        String url=base_url+token;

        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String jasondata=response.replaceAll("\\\\","");
                String j_data=jasondata.substring(1,jasondata.length()-1);
                try {
                    JSONObject j_obj=new JSONObject(j_data);
                    JSONObject j_obj2= (JSONObject) j_obj.get("cards");
                    JSONArray j_array=j_obj2.getJSONArray("data");
                    //System.out.println("Length"+j_array.length());
                    for(int i=0;i<j_array.length();i++){
                        Card_details card_details=new Card_details();
                        JSONObject j1=j_array.getJSONObject(i);
                        card_details.name=j1.getString("brand");
                        card_details.last_4=j1.getString("last4");
                        card_details.id=j1.getString("id");
                        c_d1.add(card_details);

                    }
                    SimpleArrayAdapter s_adapter=new SimpleArrayAdapter(Card_List.this,c_d1);
                    listView.setAdapter(s_adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Card_details c_d=(Card_details)listView.getItemAtPosition(i);
                            request_parsing(c_d.id);
                            Intent intent=new Intent(Card_List.this,list.class);
                            startActivity(intent);



                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        });
        queue.add(stringRequest);

    }
    private void request_parsing(String id){
        RequestQueue queue=Volley.newRequestQueue(this);
        String url=base_pay_url+"?amount="+amt+"&card="+id+"&token="+Customer_token;
        System.out.println(url);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("message  "+response);
                Toast.makeText(Card_List.this, "Payment successful", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Card_List.this,error.toString(),Toast.LENGTH_SHORT).show();

            }
        });

        queue.add(stringRequest);
    }
}
