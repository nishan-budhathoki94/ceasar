package com.mitfinalproject.ceasar.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mitfinalproject.ceasar.Data.CompleteOrderData;
import com.mitfinalproject.ceasar.Data.Constants;
import com.mitfinalproject.ceasar.Data.ItemData;
import com.mitfinalproject.ceasar.Data.OrderData;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveOrder extends AppCompatActivity {
    private RecyclerView activeOrderRecycler;
    private ProgressBar progressBar;
    private String server_url ="http://everestelectricals.com.au/ceasar/get_active_order.php";
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private List<CompleteOrderData> orderList;
    private CompleteOrderData singleOrder;
    private OrderListAdapter orderListAdapter;
    private List<OrderData> orderData;
    private long backPressed;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_order);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        activeOrderRecycler = findViewById(R.id.recyclerViewOrderCustomer);
        progressBar = findViewById(R.id.progressBarActiveOrderCustomer);

        fetchActiveOrders();
        activeOrderRecycler.setLayoutManager(new LinearLayoutManager(this));
    }


    public void fetchActiveOrders() {
        progressBar.setVisibility(View.VISIBLE);
        orderList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            JSONArray jsonArray = jo.getJSONArray("data");
                            for (int i = 0;i<jsonArray.length();i++) {
                                singleOrder = new CompleteOrderData();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                singleOrder.setOrderID(jsonObject.getInt("OrderID"));
                                singleOrder.setEmail(jsonObject.getString("email").trim());
                                singleOrder.setDate(jsonObject.getString("date").trim());
                                singleOrder.setAddress(jsonObject.getString("address").trim());
                                singleOrder.setNote(jsonObject.getString("notes"));
                                singleOrder.setPrice(jsonObject.getDouble("price"));
                                singleOrder.setTotal_items(jsonObject.getInt("total_items"));
                                singleOrder.setStatus(jsonObject.getString("status").trim());
                                if (jsonObject.has("items")){
                                    String stringItems = jsonObject.getString("items");
                                    JSONArray items = new JSONArray(stringItems);
                                    orderData = new ArrayList<>();
                                    for (int j=0;j<items.length();j++) {
                                        JSONObject itemObj = items.getJSONObject(j);
                                        orderData.add(new OrderData(itemObj.getInt("ID"),itemObj.getString("Name"),itemObj.getDouble("Price"),itemObj.getInt("Quantity")));
                                    }
                                }
                                orderList.add(singleOrder);
                            }
                            //loading the recycler only after the data is fetched from the database
                            orderListAdapter = new OrderListAdapter(orderList,ActiveOrder.this,"","");
                            activeOrderRecycler.setAdapter(orderListAdapter);
                            orderListAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("ActiveOrder", " inside onResponse"+orderList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Your Order List is empty", Toast.LENGTH_LONG).show();
                        }
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
                params.put("status", Constants.STATUS_DELIVERED.toLowerCase());
                return params;
            }
        };

        VolleySingleton.getInstance(ActiveOrder.this).addToRequestQueue(request);
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else {
            backToast= Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressed = System.currentTimeMillis();
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
                Intent intentMenu = new Intent(this, ItemListCustomer.class);
                intentMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentMenu);
                finish();
                break;

            case R.id.action_active_orders:
                Toast.makeText(getApplicationContext(), "You are already in the desired section", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_order_history:
                Intent intentHistory = new Intent(this, OrderHistory.class);
                intentHistory.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHistory);
                finish();
                break;
        }
        return true;
    }
}
