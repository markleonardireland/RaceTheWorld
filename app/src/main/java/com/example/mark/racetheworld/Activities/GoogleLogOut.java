package com.example.mark.racetheworld.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mark.racetheworld.FireBase.FirebaseDBHelper;
import com.example.mark.racetheworld.R;
import com.google.firebase.auth.FirebaseAuth;

public class GoogleLogOut extends AppCompatActivity {

    Button button;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    //Code for Permission request used to check if user has given permission for GPS
    protected static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_log_out);

        FirebaseDBHelper helper = new FirebaseDBHelper();


        button = (Button) findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        Log.e("user id: :", mAuth.getCurrentUser().getUid());

        helper.createUserIfNotExists(mAuth.getCurrentUser().getUid());
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(GoogleLogOut.this, GoogleSignIn.class));
                }
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });

        //Before the app runs we want to make sure that the app has the permission to track location.
        //For the sake of running accuracy we're going to be using FINE_LOCATION
        //Android 23 and onwards: Need to check permissions at runtime.
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);


        //TODO: Hande the event that the user denies access to GPS
        //If the permission is denied we have to request it.
        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        Intent intent = new Intent(this, RaceActivity.class);
        startActivity(intent);
    }
}
