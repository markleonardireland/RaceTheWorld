package com.example.mark.racetheworld.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mark.racetheworld.FireBase.FirebaseDBHelper;
import com.example.mark.racetheworld.FireBase.User;
import com.example.mark.racetheworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    protected View mainView;
    protected FirebaseDBHelper mHelper;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_profile, container, false);
        mHelper = new FirebaseDBHelper();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        Log.e("onCreateView", "Creating View");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());
                Log.e("OnDataChange", "Creating View");
                if(dataSnapshot.exists()) {
                    //create new user
                    Log.e("OnDataChange", "Creating View");

                    User currUser = dataSnapshot.getValue(User.class);
                    TextView name = (TextView) mainView.findViewById(R.id.user_name);
                    TextView distance = (TextView) mainView.findViewById(R.id.user_dis);
                    TextView wins = (TextView) mainView.findViewById(R.id.user_wins);

                    name.setText(currUser.name);
                    distance.setText(String.valueOf(currUser.totalDistance));
                    wins.setText(String.valueOf(currUser.racesWon));

                    ImageView profileImage = (ImageView) mainView.findViewById(R.id.user_profile);
                    mHelper.setImageFromUrl(profileImage, currUser.photoURL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        userRef.addListenerForSingleValueEvent(eventListener);



        return mainView;
    }
}
