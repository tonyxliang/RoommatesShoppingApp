package edu.uga.cs.roommatesshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static edu.uga.cs.roommatesshoppingapp.ManagementActivity.findUserAddress;

/**
 * This activity allows users to update items in the purchased list.
 * They can edit some details, cancel the item from the list, or delete it.
 * This was created with the help of Dr. Kochut's sample app.
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class UpdatePurchasedItemActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "UpdatePurchItemActivity";

    private EditText itemName;
    private EditText itemPrice;
    private EditText itemAmount;
    private EditText names;
    private Button updateItem;
    private Button deleteItem;
    private Button cancelPurchased;

    private String oldItemName;
    private String oldItemPrice;
    private String oldItemAmount;
    private String oldNames;

//    Intent intent = getIntent();
//    String userAddress = intent.getExtras().getString("address");

    private static String userAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_purchased_item);

        //implemented by importing from ManagementActivity
//        findUserAddress();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference addressRef = database.getReference("References").child(userKey);

        // Attach a listener to read the data at our posts reference
        addressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userAddress = (String) dataSnapshot.getValue();
                Log.d(DEBUG_TAG, "User address snapshot value: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        Log.d(DEBUG_TAG, "User Address found: " + userAddress);

        oldItemName = getIntent().getStringExtra("itemName");
        oldItemPrice = getIntent().getStringExtra("itemPrice");
        oldItemAmount = getIntent().getStringExtra("itemAmount");
        oldNames = getIntent().getStringExtra("names");

        itemName = findViewById(R.id.itemName);
        itemName.setEnabled(false);
        itemPrice = findViewById(R.id.itemPrice);
        itemAmount = findViewById(R.id.itemAmount);
        names = findViewById(R.id.names);
        updateItem = findViewById(R.id.updateItem);
        deleteItem = findViewById(R.id.deleteItem);
        cancelPurchased = findViewById(R.id.cancelPurchased);

        itemName.setText(oldItemName);
        itemPrice.setText(oldItemPrice);
        itemAmount.setText(oldItemAmount);
        names.setText(oldNames);

        updateItem.setOnClickListener(new ButtonClickListener());
        deleteItem.setOnClickListener(new DeleteButtonClickListener());
        cancelPurchased.setOnClickListener(new CancelPurchasedButtonClickListener());
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String newItemName = itemName.getText().toString();
            String newItemPrice = itemPrice.getText().toString();
            String newItemAmount = itemAmount.getText().toString();
            String newNames = names.getText().toString();
            final Item item = new Item(newItemName, newItemPrice, newItemAmount, newNames);

            // Add a new element to the list of items in Firebase.
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userAddress).child("Purchased List").child("Items").child(oldItemName);

            //update existing DB entries based on item name
            Map<String, Object> updates = new HashMap<String, Object>();

            updates.put("itemName", newItemName);
            updates.put("itemPrice", newItemPrice);
            updates.put("itemAmount", newItemAmount);
            updates.put("names", newNames);

            ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Show a quick confirmation
                    Toast.makeText(getApplicationContext(), "Item updated for " + item.getItemName(),
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(v.getContext(), ViewPurchasedListActivity.class);
                    intent.putExtra("address", userAddress);
                    v.getContext().startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to update item for " + item.getItemName(),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });


            // First, a call to push() appends a new node to the existing list (one is created
            // if this is done for the first time).  Then, we set the value in the newly created
            // list node to store the new item.
            // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
            // the previous apps to maintain items.
/*            myRef.push().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Show a quick confirmation
                    Toast.makeText(getApplicationContext(), "Item created for " + item.getItemName(),
                            Toast.LENGTH_SHORT).show();

*//*                    // Clear the EditTexts for next use.
                    itemName.setText("");
                    itemPrice.setText("");
                    itemAmount.setText("");
                    names.setText("");*//*
                    Intent intent = new Intent(v.getContext(), ViewListActivity.class);
                    v.getContext().startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to create an item for " + item.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

    private class DeleteButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userAddress).child("Purchased List").child("Items").child(oldItemName);

            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Show a quick confirmation
                    Toast.makeText(getApplicationContext(), "Item deleted",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(view.getContext(), ViewPurchasedListActivity.class);
                    intent.putExtra("address", userAddress);
                    view.getContext().startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to delete item",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private class CancelPurchasedButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String newItemName = itemName.getText().toString();
            String newItemPrice = itemPrice.getText().toString();
            String newItemAmount = itemAmount.getText().toString();
            String newNames = names.getText().toString();
            final Item item = new Item(newItemName, newItemPrice, newItemAmount, newNames);

            // Add a new element (Item) to the list of items in Firebase.
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(userAddress).child("Shopping List");

            // First, a call to push() appends a new node to the existing list (one is created
            // if this is done for the first time).  Then, we set the value in the newly created
            // list node to store the new item.
            // This listener will be invoked asynchronously
            myRef.child("Items").child(newItemName).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                //myRef.push().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Show a quick confirmation
                    Toast.makeText(getApplicationContext(), "Item canceled",
                            Toast.LENGTH_SHORT).show();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userAddress).child("Purchased List").child("Items").child(oldItemName);
                    ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Item removed from purchased list",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to remove item from purchased list",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent intent = new Intent(v.getContext(), ViewPurchasedListActivity.class);
                    intent.putExtra("address", userAddress);
                    v.getContext().startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to cancel item",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}