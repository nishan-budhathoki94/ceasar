package com.mitfinalproject.ceasar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CustomerPanel extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_panel);
        TextView txt = findViewById(R.id.textViewCustomerPanel);

    }
}
