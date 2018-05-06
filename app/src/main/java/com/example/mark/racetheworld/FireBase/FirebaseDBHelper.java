package com.example.mark.racetheworld.FireBase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDBHelper {
    public DatabaseReference databaseReference;

    public FirebaseDBHelper() {
        initialize();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void initialize() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.databaseReference = database.getReference();
    }

    public void createUserIfNotExists(String uid) {
        DatabaseReference userRef = this.databaseReference.child("Users").child(uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    //create new user


                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = auth.getCurrentUser();
                    String uid = currentUser.getUid();
                    String photoURL = currentUser.getPhotoUrl().toString();
                    Log.e("Creating new user: ", uid);
                    User newUser = new User(currentUser.getDisplayName(), currentUser.getEmail(), photoURL);


                    // Get the reference for users
                    databaseReference.child("Users").child(uid).setValue(newUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        userRef.addListenerForSingleValueEvent(eventListener);
    }

    public void updateCurrentStats(double distance, long time){
        DatabaseReference userRef = this.databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.child("currentDistance").setValue(distance);
        userRef.child("currentTime").setValue(time);
    }
}
