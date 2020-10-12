package com.mitfinalproject.ceasar.Customer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mitfinalproject.ceasar.Data.CartDetails;
import com.mitfinalproject.ceasar.Data.ItemData;
import com.mitfinalproject.ceasar.R;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.List;

public class ItemListAdapterCustomer extends RecyclerView.Adapter<ItemListAdapterCustomer.ItemListViewHolder> {
    private List<ItemData> data;
    private Context mContext;
    private OnAddToCartListener mOnAddToCartListener;
    public ItemListAdapterCustomer(List<ItemData> data, Context context) {
        this.data = data;
        mContext = context;
        mOnAddToCartListener = (OnAddToCartListener) context;
    }

    @NonNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_customer,viewGroup,false);
        return new ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemListViewHolder itemListViewHolder, int i) {
        final ItemData itemData = data.get(i);
        itemListViewHolder.name.setText(itemData.getName());
        itemListViewHolder.desc.setText(itemData.getDesc());
        itemListViewHolder.price.setText(String.valueOf(itemData.getPrice()));

        //hide the textview for size if its not applicable
        if(itemData.getSize().equals("None")){
            itemListViewHolder.size.setVisibility(View.INVISIBLE);
        }
        else{
            itemListViewHolder.size.setText("("+itemData.getSize()+")");
        }

        itemListViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListViewHolder.addToCart.getVisibility() != View.VISIBLE) {
                    itemListViewHolder.addToCart.setVisibility(View.VISIBLE);
                    itemListViewHolder.numberPicker.setVisibility(View.VISIBLE);
                }
                else{
                    itemListViewHolder.addToCart.setVisibility(View.GONE);
                    itemListViewHolder.numberPicker.setVisibility(View.GONE);
                }
            }
        });

        //setting up onclick event for each menu item
        itemListViewHolder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartDetails cartDetails = new CartDetails(itemData,itemListViewHolder.numberPicker.getValue());
                mOnAddToCartListener.onCartButtonClick(cartDetails);
                Log.d("AddToCart", "onClick: "+cartDetails.getPrice()+itemListViewHolder.numberPicker.getValue());

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ItemListViewHolder extends RecyclerView.ViewHolder{
        private TextView name,desc,price,size;
        private NumberPicker numberPicker;
        private Button addToCart;
        private ConstraintLayout parentLayout;

        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewSingleItemNameCustomer);
            desc = itemView.findViewById(R.id.textViewSingleItemDescriptionCustomer);
            price = itemView.findViewById(R.id.textViewSingleItemPriceCustomer);
            size = itemView.findViewById(R.id.textViewSingleItemSizeCustomer);
            numberPicker =  itemView.findViewById(R.id.numberPicker);
            addToCart = itemView.findViewById(R.id.buttonAddToCart);
            parentLayout = itemView.findViewById(R.id.singleCustomerParentLayout);
        }
    }
}
