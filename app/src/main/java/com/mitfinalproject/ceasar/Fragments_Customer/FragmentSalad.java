package com.mitfinalproject.ceasar.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mitfinalproject.ceasar.Customer.ItemListAdapterCustomer;
import com.mitfinalproject.ceasar.ItemData;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentSalad extends Fragment {

    private RecyclerView recyclerViewEntree;
    private List<ItemData> listSalad;
    private ItemData singleItem;
    ItemListAdapterCustomer itemListAdapter;
    private View v;

    public FragmentSalad() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_salad,container,false);
        recyclerViewEntree = v.findViewById(R.id.recyclerViewSalad);
        Log.d("FragmentEntree", "inside oncreate view: ");
        recyclerViewEntree.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        itemListAdapter = new ItemListAdapterCustomer(listSalad,this.getActivity());
        recyclerViewEntree.setAdapter(itemListAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchItemList();
        Log.d("FragmentEntree", "list fetched: ");

    }

    //fetch all the data for menu items from database
    public void fetchItemList(){
        String server_url = "http://everestelectricals.com.au/ceasar/get_menu_items.php";
        listSalad = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++) {
                                singleItem = new ItemData();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                singleItem.setName(jsonObject.getString("name").trim());
                                singleItem.setCategory(jsonObject.getString("category").trim());
                                singleItem.setDesc(jsonObject.getString("desc").trim());
                                singleItem.setSize(jsonObject.getString("size").trim());
                                singleItem.setItemID(jsonObject.getInt("itemID"));
                                singleItem.setPrice(jsonObject.getString("price").trim());
                                singleItem.setAvailability(jsonObject.getString("availability").trim());

                                //populate the fetched data based on the category
                                if(singleItem.getCategory().equals("Salads") && !singleItem.getAvailability().equals("No")){
                                    listSalad.add(singleItem);
                                }

                            }
                            itemListAdapter.notifyDataSetChanged();
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

        VolleySingleton.getInstance(this.getContext()).addToRequestQueue(request);
    }
}
