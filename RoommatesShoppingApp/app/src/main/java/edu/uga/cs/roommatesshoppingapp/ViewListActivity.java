package edu.uga.cs.roommatesshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import static edu.uga.cs.roommatesshoppingapp.ManagementActivity.findUserAddress;

/**
 * This activity lists all items in the shopping list.
 * This was created with the help of Dr. Kochut's sample app.
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class ViewListActivity extends AppCompatActivity {

    public interface FirebaseSuccessListener {

        void onDataFound(boolean isDataFetched);

    }

    public static final String DEBUG_TAG = "ViewListActivity";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    private List<Item> itemList;

//    Intent intent = getIntent();
//    String userAddress = intent.getExtras().getString("address");

    private static String userAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

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
                Toast.makeText(getApplicationContext(), "Loaded", Toast.LENGTH_SHORT).show();
                userAddress = (String) dataSnapshot.getValue();
                Log.d(DEBUG_TAG, "User address snapshot value: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        Log.d(DEBUG_TAG, "User Address found: " + userAddress);

        recyclerView = findViewById(R.id.recyclerView);

        // use a linear layout manager for the recycler view
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // get a Firebase DB instance reference
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(userAddress).child("Shopping List").child("Items");

        itemList = new ArrayList<Item>();

        // Set up a listener (event handler) to receive a value for the database reference, but only one time.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method.
        // We can use this listener to retrieve the current list of Items.
        // Other types of Firebase listeners may be set to listen for any and every change in the database
        // i.e., receive notifications about changes in the data in real time (hence the name, Realtime database).
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in the previous apps
        // to maintain items.
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, knowing that this is a list,
                // we need to iterate over the elements and place them on a List.
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    itemList.add(item);
                    Log.d( DEBUG_TAG, "ViewListActivity.onCreate(): added: " + item);
                }
                Log.d( DEBUG_TAG, "ViewListActivity.onCreate(): setting recyclerAdapter");

                // Now, create a ItemRecyclerAdapter to populate a ReceyclerView to display the items
                recyclerAdapter = new ItemRecyclerAdapter(itemList);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

    }
}