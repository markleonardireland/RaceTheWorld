package com.example.mark.racetheworld.FireBase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDBHelper {
    private DatabaseReference databaseReference;

    public FirebaseDBHelper() {
        Initialize();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void Initialize() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.databaseReference = database.getReference();
    }
}
