package com.mitfinalproject.ceasar.Admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mitfinalproject.ceasar.Data.ItemData;
import com.mitfinalproject.ceasar.R;

import java.util.List;

public class ItemListAdapterAdmin extends RecyclerView.Adapter<ItemListAdapterAdmin.ItemListViewHolder> {
    private List<ItemData> data;
    private Context mContext;

    public ItemListAdapterAdmin(List<ItemData> data, Context context) {
        this.data = data;
        mContext = context;
    }

    @NonNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_admin,viewGroup,false);
        return new ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListViewHolder itemListViewHolder, int i) {
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

        //setting up onclick event for each menu item
        itemListViewHolder.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editItem = new Intent(mContext,EditMenuItemAdmin.class);
                editItem.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editItem.putExtra("itemID",itemData.getItemID());
                editItem.putExtra("name",itemData.getName());
                editItem.putExtra("desc",itemData.getDesc());
                editItem.putExtra("category",itemData.getCategory());
                editItem.putExtra("price",itemData.getPrice());
                editItem.putExtra("size",itemData.getSize());
                editItem.putExtra("availability",itemData.getAvailability());
                mContext.startActivity(editItem);
                if (mContext instanceof Activity)
                    ((Activity)mContext).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ItemListViewHolder extends RecyclerView.ViewHolder{
        private TextView name,desc,price,size,editItem;

        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewSingleItemName);
            desc = itemView.findViewById(R.id.textViewSingleItemDescription);
            price = itemView.findViewById(R.id.textViewSingleItemPrice);
            size = itemView.findViewById(R.id.textViewSingleItemSize);
            editItem = itemView.findViewById(R.id.textViewEditItemAdmin);
        }
    }
}
