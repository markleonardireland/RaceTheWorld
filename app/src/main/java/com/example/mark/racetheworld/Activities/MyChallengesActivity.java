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

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        mResultList = findViewById(R.id.challenge_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        firebaseUserSearch("");
    }

    private void firebaseUserSearch(String searchText) {

        Log.e("seaarching for ", searchText);
        Toast.makeText(MyChallengesActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(firebaseSearchQuery, User.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<User, MyChallengesActivity.UsersViewHolder>(options)
        {
            @Override
            public MyChallengesActivity.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                Log.e("onCreateViewHolder: ", "Creating View Holder");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);

                return new MyChallengesActivity.UsersViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(MyChallengesActivity.UsersViewHolder viewHolder, int position, User model) {
                Log.e("onBindViewHolder", model.name);
                viewHolder.setDetails(getApplicationContext(), model.name, model.racesWon, model.email, model.photoURL);
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

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        String mEmail;
        String mName;
        long mRacesWon;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            Button btn = mView.findViewById(R.id.challenge_button);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("Button status: ", mEmail);
                    // Send to the preActivity with the email
                    Intent intent = new Intent(mView.getContext(), PreChallengeActivity.class);
                    intent.putExtra("email", mEmail);
                    mView.getContext().startActivity(intent);
                    Activity act = (Activity) mView.getContext();



                }
            });
        }

        public void setDetails(Context ctx, String userName, long racesWon, String email, String photoURL){
            TextView user_name = (TextView) mView.findViewById(R.id.name_text);
            TextView races_won = (TextView) mView.findViewById(R.id.races_won);
            ImageView user_pic = (ImageView) mView.findViewById(R.id.profile_image);


            mEmail = email;
            mRacesWon = racesWon;
            mName = userName;

            user_name.setText(mName);
            races_won.setText(String.valueOf(mRacesWon));
            FirebaseDBHelper helper = new FirebaseDBHelper();
            helper.setImageFromUrl(user_pic, photoURL.toString());


        }
    }
}
