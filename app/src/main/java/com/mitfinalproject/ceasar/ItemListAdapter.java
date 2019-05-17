package com.mitfinalproject.ceasar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder> {
    private List<ItemData> data;

    public ItemListAdapter(List<ItemData> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.singel_item,viewGroup,false);
        return new ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListViewHolder itemListViewHolder, int i) {
        ItemData itemData = data.get(i);
        itemListViewHolder.name.setText(itemData.getName());
        itemListViewHolder.desc.setText(itemData.getDesc());
        itemListViewHolder.price.setText("$"+itemData.getPrice());
        //hide the textview for size if its not applicable
        if(itemData.getSize().equals("None")){
            itemListViewHolder.size.setVisibility(View.INVISIBLE);
        }
        else{
            itemListViewHolder.size.setText("("+itemData.getSize()+")");
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ItemListViewHolder extends RecyclerView.ViewHolder{
        private TextView name,desc,price,size;

        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewSingleItemName);
            desc = itemView.findViewById(R.id.textViewSingleItemDescription);
            price = itemView.findViewById(R.id.textViewSingleItemPrice);
            size = itemView.findViewById(R.id.textViewSingleItemSize);
        }
    }
}
