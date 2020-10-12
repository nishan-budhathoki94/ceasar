package com.mitfinalproject.ceasar.Employee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.mitfinalproject.ceasar.Admin.EditMenuItemAdmin;
import com.mitfinalproject.ceasar.Admin.ItemListAdmin;
import com.mitfinalproject.ceasar.Admin.OrderHistoryAdmin;
import com.mitfinalproject.ceasar.Customer.CartAdapter;
import com.mitfinalproject.ceasar.Customer.Checkout;
import com.mitfinalproject.ceasar.Data.CartDetails;
import com.mitfinalproject.ceasar.Data.CompleteOrderData;
import com.mitfinalproject.ceasar.Data.Constants;
import com.mitfinalproject.ceasar.Data.ItemData;
import com.mitfinalproject.ceasar.Data.OrderData;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleOrderExpandedEmployee extends AppCompatActivity {

    private TextView address,price,name,phone;
    private RecyclerView orderedItems;
    private Button updateStatus;
    private CompleteOrderData completeOrderData;
    private List<CartDetails> cartDetails;
    private ProgressBar progressBar;
    private String  server_url = "http://everestelectricals.com.au/ceasar/update_order_status.php";
    private boolean admin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_expanded);
        cartDetails = new ArrayList<>();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        // set height to 40 percent of the screen
        int height =  (displaymetrics.heightPixels*80)/100;


        address = findViewById(R.id.textViewOrderExpandedAddress);
        price = findViewById(R.id.textViewOrderExpandedPrice);
        name = findViewById(R.id.textViewExpandedCustomerName);
        phone = findViewById(R.id.textViewExpandedCustomerPhone);
        updateStatus = findViewById(R.id.buttonReorder);
        orderedItems = findViewById(R.id._recycler_order_items_expanded);
        progressBar = findViewById(R.id.progressBarOrderExpanded);


        updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus();
                Intent activeOrders;
                if (admin) {
                    activeOrders = new Intent(SingleOrderExpandedEmployee.this, OrderHistoryAdmin.class);
                }
                else {
                    activeOrders = new Intent(SingleOrderExpandedEmployee.this, ActiveOrdersEmployee.class);
                }
                activeOrders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activeOrders);
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent.getParcelableExtra("orderData") != null){
            completeOrderData = intent.getParcelableExtra("orderData");
            for (int i=0;i<completeOrderData.getOrderData().size();i++) {
                ItemData itemData = new ItemData(completeOrderData.getOrderData().get(i).getName(),"","","",completeOrderData.getOrderData().get(i).getPrice()/completeOrderData.getOrderData().get(i).getQuantity(),"",completeOrderData.getOrderData().get(i).getID());
                cartDetails.add(new CartDetails(itemData,completeOrderData.getOrderData().get(i).getQuantity()));
            }
        }
        if (intent.getStringExtra("name") !=  null) {
            name.setVisibility(View.VISIBLE);
            name.setText("Customer: "+intent.getStringExtra("name").toUpperCase());
        }
        if (intent.getStringExtra("phone") !=  null) {
            phone.setVisibility(View.VISIBLE);
            phone.setText("Phone: "+intent.getStringExtra("phone"));
        }
        if (intent.getBooleanExtra("admin",false) == true){
            admin = true;
        }

        price.setText("Total Price: $"+completeOrderData.getPrice());
        address.setText(completeOrderData.getAddress());

        //hide the button if the order is delivered
        if (completeOrderData.getStatus().equalsIgnoreCase(Constants.STATUS_DELIVERED)){
            updateStatus.setVisibility(View.INVISIBLE);
        }
        updateStatus.setText(getUpdatedStatus());

        orderedItems.setLayoutManager(new LinearLayoutManager(this));

        //set recyclerview height half of the dialog box
        orderedItems.getLayoutParams().height = height/2;
        orderedItems.setHasFixedSize(true);

        CartAdapter rvAdapter = new CartAdapter(cartDetails, this);
        orderedItems.setAdapter(rvAdapter);
    }


    public void updateOrderStatus() {
        progressBar.setVisibility(View.VISIBLE);
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Order Status Updated to: "+getUpdatedStatus() , Toast.LENGTH_LONG).show();
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Error..."+error.toString(), Toast.LENGTH_SHORT).show();
                            error.printStackTrace();

                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id",String.valueOf(completeOrderData.getOrderID()));
                    params.put("status",getUpdatedStatus().toLowerCase());
                    return params;
                }
            };

            VolleySingleton.getInstance(SingleOrderExpandedEmployee.this).addToRequestQueue(request);
        }

        public String getUpdatedStatus() {

            switch (completeOrderData.getStatus().toUpperCase()){
                case Constants.STATUS_RECEIVED:
                    return Constants.STATUS_COOKING;

                case Constants.STATUS_COOKING:
                    return Constants.STATUS_ONITSWAY;

                case Constants.STATUS_ONITSWAY:
                    return Constants.STATUS_DELIVERED;

                default:
                    return Constants.STATUS_DELIVERED;
            }

        }


}
