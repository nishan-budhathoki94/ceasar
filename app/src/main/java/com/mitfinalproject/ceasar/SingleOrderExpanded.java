package com.mitfinalproject.ceasar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mitfinalproject.ceasar.Customer.CartAdapter;
import com.mitfinalproject.ceasar.Customer.Checkout;
import com.mitfinalproject.ceasar.Data.CartDetails;
import com.mitfinalproject.ceasar.Data.CompleteOrderData;
import com.mitfinalproject.ceasar.Data.ItemData;
import com.mitfinalproject.ceasar.Data.OrderData;

import java.util.ArrayList;
import java.util.List;

public class SingleOrderExpanded extends AppCompatActivity {

    private TextView address,price;
    private RecyclerView orderedItems;
    private Button reorder;
    private List<OrderData> orderItems;
    private CompleteOrderData completeOrderData;
    private List<CartDetails> cartDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_expanded);
        cartDetails = new ArrayList<>();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        // set height to 40 percent of the screen
        int height =  (displaymetrics.heightPixels*80)/100;
        // set height to 70 percent of the screen
        int width = (displaymetrics.widthPixels*70)/100;

        address = findViewById(R.id.textViewOrderExpandedAddress);
        price = findViewById(R.id.textViewOrderExpandedPrice);
        reorder = findViewById(R.id.buttonReorder);
        orderedItems = findViewById(R.id._recycler_order_items_expanded);

        //on reoder clicked
        reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkout = new Intent(SingleOrderExpanded.this, Checkout.class);
                checkout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                checkout.putParcelableArrayListExtra("cart_details", (ArrayList<? extends Parcelable>) cartDetails);
                checkout.putExtra("address",completeOrderData.getAddress());
                startActivity(checkout);
            }
        });

        Intent intent = getIntent();
        if (intent.getParcelableExtra("orderData") != null){
            completeOrderData = intent.getParcelableExtra("orderData");
            for (int i=0;i<completeOrderData.getOrderData().size();i++) {
                ItemData itemData = new ItemData(completeOrderData.getOrderData().get(i).getName(),"","","",completeOrderData.getOrderData().get(i).getPrice()/completeOrderData.getOrderData().get(i).getQuantity(),"",completeOrderData.getOrderData().get(i).getQuantity());
                cartDetails.add(new CartDetails(itemData,completeOrderData.getTotal_items()));
            }
        }
        price.setText("Total Price: $"+completeOrderData.getPrice());
        address.setText(completeOrderData.getAddress());

        orderedItems.setLayoutManager(new LinearLayoutManager(this));

        //set recyclerview height half of the dialog box
        orderedItems.getLayoutParams().height = height/2;
        orderedItems.setHasFixedSize(true);

        CartAdapter rvAdapter = new CartAdapter(cartDetails, this);
        orderedItems.setAdapter(rvAdapter);
    }
}
