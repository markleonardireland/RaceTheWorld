package com.example.mark.racetheworld.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mark.racetheworld.R;

public class PreChallengeActivity extends AppCompatActivity {
    protected String mOppEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_challenge);
        mOppEmail = getIntent().getStringExtra("email");

        Log.e("PreChallengeActivity: ", "Opponents Email is : " + mOppEmail);

        
    }
}
