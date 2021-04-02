package edu.uga.cs.roommatesshoppingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all items.
 * This was created with the help of Dr. Kochut's sample app.
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemHolder> {

    public static final String DEBUG_TAG = "ItemRecyclerAdapter";

    private List<Item> itemList;

    private Context context;

    public ItemRecyclerAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView itemPrice;
        TextView itemAmount;
        TextView names;

        public ItemHolder(View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemAmount = itemView.findViewById(R.id.itemAmount);
            names = itemView.findViewById(R.id.names);
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
        return new ItemHolder(view);
    }

    // This method fills in the values of the Views to show a Item
    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Item item = itemList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + item);

        //get context of view and activity in which button will be pressed
        context = holder.itemView.getContext();
        String contextName = context.getClass().getSimpleName();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //contextName will change based on where the recyclerview card is clicked (Shopping or Purchased List)
                if (contextName.equals("ViewListActivity")) {
                    Intent intent = new Intent(context, UpdateItemActivity.class);
                    intent.putExtra("itemName", item.getItemName());
                    intent.putExtra("itemPrice", item.getItemPrice());
                    intent.putExtra("itemAmount", item.getItemAmount());
                    intent.putExtra("names", item.getNames());
                    v.getContext().startActivity(intent);
                    ((Activity)v.getContext()).finish();
                } else if (contextName.equals("ViewPurchasedListActivity")) {
                    Intent intent = new Intent(context, UpdatePurchasedItemActivity.class);
                    intent.putExtra("itemName", item.getItemName());
                    intent.putExtra("itemPrice", item.getItemPrice());
                    intent.putExtra("itemAmount", item.getItemAmount());
                    intent.putExtra("names", item.getNames());
                    v.getContext().startActivity(intent);
                    ((Activity)v.getContext()).finish();
                }
            }
        });

        holder.itemName.setText(item.getItemName());
        holder.itemPrice.setText("Price: $" + item.getItemPrice());
        holder.itemAmount.setText("Amount: " + item.getItemAmount());
        holder.names.setText("Buyer: " + item.getNames());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}