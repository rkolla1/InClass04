package com.example.inclass_04b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
//mport android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

public class New_Card extends AppCompatActivity {
    Stripe stripe;
    static String Publishable_Key = "pk_test_stRHfmkLnCKkhSemPFwlWMdN00HjLDZzsq";
    CardMultilineWidget cardwidget;
    int amt;
    String token;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__card2);
        final Button pay = findViewById(R.id.pay);
        Intent intent=getIntent();
        mPref= PreferenceManager.getDefaultSharedPreferences(this);
        token=mPref.getString("token","");
        amt = (int)intent.getExtras().getDouble("amount");
        stripe = new Stripe(this, Publishable_Key);
        cardwidget=findViewById(R.id.card_widget);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oncardsaved();
            }
        });

    }

    private void oncardsaved() {
        Log.d("Status", "Inside on card saved");
        final Card cardtosave = cardwidget.getCard();
        if (cardtosave != null) {
            //cardtosave=cardtosave.toBuilder().name("")
            tokenizeCard(cardtosave);
        }
    }

    private void tokenizeCard(Card card) {
        Log.d("Status", "Inside tokenizecard");
        stripe.createToken(card, new ApiResultCallback<Token>() {
            @Override
            public void onSuccess(@NonNull Token result) {
                request_parsing(result.getId());



            }

            @Override
            public void onError(@NonNull Exception e) {

            }
        });
    }
    private void request_parsing(String card_token){
        System.out.println("Inside request_parsing");
        RequestQueue queue= Volley.newRequestQueue(New_Card.this);
        String url="https://z1cbskq9w2.execute-api.us-east-2.amazonaws.com/default/payment?token="+token+"&amount="+amt*100+"&source="+card_token;
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Toast.makeText(New_Card.this, "Added_successfuly", Toast.LENGTH_SHORT).show();
                Intent i1=new Intent(New_Card.this,Card_List.class);
                i1.putExtra("amount",100);
                startActivity(i1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(New_Card.this,"Payment_unsuccessful",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);


    }
}
