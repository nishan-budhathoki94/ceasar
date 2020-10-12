package com.mitfinalproject.ceasar.Admin;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.SphericalUtil;
import com.mitfinalproject.ceasar.Customer.ItemListCustomer;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.SignUp;
import com.mitfinalproject.ceasar.VolleySingleton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeliveryZone extends AppCompatActivity {

    private long backPressed;
    private Toast backToast;
    private String apiKey = "AIzaSyCY5ouweav0eewMcEfiKHKGVXO2_XL8wGo";
    private String server_url = "http://everestelectricals.com.au/ceasar/set_delivery.php";
    private String Address;
    private static LatLng center;
    private LatLngBounds deliveryZone;
    private ProgressBar progressbar;
    private  AutocompleteSupportFragment autocompleteFragment;
    private EditText radius;
    private Button setAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_zone);

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        //mapping the design elements of the activity
        radius = findViewById(R.id.editTextRadius);
        setAddress = findViewById(R.id.buttonSetDeliveryZone);
        progressbar = findViewById(R.id.progressBarDelivery);

        //get delivery address from database
        fetchAddressFromDB();

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));


        //setting up the autocomplete fragment for more relevant results
        autocompleteFragment.setCountry("AU");
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(-37.814, 144.96332),
                new LatLng(-37.814, 144.96332)));

        setAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);

                if (autocompleteFragment.a.getText().toString().isEmpty() || center == null) {
                    Toast.makeText(getBaseContext(),"Address for new delivery zone not entered",Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.INVISIBLE);
                }
                else if (radius.getText().toString().isEmpty() || radius.getText().toString().equals("0")){
                    Toast.makeText(getBaseContext(),"Please enter a valid radius",Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.INVISIBLE);
                }
                else {
                       toBounds(Double.parseDouble(radius.getText().toString()) * 1000);
                       updateDeliveryZone();
                }
            }
        });

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("DeliveryZone", "Place: " + place.getAddress() + ", " + place.getLatLng());
                center = place.getLatLng();
                Address = place.getAddress();
                autocompleteFragment.setText(place.getAddress());
                Toast.makeText(getBaseContext(),"Address Selected "+place.getAddress(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Toast.makeText(getBaseContext(),"Error: "+status,Toast.LENGTH_SHORT).show();
                Log.d("Delivery Zone", "An error occurred: " + status);
            }
        });

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

    //insert into database
    public void updateDeliveryZone(){

        StringRequest request = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "New Delivery Zone Created", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Error..."+error.toString(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("NELat", String.valueOf(deliveryZone.northeast.latitude));
                params.put("SWLat", String.valueOf(deliveryZone.southwest.latitude));
                params.put("NELong", String.valueOf(deliveryZone.northeast.longitude));
                params.put("SWLong", String.valueOf(deliveryZone.southwest.longitude));
                params.put("rad",radius.getText().toString());
                params.put("address",Address);
                return params;
            }
        };
        VolleySingleton.getInstance(DeliveryZone.this).addToRequestQueue(request);
    }

    public void fetchAddressFromDB(){
        progressbar.setVisibility(View.VISIBLE);
        String server_url = "http://everestelectricals.com.au/ceasar/get_address.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    double NELat, NELong, SWLat, SWLong;
                    LatLng NE, SW;
                    JSONArray jsonArray = response.getJSONArray("data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    NELat = jsonObject.getDouble("NELat");
                    NELong = jsonObject.getDouble("NELong");
                    SWLat = jsonObject.getDouble("SWLat");
                    SWLong = jsonObject.getDouble("SWLong");
                    autocompleteFragment.setText(jsonObject.getString("Address"));
                    radius.setText(String.valueOf(jsonObject.getDouble("Radius")));
                    NE = new LatLng(NELat,NELong);
                    SW = new LatLng(SWLat,SWLong);
                    deliveryZone = new LatLngBounds(SW,NE);
                    Log.d("DeliveryAddress",deliveryZone.toString()+Address);
                    progressbar.setVisibility(View.INVISIBLE);
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
        VolleySingleton.getInstance(DeliveryZone.this).addToRequestQueue(request);
    }

    //create a circular region from the coordinates and radius provided
    public void toBounds(double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        deliveryZone = new LatLngBounds(southwestCorner, northeastCorner);
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
                Intent intentEmployee = new Intent(DeliveryZone.this, SignUpEmployee.class);
                intentEmployee.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentEmployee);
                finish();
                break;
            case R.id.view_orders:
                Intent viewOrders = new Intent(this, OrderHistoryAdmin.class);
                viewOrders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(viewOrders);
                finish();
                break;
        }
        return true;
    }

}
