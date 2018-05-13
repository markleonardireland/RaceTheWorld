package com.example.mark.racetheworld.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mark.racetheworld.FireBase.User;
import com.example.mark.racetheworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultsActivity extends AppCompatActivity {

    protected String mOpponentUid;
    protected User mCurrentUser;
    protected User mOpponent;
    protected double mRunDistance;
    protected ChildEventListener mEventListener;
    protected DatabaseReference mReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        mOpponentUid = getIntent().getStringExtra("OppUid");
        mRunDistance = getIntent().getDoubleExtra("userDistance", 0.00);
        checkForWinner();
    }

    private void checkForWinner(){
        // Get both current user and opponent user info from teh database and then check
        // who won
        mReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("Child Added", dataSnapshot.toString());
                if (dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    mCurrentUser = dataSnapshot.getValue(User.class);
                    mReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("totalDistance").setValue(mCurrentUser.totalDistance + mRunDistance);
                }

                if (dataSnapshot.getKey().equals(mOpponentUid)){
                    mOpponent = dataSnapshot.getValue(User.class);
                }

                if (mCurrentUser != null && mOpponent != null){
                    displayWinner();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mEventListener);
    }

    private void displayWinner(){
        System.out.println("Displaying Winner");
        mReference.removeEventListener(mEventListener);
        // Incrememnt the winners score
        User winner;

        if (mCurrentUser.currentDistance >= mOpponent.currentDistance){
            winner = mCurrentUser;
        }
        FirebaseDatabase.getInstance().getReference().child("Users").child(mOpponentUid).child("racesWon").setValue(mOpponent.racesWon + 1);


    }
}
