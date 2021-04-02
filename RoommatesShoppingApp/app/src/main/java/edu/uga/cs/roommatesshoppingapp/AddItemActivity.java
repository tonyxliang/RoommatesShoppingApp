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

import static edu.uga.cs.roommatesshoppingapp.ManagementActivity.findUserAddress;

/**
 * This activity is responsible for adding a new item to the shopping list.
 * This was created with the help of Dr. Kochut's sample app.
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class AddItemActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "AddItemActivity";

    private EditText itemName;
    private EditText itemPrice;
    private EditText itemAmount;
    private EditText names;
    private Button addItem;

//    Intent intent = getIntent();
//    String userAddress = intent.getExtras().getString("address");
    private static String userAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        //findUserAddress
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("References").child(userKey);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
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

        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemAmount = findViewById(R.id.itemAmount);
        names = findViewById(R.id.names);
        addItem = findViewById(R.id.addItem);

        addItem.setOnClickListener(new ButtonClickListener());
    }

    private class ButtonClickListener implements View.OnClickListener {
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
                    Toast.makeText(getApplicationContext(), "Item created for " + item.getItemName(),
                            Toast.LENGTH_SHORT).show();

                    // Clear the EditTexts for next use.
                    itemName.setText("");
                    itemPrice.setText("");
                    itemAmount.setText("");
                    names.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to create an item for " + item.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}