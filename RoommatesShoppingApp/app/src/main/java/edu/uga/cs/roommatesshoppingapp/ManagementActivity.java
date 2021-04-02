package edu.uga.cs.roommatesshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This activity allows users to add items to the shopping list
 * and view both shopping and purchased lists. They can also logout.
 * This was created with the help of Dr. Kochut's sample app.
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class ManagementActivity extends AppCompatActivity {

    private Button addItem;
    private Button viewList;
    private TextView signedInAs;
    private Button logout;
    private Button viewPurchased;
    private Button settleCost;

    private static final String DEBUG_TAG = "ManagementActivity";

    private static String userAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        addItem = findViewById(R.id.addItem);
        viewList = findViewById(R.id.viewList);
        signedInAs = findViewById(R.id.signedInAs);
        logout = findViewById(R.id.logout);
        viewPurchased = findViewById(R.id.viewPurchased);
        settleCost = findViewById(R.id.settleCost);

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

        addItem.setOnClickListener(new AddItemButtonClickListener());
        viewList.setOnClickListener(new ViewListButtonClickListener());
        logout.setOnClickListener(new LogoutButtonClickListener());
        viewPurchased.setOnClickListener(new ViewPurchasedButtonClickListener());
        settleCost.setOnClickListener(new SettleCostButtonClickListener());

        // Setup a listener for a change in the sign in status (authentication status change)
        // when it is invoked, check if a user is signed in and update the UI text view string,
        // as needed.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null) {
                    // User is signed in
                    Log.d(DEBUG_TAG, "onAuthStateChanged:signed_in:" + currentUser.getUid());
                    String userEmail = currentUser.getEmail();
                    signedInAs.setText("Signed in as: " + userEmail);
                } else {
                    // User is signed out
                    Log.d(DEBUG_TAG, "onAuthStateChanged:signed_out");
                    signedInAs.setText("Signed in as: not signed in");
                }
            }
        });
    }

    private class AddItemButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), AddItemActivity.class);
            intent.putExtra("address", userAddress);
            view.getContext().startActivity(intent);
        }
    }

    private class ViewListButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ViewListActivity.class);
            intent.putExtra("address", userAddress);
            view.getContext().startActivity(intent);
        }
    }

    private class LogoutButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.putExtra("address", userAddress);
            view.getContext().startActivity(intent);
            finish();
        }
    }

    private class ViewPurchasedButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ViewPurchasedListActivity.class);
            intent.putExtra("address", userAddress);
            view.getContext().startActivity(intent);
        }
    }

    private class SettleCostButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), SettleCostActivity.class);
            intent.putExtra("address", userAddress);
            view.getContext().startActivity(intent);
        }
    }

    public static void findUserAddress() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userKey = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("References").child(userKey);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userAddress = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


}