package com.example.mark.racetheworld.FireBase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;

public class FirebaseDBHelper {
    public DatabaseReference databaseReference;

    public FirebaseDBHelper() {
        initialize();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void initialize() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.databaseReference = database.getReference();
    }

    public void createUserIfNotExists(String uid) {
        DatabaseReference userRef = this.databaseReference.child("Users").child(uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    //create new user


                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = auth.getCurrentUser();
                    String uid = currentUser.getUid();
                    String photoURL = currentUser.getPhotoUrl().toString();
                    Log.e("Creating new user: ", uid);
                    User newUser = new User(currentUser.getDisplayName(), currentUser.getEmail(), photoURL);


                    // Get the reference for users
                    databaseReference.child("Users").child(uid).setValue(newUser);
                }
                else
                {
                    dataSnapshot.child("ready").getRef().setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        userRef.addListenerForSingleValueEvent(eventListener);
    }

    public void resetReadyState(String uid)
    {
        databaseReference.child("Users").child(uid).child("ready").setValue(false);
    }

    public void setReadyState(String uid)
    {
        databaseReference.child("Users").child(uid).child("ready").setValue(true);
    }

    public void updateCurrentStats(double distance, long time){
        DatabaseReference userRef = this.databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.child("currentDistance").setValue(distance);
        userRef.child("currentTime").setValue(time);
    }

    public void setImageFromUrl(ImageView img, String imageURL)
    {
        new DownloadImageTask(img)
                .execute(imageURL);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void createNewChallenge(String oppEmail, double distance){
        // First find the last ID that was created,
        final String oEmail = oppEmail;
        final double d = distance;
        Query query = databaseReference.child("Challenges");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String issuedByUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String currentName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                Challenge newChallenge = new Challenge(oEmail, d, currentName, issuedByUid, userEmail);
                databaseReference.child("Challenges").child(issuedByUid).setValue(newChallenge);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addListenerForSingleValueEvent(eventListener);


    }
}
