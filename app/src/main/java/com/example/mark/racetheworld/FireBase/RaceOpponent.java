package com.example.mark.racetheworld.FireBase;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mark.racetheworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RaceOpponent {
    public static final RaceOpponent instance = new RaceOpponent();
    protected ValueEventListener mEventListenet;
    protected User mOpponent;

    private RaceOpponent(){
        // Setup EventListener
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    // Set the user details
                    mOpponent = dataSnapshot.getValue(User.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
    }

    public static RaceOpponent getInstance(){
        return instance;
    }

    public void setOpponent(String uid)
    {

    }
}
