package com.mitfinalproject.ceasar.Admin;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mitfinalproject.ceasar.Customer.ViewPagerAdapter;
import com.mitfinalproject.ceasar.Admin.Fragments_Admin.FragmentDessert;
import com.mitfinalproject.ceasar.Admin.Fragments_Admin.FragmentDrink;
import com.mitfinalproject.ceasar.Admin.Fragments_Admin.FragmentEntree;
import com.mitfinalproject.ceasar.Admin.Fragments_Admin.FragmentPizza;
import com.mitfinalproject.ceasar.Admin.Fragments_Admin.FragmentSalad;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;

public class ItemListAdmin extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private long backPressed;
    private Toast backToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_admin);
        tabLayout = findViewById(R.id.tabLayoutAdmin);
        viewPager = findViewById(R.id.viewPagerAdmin);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
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
                Intent intentDeliverZone = new Intent(this, DeliveryZone.class);
                intentDeliverZone.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentDeliverZone);
                finish();
                break;

            case R.id.viewMenuItems:
                Toast.makeText(getApplicationContext(), "You are already viewing the menu items", Toast.LENGTH_SHORT).show();
                break;
            case R.id.createEmployeeAccount:
                Intent intentEmployee = new Intent(ItemListAdmin.this, SignUpEmployee.class);
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
