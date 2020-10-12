package com.mitfinalproject.ceasar.Customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mitfinalproject.ceasar.Admin.OrderHistoryAdmin;
import com.mitfinalproject.ceasar.Data.CompleteOrderData;
import com.mitfinalproject.ceasar.Employee.SingleOrderExpandedEmployee;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.SingleOrderExpanded;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ItemListViewHolder> {
    private List<CompleteOrderData> data;
    private Context mContext;
    private String phone,name;

    public OrderListAdapter(List<CompleteOrderData> data, Context context,String name,String phone) {
        this.data = data;
        mContext = context;
        this.phone = phone;
        this.name = name;
    }

    @NonNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_order_customer,viewGroup,false);
        Log.d("OrderAdapter", "oncreate layout");
        return new ItemListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final OrderListAdapter.ItemListViewHolder itemListViewHolder, int i) {
        final CompleteOrderData itemData = data.get(i);
        itemListViewHolder.ID.setText(String.valueOf(itemData.getOrderID()));
        itemListViewHolder.price.setText("$"+(itemData.getPrice()));
        itemListViewHolder.address.setText(itemData.getAddress());
        itemListViewHolder.total_items.setText(String.valueOf(itemData.getTotal_items()));
        itemListViewHolder.status.setText(itemData.getStatus().toUpperCase());
        itemListViewHolder.date.setText(itemData.getDate());
        if (!phone.isEmpty()){
            itemListViewHolder.rowName.setVisibility(View.VISIBLE);
            itemListViewHolder.rowPhone.setVisibility(View.VISIBLE);
            itemListViewHolder.name.setText(name.toUpperCase());
            itemListViewHolder.phone.setText(phone);
        }

        if (mContext.getClass() != ActiveOrder.class){
            itemListViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext.getClass() == OrderHistory.class) {
                        Intent order_expanded_customer = new Intent(mContext, SingleOrderExpanded.class);
                        order_expanded_customer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        order_expanded_customer.putExtra("orderData",itemData);
                        mContext.startActivity(order_expanded_customer);
                    }
                    else{
                        Intent order_expanded_admin_employee = new Intent(mContext, SingleOrderExpandedEmployee.class);
                        order_expanded_admin_employee.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        order_expanded_admin_employee.putExtra("orderData",itemData);
                        order_expanded_admin_employee.putExtra("name",name);
                        order_expanded_admin_employee.putExtra("phone",phone);
                        if (mContext.getClass() == OrderHistoryAdmin.class)
                            order_expanded_admin_employee.putExtra("admin",true);
                        mContext.startActivity(order_expanded_admin_employee);
                    }

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ItemListViewHolder extends RecyclerView.ViewHolder{
        private TextView ID,date,price,status,address,total_items,name,phone;
        private CardView parentLayout;
        private TableRow rowName,rowPhone;

        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            ID = itemView.findViewById(R.id.textViewOrderIDCustomer);
            address = itemView.findViewById(R.id.textViewOrderAddressCustomer);
            price = itemView.findViewById(R.id.textViewOrderPriceCustomer);
            status = itemView.findViewById(R.id.textViewOrderStatusCustomer);
            date = itemView.findViewById(R.id.textViewOrderDateCustomer);
            total_items = itemView.findViewById(R.id.textViewOrderTotalCustomer);
            parentLayout = itemView.findViewById(R.id.orderLayoutCustomer);
            name = itemView.findViewById(R.id.textViewOrderCustomerName);
            phone = itemView.findViewById(R.id.textViewOrderCustomerPhone);
            rowPhone = itemView.findViewById(R.id.rowCustomerPhone);
            rowName = itemView.findViewById(R.id.rowCustomerName);

        }
    }
}
