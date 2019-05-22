package com.mitfinalproject.ceasar;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdmin extends AppCompatActivity {
    private Spinner spinnerCategory;
    private String name,category,desc,price,availability,size;
    private int itemID;
    private List<ItemData> itemListEntree,itemListSalad,itemListDrink,itemListDessert,itemListPizza,itemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_admin);
        if (Build.VERSION.SDK_INT >= 21){

        }
        fetchItemList();
        //setupList(itemListEntree);
        //assigning spinner to its designated array
        spinnerCategory =  findViewById(R.id.spinnerItemListCategory);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(ItemListAdmin.this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.categories));
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);

        //to display the menu items as per category selected through spinner
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        setupList(itemListEntree);
                        break;
                    case 1:
                        setupList(itemListPizza);
                        break;
                    case 2:
                        setupList(itemListSalad);
                        break;
                    case 3:
                        setupList(itemListDrink);
                        break;
                    case 4:
                        setupList(itemListDessert);
                        break;
                    default:
                        setupList(itemListEntree);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setupList(itemListEntree);
            }
        });

    }

    //setup recycler view according to the list received
    public void setupList(List<ItemData> itemList){
        RecyclerView menuListRecycler = findViewById(R.id.recyclerMenuItems);
        menuListRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        menuListRecycler.setLayoutManager(layoutManager);
        ItemListAdapter itemListAdapter = new ItemListAdapter(itemList);
        menuListRecycler.setAdapter(itemListAdapter);
        itemListAdapter.notifyDataSetChanged();
    }


    //fetch all the data for menu items from database
    public void fetchItemList(){
        String server_url = "http://everestelectricals.com.au/ceasar/get_menu_items.php";
        itemListEntree = new ArrayList<>();
        itemListPizza = new ArrayList<>();
        itemListSalad = new ArrayList<>();
        itemListDessert = new ArrayList<>();
        itemListDrink = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                name = jsonObject.getString("name").trim();
                                category = jsonObject.getString("category").trim();
                                desc = jsonObject.getString("desc").trim();
                                size = jsonObject.getString("size").trim();
                                itemID = jsonObject.getInt("itemID");
                                price = jsonObject.getString("price").trim();
                                availability = jsonObject.getString("availability").trim();

                                //populate the fetched data based on the category
                                if(category.equals("Entree") && !availability.equals("No")){
                                    itemListEntree.add(new ItemData(name,category,desc,size,price,availability,itemID));
                                }
                                else if (category.equals("Salads") && !availability.equals("No")) {
                                    itemListSalad.add(new ItemData(name,category,desc,size,price,availability,itemID));
                                }
                                else if (category.equals("Desserts") && !availability.equals("No")) {
                                    itemListDessert.add(new ItemData(name,category,desc,size,price,availability,itemID));
                                }
                                else if (category.equals("Drinks") && !availability.equals("No")) {
                                    itemListDrink.add(new ItemData(name,category,desc,size,price,availability,itemID));
                                }
                                else if (category.equals("Pizza") && !availability.equals("No")) {
                                    itemListPizza.add(new ItemData(name, category, desc, size, price, availability, itemID));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley", "Cannot connect to database");
                    }
                }) {

        };

        VolleySingleton.getInstance(ItemListAdmin.this).addToRequestQueue(request);
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

            case R.id.action_addMenuItem:
                finish();
                startActivity(new Intent(this, AddMenuItemAdmin.class));
                break;
            case R.id.viewMenuItems:
                Toast.makeText(getApplicationContext(), "You are already viewing the menu items", Toast.LENGTH_SHORT).show();
                break;
            case R.id.createEmployeeAccount:
                finish();
                startActivity(new Intent(ItemListAdmin.this,SignUpEmployee.class));
                break;
        }
        return true;
    }
}
