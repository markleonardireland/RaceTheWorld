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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        mOpponentUid = getIntent().getStringExtra("OppUid");
        checkForWinner();
    }

    private void checkForWinner(){
        // Get both current user and opponent user info from teh database and then check
        // who won
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("Child Added", dataSnapshot.toString());
                if (dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    mCurrentUser = dataSnapshot.getValue(User.class);
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
        userRef.addChildEventListener(eventListener);
    }

    private void displayWinner(){
        System.out.println("Displaying Winner");
        // Incrememnt the winners score
        User winner;

        if (mCurrentUser.currentDistance >= mOpponent.currentDistance){
            winner = mCurrentUser;
        }
        else{
            winner = mOpponent;
        }
        FirebaseDatabase.getInstance().getReference().child("Users").child(mOpponentUid).child("racesWon").setValue(mOpponent.racesWon + 1);


    }
}
