package com.example.mark.racetheworld.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.example.mark.racetheworld.R;

public class SearchUser extends AppCompatActivity {

    private EditText mSearchField;
    private Button mSearchBtn;

    private RecyclerView mResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (Button) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
    }
}
