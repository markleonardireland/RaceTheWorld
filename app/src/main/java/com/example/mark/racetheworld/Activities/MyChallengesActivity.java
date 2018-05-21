package com.example.mark.racetheworld.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark.racetheworld.FireBase.Challenge;
import com.example.mark.racetheworld.FireBase.FirebaseDBHelper;
import com.example.mark.racetheworld.FireBase.User;
import com.example.mark.racetheworld.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyChallengesActivity extends AppCompatActivity {
    protected FirebaseRecyclerAdapter mAdapter;
    protected RecyclerView mResultList;
    protected DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_challenges);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Challenges");

        mResultList = findViewById(R.id.challenge_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        firebaseUserSearch("");
    }

    private void firebaseUserSearch(String searchText) {

        Log.e("seaarching for ", searchText);
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Toast.makeText(MyChallengesActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("issuedToEmail").startAt(userEmail).endAt(userEmail + "\uf8ff");;

        FirebaseRecyclerOptions<Challenge> options =
                new FirebaseRecyclerOptions.Builder<Challenge>()
                        .setQuery(firebaseSearchQuery, Challenge.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<Challenge, MyChallengesActivity.ChallengeViewHolder>(options)
        {
            @Override
            public MyChallengesActivity.ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                Log.e("onCreateViewHolder: ", "Creating View Holder");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.challenge_list_layout, parent, false);

                return new MyChallengesActivity.ChallengeViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(MyChallengesActivity.ChallengeViewHolder viewHolder, int position, Challenge model) {
                Log.e("onBindViewHolder", String.valueOf(model.distance));
                viewHolder.setDetails(getApplicationContext(), model.issuedByName, model.distance, model.issuedByUid);
            }
        };

        Log.e("firebaseUserSearch: ", "Attaching adapter");
        mAdapter.startListening();
        mResultList.setAdapter(mAdapter);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.e("onDestroy", "Stopped Listening");
        super.onStop();
        mAdapter.stopListening();
    }
    // View Holder Class

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {

        View mView;
        double mDistance;
        String mName;
        String mUid;

        public ChallengeViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            Button acceptButton = (Button) mView.findViewById(R.id.accept_button);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getContext(), RaceActivity.class);
                    intent.putExtra("oppuid", mUid);
                    intent.putExtra("targetDistance", mDistance);
                    FirebaseDBHelper helper = new FirebaseDBHelper();
                    helper.setReadyState(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mView.getContext().startActivity(intent);
                    ((Activity)mView.getContext()).finish();

                }
            });
        }

        public void setDetails(Context ctx, String name, double distance, String uid){
            TextView challengerName = (TextView) mView.findViewById(R.id.challenge_name);
            TextView challengeDistance = (TextView) mView.findViewById(R.id.challenge_distance);

            mDistance = distance;
            mName = name;
            mUid = uid;

            challengerName.setText(name);
            challengeDistance.setText(String.format("%4.2f km", distance));
            FirebaseDBHelper helper = new FirebaseDBHelper();



        }
    }
}
