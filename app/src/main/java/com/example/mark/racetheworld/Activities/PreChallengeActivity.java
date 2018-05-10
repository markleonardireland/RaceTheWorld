package com.example.mark.racetheworld.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mark.racetheworld.FireBase.FirebaseDBHelper;
import com.example.mark.racetheworld.FireBase.User;
import com.example.mark.racetheworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PreChallengeActivity extends AppCompatActivity {
    protected String mOppEmail;
    protected User mOpponent;
    protected FirebaseDBHelper mHelper;
    protected String mOpponentUid;
    protected ChildEventListener mEventListener;
    protected Query mUserQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_challenge);
        mOppEmail = getIntent().getStringExtra("email");
        mHelper = new FirebaseDBHelper();

        Log.e("PreChallengeActivity: ", "Opponents Email is : " + mOppEmail);

        // Do a query for the opponent using email
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserQuery = userRef.orderByChild("email").equalTo(mOppEmail);

        mHelper.setReadyState(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                // Get the user info who has the email
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()) {
                    mOpponent = dataSnapshot.getValue(User.class);
                    Log.e("Users email: ", mOpponent.email);
                    Log.e("Users UID: ", dataSnapshot.getKey());

                    mOpponentUid = dataSnapshot.getKey();

                    // Update the UI with the opponent information

                    ImageView profileImage = (ImageView) findViewById(R.id.user_profile);
                    mHelper.setImageFromUrl(profileImage, mOpponent.photoURL);

                    TextView opponentName = (TextView) findViewById(R.id.user_name);
                    TextView opponentWins = (TextView) findViewById(R.id.user_wins);
                    TextView opponentDistance = (TextView) findViewById(R.id.user_dis);

                    opponentName.setText(mOpponent.name);
                    opponentWins.setText(String.valueOf(mOpponent.racesWon));
                    opponentDistance.setText(String.valueOf(mOpponent.totalDistance));


                    // Now that we have the information begin to check if the other use r is ready
                    if (mOpponent.ready == true) {
                        onUserReady();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                mOpponent = dataSnapshot.getValue(User.class);
                Log.e("PreChallengeActivity: ", "onChildChanged");
                if (mOpponent.ready == true) {
                    onUserReady();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };
        mUserQuery.addChildEventListener(mEventListener);

    }


    public void onUserReady()
    {
        Log.e("onUserReady", "Starting another race activity");
        Intent intent = new Intent(PreChallengeActivity.this, RaceActivity.class);
        intent.putExtra("oppuid", mOpponentUid);
        startActivity(intent);
        mUserQuery.removeEventListener(mEventListener);
        finish();
    }
}
