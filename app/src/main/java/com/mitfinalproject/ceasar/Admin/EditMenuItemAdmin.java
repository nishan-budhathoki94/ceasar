package com.mitfinalproject.ceasar.Admin;

import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.mitfinalproject.ceasar.Data.ItemData;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class EditMenuItemAdmin extends AppCompatActivity {

    private Spinner spinnerCategory,spinnerSize,spinnerAvailability;
    TextInputLayout textInputLayoutName,textInputLayoutDescription,textInputLayoutPrice;
    private long backPressed;
    private Toast backToast;
    String server_url;
    ProgressBar progressBar;
    ItemData singleItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_items);
        //get the item details received from the intent
        getIntentExtra();
        textInputLayoutName = findViewById(R.id.textInputLayoutMenuName);
        textInputLayoutName.getEditText().setText(singleItem.getName());
        textInputLayoutDescription = findViewById(R.id.textInputLayoutMenuDescription);
        textInputLayoutDescription.getEditText().setText(singleItem.getDesc());
        textInputLayoutPrice = findViewById(R.id.textInputLayoutMenuPrice);
        textInputLayoutPrice.getEditText().setText(String.valueOf(singleItem.getPrice()));
        progressBar = findViewById(R.id.progressBarMenuItem);
        //setting up each spinner to its relative arrays
        spinnerCategory =  findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(EditMenuItemAdmin.this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.categories));
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);
        spinnerCategory.setSelection(adapterCategory.getPosition(singleItem.getCategory()));

        spinnerSize =  findViewById(R.id.spinnerSize);
        ArrayAdapter<String> adapterSize = new ArrayAdapter<>(EditMenuItemAdmin.this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.size));
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(adapterSize);
        spinnerSize.setSelection(adapterSize.getPosition(singleItem.getSize()));

        spinnerAvailability =  findViewById(R.id.spinnerAvailability);
        ArrayAdapter<String> adapterAvailability = new ArrayAdapter<>(EditMenuItemAdmin.this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.availability));
        adapterAvailability.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailability.setAdapter(adapterAvailability);
        spinnerAvailability.setSelection(adapterAvailability.getPosition(singleItem.getAvailability()));
    }


    //when user hits done button
    public void addMenuItemBtn(View v) {
        progressBar.setVisibility(View.VISIBLE);
        server_url = "http://everestelectricals.com.au/ceasar/edit_item.php";
        if(validateEmpty(textInputLayoutName)&& validateEmpty(textInputLayoutPrice) && validatePrice()){
            singleItem.setAvailability(spinnerAvailability.getSelectedItem().toString());
            singleItem.setSize(spinnerSize.getSelectedItem().toString());
            singleItem.setCategory(spinnerCategory.getSelectedItem().toString());
            singleItem.setName(textInputLayoutName.getEditText().getText().toString());
            singleItem.setDesc(textInputLayoutDescription.getEditText().getText().toString().trim());
            singleItem.setPrice(Double.parseDouble(textInputLayoutPrice.getEditText().getText().toString().trim()));
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Item Edited Please check your item list to confirm", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(EditMenuItemAdmin.this,ItemListAdmin.class));

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
                    params.put("name",singleItem.getName());
                    params.put("desc", singleItem.getDesc());
                    params.put("size",singleItem.getSize());
                    params.put("availability",singleItem.getAvailability());
                    params.put("price",String.valueOf(singleItem.getPrice()));
                    params.put("category",singleItem.getCategory());
                    params.put("itemID", (String.valueOf(singleItem.getItemID())));
                    return params;
                }
            };

            VolleySingleton.getInstance(EditMenuItemAdmin.this).addToRequestQueue(request);
        }
    }

    public void getIntentExtra(){
        Intent intent = getIntent();
        singleItem = new ItemData(intent.getStringExtra("name"),
                intent.getStringExtra("category"),intent.getStringExtra("desc"),intent.getStringExtra("size"),
                intent.getDoubleExtra("price",0),intent.getStringExtra("availability"),intent.getIntExtra("itemID",0));
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
            case R.id.viewMenuItems:
                Intent intentViewItems = new Intent(this, ItemListAdmin.class);
                intentViewItems.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentViewItems);
                finish();
                break;
            case R.id.action_addMenuItem:
                Intent intent = new Intent(this, AddMenuItemAdmin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            case R.id.set_delivery_zone:
                Intent intentDeliverZone = new Intent(this, DeliveryZone.class);
                intentDeliverZone.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentDeliverZone);
                finish();
                break;

            case R.id.createEmployeeAccount:
                Intent intentEmployee = new Intent(EditMenuItemAdmin.this, SignUpEmployee.class);
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

    //validate email
    public boolean validateEmpty(TextInputLayout textInput) {
        String emailInput = textInput.getEditText().getText().toString().trim();

        if(emailInput.isEmpty()) {
            textInput.setError("Field cannot be empty");
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }

        else {
            textInput.setError(null);
            return true;
        }
    }

    //validate price
    public boolean validatePrice() {
        float price = Float.parseFloat(textInputLayoutPrice.getEditText().getText().toString().trim());
        if(price <=0) {
            textInputLayoutPrice.setError("Price cannot be zero");
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        else {
            textInputLayoutPrice.setError(null);
            return true;
        }
    }
}
