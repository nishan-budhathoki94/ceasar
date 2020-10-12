package com.mitfinalproject.ceasar.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.mitfinalproject.ceasar.CurrentDateFormat;
import com.mitfinalproject.ceasar.Data.CartDetails;
import com.mitfinalproject.ceasar.Data.Constants;
import com.mitfinalproject.ceasar.Data.OrderData;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checkout extends AppCompatActivity {


    private static final int REQUEST_CODE = 1234;
    private String API_GET_TOKEN="http://everestelectricals.com.au/ceasar/braintree/get_token.php";
    private String API_CheckOUT="http://everestelectricals.com.au/ceasar/braintree/checkout.php";
    private String token,date,server_url = "http://everestelectricals.com.au/ceasar/set_order.php";
    private double amount;
    private int total_items;
    private List<CartDetails> cartDetails = new ArrayList<>();
    private HashMap<String,String> paramsHash;
    private Button btn_pay;
    private TextView price,numberOfItems,address;
    private EditText deliveyNote;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private CurrentDateFormat currentDate = new CurrentDateFormat();
    private ProgressBar progressBar;
    private String jSon;
    private List<OrderData> orderDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        price = findViewById(R.id.textViewCheckOutPrice);
        numberOfItems = findViewById(R.id.textViewCheckoutItems);
        address = findViewById(R.id.textViewCheckOutAddress);
        btn_pay = findViewById(R.id.buttonPay);
        deliveyNote = findViewById(R.id.editTextCheckOutDeliverNote);
        progressBar = findViewById(R.id.progressBarPayment);

        if (getIntent().getParcelableArrayListExtra("cart_details") != null) {
            cartDetails = getIntent().getParcelableArrayListExtra("cart_details");
            orderDataList = new ArrayList<>();
            //loop through each element of the cart
            for (int i =0;i<cartDetails.size();i++){
                orderDataList.add(new OrderData(cartDetails.get(i).getItemData().getItemID(),
                        cartDetails.get(i).getItemData().getName(),
                        cartDetails.get(i).getPrice(),cartDetails.get(i).getNumber()));
                total_items += cartDetails.get(i).getNumber();
                amount += cartDetails.get(i).getPrice();
            }
            //converting to json string to inject into database
            Gson gson = new Gson();
            jSon = gson.toJson(orderDataList);
        }

        address.setText(getIntent().getStringExtra("address"));
        numberOfItems.setText(String.valueOf(total_items));
        price.setText("$" +amount);


        new getToken().execute();

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
            }
        });

    }

    private void submitPayment(){
        if(amount>0)
        {
            DropInRequest dropInRequest=new DropInRequest().clientToken(token);
            startActivityForResult(dropInRequest.getIntent(this),REQUEST_CODE);
        }
        else
            Toast.makeText(this, "Amount is not valid", Toast.LENGTH_SHORT).show();

    }

    private void sendPayments(){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, API_CheckOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("Successful")){
                            injectIntoDB();
                        }
                        else {
                            Toast.makeText(Checkout.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Response",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Err",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(paramsHash==null)
                    return null;
                Map<String,String> params=new HashMap<>();
                for(String key:paramsHash.keySet())
                {
                    params.put(key,paramsHash.get(key));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("Content-type","application/x-www-form-urlencoded");
                return params;
            }
        };
        RetryPolicy mRetryPolicy=new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.getInstance(Checkout.this).addToRequestQueue(stringRequest);
    }

    private class getToken extends AsyncTask {
        ProgressDialog mDailog;

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client=new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    mDailog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            token = responseBody;
                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    mDailog.dismiss();
                    Log.d("Err",exception.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDailog=new ProgressDialog(Checkout.this,android.R.style.Theme_DeviceDefault_Light_Dialog);
            mDailog.setCancelable(false);
            mDailog.setMessage("Loading Wallet, Please Wait");
            mDailog.show();
        }

        @Override
        protected void onPostExecute(Object o){
            super.onPostExecute(o);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== REQUEST_CODE){
            if(resultCode==RESULT_OK)
            {
                progressBar.setVisibility(View.VISIBLE);
                DropInResult result=data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce= result.getPaymentMethodNonce();
                String strNounce = nonce.getNonce();
                    paramsHash=new HashMap<>();
                    paramsHash.put("amount",String.valueOf(amount));
                    paramsHash.put("nonce",strNounce);
                    sendPayments();

            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "User cancelled", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Exception error=(Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Err",error.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_customer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intentLogout = new Intent(this, Login.class);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentLogout);
                finish();
                break;

           case R.id.action_view_menu:
               Intent intentMenu = new Intent(Checkout.this, ItemListCustomer.class);
               intentMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intentMenu);
               finish();
                break;
           case R.id.action_order_history:
                Intent intentHistory = new Intent(Checkout.this, OrderHistory.class);
                intentHistory.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHistory);
                finish();
                break;
            case R.id.action_active_orders:
                Intent intentActive = new Intent(Checkout.this, ActiveOrder.class);
                intentActive.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentActive);
                finish();
                break;
        }
        return true;
    }

    public void injectIntoDB(){
        StringRequest request = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Checkout.this, "Payment Success. "+response, Toast.LENGTH_LONG).show();
                        Log.d("JsonParsing", "onResponse: "+jSon);
                        progressBar.setVisibility(View.GONE);

                        //start new activity
                        Intent activeOrder = new Intent(Checkout.this,ActiveOrder.class);
                        activeOrder.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(activeOrder);
                        finish();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error..."+error.toString(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", mUser.getEmail());
                params.put("address",address.getText().toString());
                params.put("note",deliveyNote.getText().toString());
                params.put("status", Constants.STATUS_RECEIVED.toLowerCase());
                params.put("price",String.valueOf(amount));
                params.put("date", currentDate.getCurrentDate());
                params.put("total",String.valueOf(total_items));
                params.put("items",jSon);
                return params;
            }
        };

        VolleySingleton.getInstance(Checkout.this).addToRequestQueue(request);
    }

}