package com.mitfinalproject.ceasar.Employee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mitfinalproject.ceasar.CurrentDateFormat;
import com.mitfinalproject.ceasar.Customer.OrderListAdapter;
import com.mitfinalproject.ceasar.Data.CompleteOrderData;
import com.mitfinalproject.ceasar.Data.Constants;
import com.mitfinalproject.ceasar.Data.OrderData;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveOrdersEmployee extends AppCompatActivity {

    private RecyclerView activeOrderRecycler;
    private ProgressBar progressBar;
    private String server_url ="http://everestelectricals.com.au/ceasar/get_active_orders_employee.php";
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private List<CompleteOrderData> orderList;
    private CompleteOrderData singleOrder;
    private OrderListAdapter orderListAdapter;
    private List<OrderData> orderData;
    private long backPressed;
    private Toast backToast;
    private CurrentDateFormat currentDateFormat;
    private TextView title;
    private String name,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_order);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        activeOrderRecycler = findViewById(R.id.recyclerViewOrderCustomer);
        progressBar = findViewById(R.id.progressBarActiveOrderCustomer);
        title = findViewById(R.id.textView12);
        title.setText("Today's Active Orders");
        currentDateFormat = new CurrentDateFormat();

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
                            Log.d("ActiveOrderEmployee", " inside onResponse"+response);
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
                                name = jsonObject.getString("name");
                                phone = jsonObject.getString("phone");
                                if (jsonObject.has("items")){
                                    String stringItems = jsonObject.getString("items");
                                    JSONArray items = new JSONArray(stringItems);
                                    orderData = new ArrayList<>();
                                    for (int j=0;j<items.length();j++) {
                                        JSONObject itemObj = items.getJSONObject(j);
                                        orderData.add(new OrderData(itemObj.getInt("ID"),itemObj.getString("Name"),itemObj.getDouble("Price"),itemObj.getInt("Quantity")));
                                    }
                                    singleOrder.setOrderData(orderData);
                                }
                                orderList.add(singleOrder);
                            }
                            //loading the recycler only after the data is fetched from the database
                            orderListAdapter = new OrderListAdapter(orderList, ActiveOrdersEmployee.this,name,phone);
                            activeOrderRecycler.setAdapter(orderListAdapter);
                            orderListAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("ActiveOrderEmployee", " inside onResponse"+orderList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            title.setText("No Active Orders To Show");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        title.setText("No ORDERS TO SHOW");
                        error.printStackTrace();

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("date",currentDateFormat.getCurrentDate());
                params.put("status", Constants.STATUS_DELIVERED.toLowerCase());
                return params;
            }
        };

        VolleySingleton.getInstance(ActiveOrdersEmployee.this).addToRequestQueue(request);
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
        inflater.inflate(R.menu.employee,menu);
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

        }
        return true;
    }


}
