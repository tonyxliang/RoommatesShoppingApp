package edu.uga.cs.roommatesshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static edu.uga.cs.roommatesshoppingapp.ManagementActivity.findUserAddress;

public class SettleCostActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "SettleCostActivity";

    private TextView totalBalanceText;
    private TextView roommateBalanceText;
    private Button clearBalanceButton;

//    Intent intent = getIntent();
//    String userAddress = intent.getExtras().getString("address");

    private static String userAddress = "";
    final static double[] totalBalance = {0};
    final static long[] numberOfRoommates = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_cost);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference addressRef = database.getReference("References").child(userKey);

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

        totalBalanceText = (TextView) findViewById( R.id.totalBalance);
        roommateBalanceText = (TextView) findViewById( R.id.roommateBalance);
        clearBalanceButton = (Button) findViewById( R.id.clearBalance );

//        final double[] totalBalance = {0};
//        final long[] numberOfRoommates = {1};

//        DatabaseReference ref = database.getReference(userAddress).child("Users");
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                // Once we have a DataSnapshot object, knowing that this is a list,
//                // we need to iterate over the elements and place them on a List.
//                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
//                    numberOfRoommates[0]++;
//                }
//                Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): Number of Roommates = " + numberOfRoommates[0]);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getMessage());
//            }
//        });

        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference ref = database2.getReference(userAddress).child("Users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, knowing that this is a list,
                // we need to iterate over the elements and place them on a List.
                numberOfRoommates[0] = snapshot.getChildrenCount();
                Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): Exists: " + snapshot.exists());
                Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): Has children: " + snapshot.hasChildren());
                Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): Roommates from snapshot= " + snapshot.getChildren());
                Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): Number of Roommates from snapshot= " + snapshot.getChildrenCount());
                Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): Number of Roommates = " + numberOfRoommates[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database3.getReference(userAddress).child("Purchased List").child("Items");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, knowing that this is a list,
                // we need to iterate over the elements and place them on a List.
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    double price = Double.parseDouble(item.getItemPrice());
                    int quantity = Integer.parseInt(item.getItemAmount());
                    totalBalance[0] += price*quantity;
                    Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): added: " + price*quantity);
                    Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): totalBalance: " + totalBalance[0]);
                }
                Log.d( DEBUG_TAG, "SettleCostActivity.onDataChange(): setting totalBalance");

                //setting the text for total balance and balance for each roommate
                totalBalanceText.setText("Total balance: $" + totalBalance[0]);
//                roommateBalanceText.setText("Balance for each roommate: $" + round(totalBalance[0]/ numberOfRoommates[0],2));
                roommateBalanceText.setText("Balance for each roommate: $" + totalBalance[0] + "/" + numberOfRoommates[0]);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        //doesn't display this (assuming it doesn't come here because is constant listening for data changes
        Log.d( DEBUG_TAG, "TotalBalance: " + totalBalance[0]);

        clearBalanceButton.setOnClickListener( new ButtonClickListener());
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(userAddress).child("Purchased List");


            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Show a quick confirmation
                    Toast.makeText(getApplicationContext(), "Balance cleared",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(view.getContext(), ManagementActivity.class);
                    view.getContext().startActivity(intent);
                    intent.putExtra("address", userAddress);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to clear balance",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}