package com.example.api;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.HashMap;

import static lib.android.paypal.com.magnessdk.network.c.t;
import static lib.android.paypal.com.magnessdk.network.c.v;

public class shopping extends AppCompatActivity {

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEdit;
    String client_token;
    String email;
    String payemt_api="https://z1cbskq9w2.execute-api.us-east-2.amazonaws.com/default/payment/";
    String payment_nonce;
    String token;

    Button pay;
    static int REQUEST_CODE=1;
    String amt="100";
    double amtfinal;

    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        txt = findViewById(R.id.txt);

        Intent intent = getIntent();

        amtfinal = intent.getDoubleExtra("amt",0.0);
        Log.d("vineel", String.valueOf(amtfinal));

        txt.setText("Total Amount: $" + amtfinal );


        pay = findViewById(R.id.pay);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        client_token = mPref.getString("client_token","");
        token=mPref.getString("token","");
        email= mPref.getString("email","");



    }


    public void onBraintreeSubmit(View v){
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(client_token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce=result.getPaymentMethodNonce();
                payment_nonce=nonce.getNonce();
                Log.d("nonce",payment_nonce);

                sendpaymentdetails();
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }

    private void sendpaymentdetails() {
        String details = String.valueOf(amtfinal);
        Log.d("status","inside sendpaymentdetails");
        RequestQueue queue=Volley.newRequestQueue(this);
        String url="https://z1cbskq9w2.execute-api.us-east-2.amazonaws.com/default/payment/?email="+email+"&token="+token+"&amount="+details+"&nonce="+payment_nonce;
        Log.d("url",url);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener <String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                if (response.contains("true")) {
                    Log.d("status", "transaction successful");
                    Toast.makeText(shopping.this,"Transaction successfull",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(shopping.this,list.class);
                    startActivity(i);
                    finish();
                } else {
                    Log.d("status", "transaction unsuccessful");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Payement_status","error with contacting");
            }
        });
        queue.add(stringRequest);
    }
    }



