package com.mitfinalproject.ceasar.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mitfinalproject.ceasar.Data.CartDetails;
import com.mitfinalproject.ceasar.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ItemListViewHolder> {
    private List<CartDetails> cart;
    private Context mContext;


    public CartAdapter(List<CartDetails> cart, Context mContext) {
        this.cart = cart;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_cart_single,parent,false);
        return new CartAdapter.ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListViewHolder holder, int position) {
        CartDetails cartItem = cart.get(position);
        holder.name.setText(cartItem.getItemData().getName());
        holder.number.setText(String.valueOf(cartItem.getNumber()));
        holder.price.setText("$"+cartItem.getPrice());

    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    public class ItemListViewHolder extends RecyclerView.ViewHolder{
        private TextView name,number,price;

        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewItemName);
            number = itemView.findViewById(R.id.textViewNumber);
            price = itemView.findViewById(R.id.textViewCartPrice);
        }
    }
}
