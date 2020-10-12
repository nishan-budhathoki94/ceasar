package com.mitfinalproject.ceasar;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mitfinalproject.ceasar.Admin.ItemListAdmin;
import com.mitfinalproject.ceasar.Customer.ItemListCustomer;
import com.mitfinalproject.ceasar.Data.Constants;
import com.mitfinalproject.ceasar.Employee.ActiveOrdersEmployee;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    //instantiate to avoid null exception
    private String type = "customer";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.home);
        ConstraintLayout noInternetLayout = findViewById(R.id.layoutNoInternet);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (!hasInternet())
        {
            noInternetLayout.setVisibility(View.VISIBLE);
        }
        //if user was previously logged in
        if( mUser!= null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUpUser();
                }
            },1000);

        }
        else {
            //delaying the activity as splash screen
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Home.this, Login.class));
                    finish();
                }
            }, 2000);
        }

    }

    //check if the device has internet connection
    public boolean hasInternet(){
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() ){
            return true;
        }
        return false;
    }

    //restart the activity
    public void btnRetryClick(View view){
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    public void setUpUser(){
        String server_url = "http://everestelectricals.com.au/ceasar/getuser.php";
        StringRequest request = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String  response) {
                        try {
                            Log.d("jsonres", "onResponse: "+response);
                            JSONObject jo = new JSONObject(response);
                            JSONArray jsonArray = jo.getJSONArray("data");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            type = jsonObject.getString("type").trim();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (type.equalsIgnoreCase(Constants.TYPE_CUSTOMER)) {
                            Intent customerPanel = new Intent(Home.this, ItemListCustomer.class);
                            customerPanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(customerPanel);
                            finish();
                        }
                        else if (type.equalsIgnoreCase(Constants.TYPE_ADMIN)){
                            Intent adminPanel = new Intent(Home.this, ItemListAdmin.class);
                            adminPanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(adminPanel);
                        }
                        else{
                            Intent employee = new Intent(Home.this, ActiveOrdersEmployee.class);
                            employee.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(employee);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley", "Cannot connect to database");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", mUser.getEmail());
                return params;
            }
        };
        VolleySingleton.getInstance(Home.this).addToRequestQueue(request);
    }
}
