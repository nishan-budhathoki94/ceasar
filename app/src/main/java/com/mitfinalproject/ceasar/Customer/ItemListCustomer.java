package com.mitfinalproject.ceasar.Customer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mitfinalproject.ceasar.Customer.Fragments_Customer.FragmentDessert;
import com.mitfinalproject.ceasar.Customer.Fragments_Customer.FragmentDrink;
import com.mitfinalproject.ceasar.Customer.Fragments_Customer.FragmentEntree;
import com.mitfinalproject.ceasar.Customer.Fragments_Customer.FragmentPizza;
import com.mitfinalproject.ceasar.Customer.Fragments_Customer.FragmentSalad;
import com.mitfinalproject.ceasar.Data.CartDetails;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemListCustomer extends AppCompatActivity implements OnAddToCartListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private ConstraintLayout cartLayout;
    private TextView cartPrice,cartItemNumber,showCart;
    private long backPressed;
    private Toast backToast;
    private String apiKey = "AIzaSyCY5ouweav0eewMcEfiKHKGVXO2_XL8wGo";
    private String TAG = "CustomerList";
    private LatLngBounds deliveryZone;
    private String address = "";
    private List<CartDetails> cartItems = new ArrayList<>();
    private Dialog dialog;
    AutocompleteSupportFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_panel);

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        //fetch delivery address from DataBase
        fetchAddressFromDB();

        tabLayout = findViewById(R.id.tabLayoutCustomer);
        viewPager = findViewById(R.id.viewPagerCustomer);
        cartLayout = findViewById(R.id.layoutCartDetails);
        cartPrice = findViewById(R.id.textViewCartPrice);
        cartItemNumber = findViewById(R.id.textViewCartItems);
        showCart = findViewById(R.id.textViewCartShow);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adding fragments for each food category
        pagerAdapter.AddFragment(new FragmentEntree(),"Entree");
        pagerAdapter.AddFragment(new FragmentPizza(),"Pizza");
        pagerAdapter.AddFragment(new FragmentDrink(), "Drink");
        pagerAdapter.AddFragment(new FragmentSalad(),"Salad");
        pagerAdapter.AddFragment(new FragmentDessert(),"Dessert");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(tabSelectedListener(viewPager));

    }

    private TabLayout.OnTabSelectedListener tabSelectedListener(final ViewPager pager){
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
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
                Toast.makeText(getApplicationContext(), "You are already in the desired section", Toast.LENGTH_SHORT).show();
                break;

           case R.id.action_active_orders:
               Intent intentActiveOrders = new Intent(this, ActiveOrder.class);
               intentActiveOrders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intentActiveOrders);
               finish();
                break;
           case R.id.action_order_history:
                Intent intentHistory = new Intent(ItemListCustomer.this, OrderHistory.class);
                intentHistory.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHistory);
                finish();
                break;
        }
        return true;
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


    //implementing the method on add cart
    @Override
    public void onCartButtonClick(CartDetails cartDetails) {

        if (cartLayout.getVisibility() == View.GONE){
            cartLayout.setVisibility(View.VISIBLE);
            cartPrice.setText(String.valueOf(cartDetails.getPrice()));
            cartItemNumber.setText(String.valueOf(cartDetails.getNumber()));
        }
        else {
            cartPrice.setText(String.valueOf(Double.parseDouble(cartPrice.getText().toString())+ cartDetails.getPrice()));
            cartItemNumber.setText(String.valueOf(Integer.parseInt(cartItemNumber.getText().toString())+cartDetails.getNumber()));
        }
        Toast.makeText(this,"Items Added",Toast.LENGTH_SHORT).show();
        cartItems.add(cartDetails);

    }

    public void OnClickShowCart(View v){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        // set height to 40 percent of the screen
        int height =  (displaymetrics.heightPixels*40)/100;
        // set height to 70 percent of the screen
        int width = (displaymetrics.widthPixels*70)/100;

        //initialize dialog only for the first time
       if (dialog == null){
           dialog = new Dialog(this);
           dialog.getWindow().setLayout(width, height);
           dialog.setContentView(R.layout.dialog_cart);
           dialog.setCanceledOnTouchOutside(true);
           dialog.setCancelable(true);
       }
        dialog.show();

        TextView totalItems = dialog.findViewById(R.id.textViewTotalItems);
        TextView totalPrice = dialog.findViewById(R.id.textViewTotalPrice);
        Button checkout = dialog.findViewById(R.id.buttonCheckOut);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_cart);

        /* Specify the types of place data to return. */
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME));

        //setting up the autocomplete fragment for more relevant results
        autocompleteFragment.setCountry("AU");
        autocompleteFragment.setHint("Enter Delivery Address");
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        //if delivery address is not been retrieved, just set bias else restrict
        if(deliveryZone == null){
            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                    new LatLng(-37.947169064555, 144.79711754184),
                    new LatLng(-37.677373663491, 145.13862329865)));
        }
        else {
            autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(deliveryZone));
        }

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                autocompleteFragment.setText(place.getAddress());
                address = place.getAddress();
            }
            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getBaseContext(),"Error: "+status,Toast.LENGTH_SHORT).show();
            }

        });
        double price = 0;
        int quantity = 0;
        for(int i = 0;i<cartItems.size();i++){
            price += cartItems.get(i).getPrice();
            quantity += cartItems.get(i).getNumber();
        }

        totalItems.setText(String.valueOf(quantity));
        totalPrice.setText("$".concat(String.valueOf(price)));
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (autocompleteFragment.a.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(),"Please enter an address",Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                    OnCheckOutClick();
                }
            }
        });

        RecyclerView rvCart =  dialog.findViewById(R.id.recyclerViewCart);
        rvCart.setLayoutManager(new LinearLayoutManager(dialog.getContext()));

        //set recyclerview height half of the dialog box
        rvCart.getLayoutParams().height = height/2;
        rvCart.setHasFixedSize(true);

        CartAdapter rvAdapter = new CartAdapter(cartItems, this);
        rvCart.setAdapter(rvAdapter);

    }

    public void OnCheckOutClick() {
           Intent checkOutIntent = new Intent(this,Checkout.class);
           checkOutIntent.putParcelableArrayListExtra("cart_details", (ArrayList<? extends Parcelable>) cartItems);
           checkOutIntent.putExtra("address",address);
           startActivity(checkOutIntent);

    }

    //Fetch address from database
    public void fetchAddressFromDB(){
        String server_url = "http://everestelectricals.com.au/ceasar/get_address.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    double NELat, NELong, SWLat, SWLong;
                    String address;
                    LatLng NE, SW;
                    JSONArray jsonArray = response.getJSONArray("data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    NELat = jsonObject.getDouble("NELat");
                    NELong = jsonObject.getDouble("NELong");
                    SWLat = jsonObject.getDouble("SWLat");
                    SWLong = jsonObject.getDouble("SWLong");
                    address = jsonObject.getString("Address");
                    NE = new LatLng(NELat,NELong);
                    SW = new LatLng(SWLat,SWLong);
                    deliveryZone = new LatLngBounds(SW,NE);
                    Log.d("DeliveryAddress",deliveryZone.toString()+address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error..."+error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance(ItemListCustomer.this).addToRequestQueue(request);
    }
}
