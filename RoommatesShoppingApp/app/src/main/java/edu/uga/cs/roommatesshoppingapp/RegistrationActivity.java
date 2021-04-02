package edu.uga.cs.roommatesshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This activity is used to allow users to register.
 * This was created with the help of Dr. Kochut's sample app.
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class RegistrationActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "RegisterActivity";

    private EditText emailText;
    private EditText passwordText;
    private EditText fullNameText;
    private EditText addressText;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailText = findViewById(R.id.email);
        fullNameText = findViewById(R.id.fullName);
        addressText = findViewById(R.id.address);
        passwordText = findViewById(R.id.password);
        register = findViewById(R.id.viewList);

        register.setOnClickListener(new RegisterButtonClickListener());
    }

    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final String email = emailText.getText().toString();
            final String password = passwordText.getText().toString();

            String newUserEmail = emailText.getText().toString();
            String newUserFullName = fullNameText.getText().toString();
            String newUserAddress = addressText.getText().toString();
            final User user = new User(newUserEmail, newUserFullName, newUserAddress);

            final String userAddress = newUserAddress;

            // Add a new element (User) to the list of items in Firebase.
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(newUserAddress);

            // First, a call to push() appends a new node to the existing list (one is created
            // if this is done for the first time).  Then, we set the value in the newly created
            // list node to store the new item.
            // This listener will be invoked asynchronously
            myRef.child("Users").child(newUserFullName).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                //myRef.push().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Show a quick confirmation
                    Toast.makeText(getApplicationContext(), "User created for " + user.getFullName(),
                            Toast.LENGTH_SHORT).show();

                    // Clear the EditTexts for next use.
                    emailText.setText("");
                    passwordText.setText("");
                    fullNameText.setText("");
                    addressText.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to create a user for " + user.getFullName(),
                            Toast.LENGTH_SHORT).show();
                }
            });

            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final DatabaseReference ref = database.getReference("References");
            // This is how we can create a new user using an email/password combination.
            // Note that we also add an onComplete listener, which will be invoked once
            // a new user has been created by Firebase.  This is how we will know the
            // new user creation succeeded or failed.
            // If a new user has been created, Firebase already signs in the new user;
            // no separate sign in is needed.
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(getApplicationContext(),
                                "Registered user: " + email,
                                Toast.LENGTH_SHORT).show();

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(DEBUG_TAG, "createUserWithEmail: success");

                        FirebaseUser user = mAuth.getCurrentUser();

                        // Add a new element (reference) to the list of items in Firebase.

                        FirebaseUser loggedInUser = mAuth.getCurrentUser();
                        String userKey = loggedInUser.getUid();
                        String userEmail = loggedInUser.getEmail();

                        // First, a call to push() appends a new node to the existing list (one is created
                        // if this is done for the first time).  Then, we set the value in the newly created
                        // list node to store the new item.
                        // This listener will be invoked asynchronously
                        ref.child(userKey).setValue(userAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                            //myRef.push().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Show a quick confirmation
                                Toast.makeText(getApplicationContext(), "Reference User created for " + userEmail,
                                        Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to create a user for " + userEmail,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent intent = new Intent(RegistrationActivity.this, ManagementActivity.class);
                        intent.putExtra("address", userAddress);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(DEBUG_TAG, "createUserWithEmail: failure", task.getException());
                        Toast.makeText(RegistrationActivity.this, "Registration failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}