package com.example.mark.racetheworld.Activities;

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
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark.racetheworld.FireBase.User;
import com.example.mark.racetheworld.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchUser extends AppCompatActivity {

    private EditText mSearchField;
    private Button mSearchBtn;

    private RecyclerView mResultList;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");


        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (Button) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        firebaseUserSearch("");

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);

            }
        });



    }
    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //mAdapter.stopListening();
    }



    private void firebaseUserSearch(String searchText) {

        Log.e("seaarching for ", searchText);
        Toast.makeText(SearchUser.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(firebaseSearchQuery, User.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options)
        {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                Log.e("onCreateViewHolder: ", "Creating View Holder");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);

                return new UsersViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(UsersViewHolder viewHolder, int position, User model) {
                Log.e("onBindViewHolder", model.name);
                viewHolder.setDetails(getApplicationContext(), model.name, model.racesWon, model.email);
            }
        };

        Log.e("firebaseUserSearch: ", "Attaching adapter");
        mAdapter.startListening();
        mResultList.setAdapter(mAdapter);

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


                }
            });
        }

        public void setDetails(Context ctx, String userName, long racesWon, String email){

            TextView user_name = (TextView) mView.findViewById(R.id.name_text);
            TextView races_won = (TextView) mView.findViewById(R.id.races_won);

            mEmail = email;
            mRacesWon = racesWon;
            mName = userName;

            user_name.setText(mName);
            races_won.setText(String.valueOf(mRacesWon));

        }




    }

}
