package com.mitfinalproject.ceasar;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

public class AddMenuItemAdmin extends AppCompatActivity {

    private Spinner spinnerCategory,spinnerSize,spinnerAvailability;
    TextInputLayout textInputLayoutName,textInputLayoutDescription,textInputLayoutPrice;
    ProgressBar progressBar;
    String name,desc,category,size,server_url,availability,price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        server_url = "http://everestelectricals.com.au/ceasar/add_item.php";
        textInputLayoutName = findViewById(R.id.textInputLayoutMenuName);
        textInputLayoutDescription = findViewById(R.id.textInputLayoutMenuDescription);
        textInputLayoutPrice = findViewById(R.id.textInputLayoutMenuPrice);
        progressBar = findViewById(R.id.progressBarMenuItem);
        //setting up each spinner to its relative arrays
        spinnerCategory =  findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(AddMenuItemAdmin.this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.categories));
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);

        spinnerSize =  findViewById(R.id.spinnerSize);
        ArrayAdapter<String> adapterSize = new ArrayAdapter<>(AddMenuItemAdmin.this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.size));
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(adapterSize);

        spinnerAvailability =  findViewById(R.id.spinnerAvailability);
        ArrayAdapter<String> adapterAvailability = new ArrayAdapter<>(AddMenuItemAdmin.this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.availability));
        adapterAvailability.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailability.setAdapter(adapterAvailability);
    }


    //when user hits done button
    public void addMenuItemBtn(View v) {
        progressBar.setVisibility(View.VISIBLE);
        if(validateEmpty(textInputLayoutName)&& validateEmpty(textInputLayoutPrice) && validatePrice()){
            availability = spinnerAvailability.getSelectedItem().toString();
            size = spinnerSize.getSelectedItem().toString();
            category = spinnerCategory.getSelectedItem().toString();
            name = textInputLayoutName.getEditText().getText().toString();
            desc = textInputLayoutDescription.getEditText().getText().toString();
            price = textInputLayoutPrice.getEditText().getText().toString().trim();
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), response+"Please check your item list to confirm", Toast.LENGTH_LONG).show();
                            textInputLayoutDescription.getEditText().setText("");
                            textInputLayoutName.getEditText().setText("");
                            textInputLayoutPrice.getEditText().setText("");

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
                    params.put("name",name);
                    params.put("desc", desc);
                    params.put("size",size);
                    params.put("availability",availability);
                    params.put("price",price );
                    params.put("category",category);
                    return params;
                }
            };

            VolleySingleton.getInstance(AddMenuItemAdmin.this).addToRequestQueue(request);
        }
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
                finish();
                startActivity(new Intent(this,Login.class));
                break;
            case R.id.viewMenuItems:
                finish();
                startActivity(new Intent(this, ItemListAdmin.class));
                break;
            case R.id.action_addMenuItem:
                Toast.makeText(getApplicationContext(), "You are already in Add Menu Items", Toast.LENGTH_SHORT).show();
                break;
            case R.id.createEmployeeAccount:
                Intent createEmployee = new Intent(AddMenuItemAdmin.this,SignUp.class);
                createEmployee.putExtra("type","employee");
                finish();
                startActivity(createEmployee);
                break;

        }
        return true;
    }

    //validate email
    public boolean validateEmpty(TextInputLayout textInput) {
        String emailInput = textInput.getEditText().getText().toString().trim();

        if(emailInput.isEmpty()) {
            textInput.setError("Field cannot be empty");
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
            return false;
        }
        else {
            textInputLayoutPrice.setError(null);
            return true;
        }
    }
}
