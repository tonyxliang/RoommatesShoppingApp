package edu.uga.cs.roommatesshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

/**
 * This is the main activity responsible for letting users login and register
 * This was created with the help of Dr. Kochut's sample app.
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "MainActivity";

    private static final int RC_SIGN_IN = 123;

    private static String userAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.viewList);

        login.setOnClickListener(new LoginButtonClickListener());
        register.setOnClickListener(new RegisterButtonClickListener());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            mAuth.signOut();
        }


    }

    private class LoginButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // This is an example of how to use the AuthUI activity for signing in
            // Here, we are just using email/password sign in
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build()
            );

            // Start a sign in activity, but come back with a result
            // This is an example of how to use startActivityForResult
            // The last argument is the code of the result from this call
            // It is used later in the onActivityResult listener to identify
            // the result as a result from this startActivityForResult call.
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), RegistrationActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    // this is a listener (event handler) which is called by android when the
    // activity started by a startActivityForResult finishes and we come back
    // to the originating activity;  we need to get the result from the activity
    // that just ended.  The second argument is the code of the startActivityForResult
    // call (there may be several of them).
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(DEBUG_TAG, "Item: MainActivity.onActivityResult()" );

        // check if it is a sign in activity result
        if(requestCode == RC_SIGN_IN) {

            if(resultCode == RESULT_OK) {

                // Successfully signed in
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

                Log.i("FireBase Test", "Signed in as: " + user.getEmail());

                // after a successful sign in, start the management activity
                Intent intent = new Intent(this, ManagementActivity.class);
                intent.putExtra("address", userAddress);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in, e.g., by using the back button
                Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}