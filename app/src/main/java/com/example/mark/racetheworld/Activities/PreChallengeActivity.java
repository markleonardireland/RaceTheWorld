package com.example.mark.racetheworld.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mark.racetheworld.FireBase.User;
import com.example.mark.racetheworld.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PreChallengeActivity extends AppCompatActivity {
    protected String mOppEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_challenge);
        mOppEmail = getIntent().getStringExtra("email");

        Log.e("PreChallengeActivity: ", "Opponents Email is : " + mOppEmail);

        // Do a query for the opponent using email
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = userRef.orderByChild("email").equalTo(mOppEmail);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                // Get the user info who has the email
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists())
                {
                    System.out.println(dataSnapshot.toString());
                    System.out.println(dataSnapshot.getValue());
                    User user = dataSnapshot.getValue(User.class);
                    System.out.println(user.name);
                    Log.e("Users email: ", user.email);
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }
}
