package com.mitfinalproject.ceasar.Admin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

public class OrderHistoryAdmin extends AppCompatActivity {

    private RecyclerView activeOrderRecycler;
    private ProgressBar progressBar;
    private String server_url ="http://everestelectricals.com.au/ceasar/get_active_orders_employee.php";
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private List<CompleteOrderData> orderList;
    private CompleteOrderData singleOrder;
    private OrderListAdapter orderListAdapter;
    private List<OrderData> orderData;
    private DatePicker datePicker;
    private long backPressed;
    private Toast backToast;
    private CurrentDateFormat currentDateFormat;
    private String name,phone;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_admin);
        setContentView(R.layout.activity_order_history_admin);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        activeOrderRecycler = findViewById(R.id.recyclerViewOrderAdmin);
        progressBar = findViewById(R.id.progressBarActiveOrderCustomer);
        datePicker = findViewById(R.id.datePicker);
        currentDateFormat = new CurrentDateFormat();

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy");
                calendar.set(year,monthOfYear,dayOfMonth);
                String mydate = dateFormat.format(calendar.getTime());
                fetchActiveOrders(mydate);
            }
        });

        fetchActiveOrders(currentDateFormat.getCurrentDate());
        activeOrderRecycler.setLayoutManager(new LinearLayoutManager(this));
    }


    public void fetchActiveOrders(final String dateValue) {
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
                            orderListAdapter = new OrderListAdapter(orderList, OrderHistoryAdmin.this,name,phone);
                            activeOrderRecycler.setAdapter(orderListAdapter);
                            orderListAdapter.notifyDataSetChanged();
                            activeOrderRecycler.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("ActiveOrderEmployee", " inside onResponse"+orderList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                           Toast.makeText(OrderHistoryAdmin.this,"Order List Empty",Toast.LENGTH_LONG).show();
                           activeOrderRecycler.setVisibility(View.INVISIBLE);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OrderHistoryAdmin.this,"Database Error",Toast.LENGTH_LONG).show();
                        error.printStackTrace();

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("date",dateValue);
                params.put("status", Constants.STATUS_DELIVERED.toLowerCase());
                return params;
            }
        };

        VolleySingleton.getInstance(OrderHistoryAdmin.this).addToRequestQueue(request);
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
        inflater.inflate(R.menu.menu_admin_panel,menu);
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

            case R.id.action_addMenuItem:
                Intent intent = new Intent(this, AddMenuItemAdmin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            case R.id.set_delivery_zone:
                Toast.makeText(getApplicationContext(), "You are already viewing the menu items", Toast.LENGTH_SHORT).show();
                break;

            case R.id.viewMenuItems:
                Intent intentViewItems = new Intent(this, ItemListAdmin.class);
                intentViewItems.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentViewItems);
                finish();
                break;
            case R.id.createEmployeeAccount:
                Intent intentEmployee = new Intent(OrderHistoryAdmin.this, SignUpEmployee.class);
                intentEmployee.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentEmployee);
                finish();
                break;
            case R.id.view_orders:
                Toast.makeText(getApplicationContext(), "You are already viewing the orders", Toast.LENGTH_SHORT).show();
                break;


        }
        return true;
    }
}
