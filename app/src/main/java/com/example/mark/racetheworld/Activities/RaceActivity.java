package com.example.mark.racetheworld.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark.racetheworld.FireBase.FirebaseDBHelper;
import com.example.mark.racetheworld.FireBase.User;
import com.example.mark.racetheworld.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import static com.example.mark.racetheworld.Tracking.Calculations.distFrom;


public class RaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static String TAG = "RunActivity";

    //Variables for controlling how fast the location update intervals are
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected double mTargetDistance;
    protected String mOpponentUid;

    protected Button mStopButton;

    protected TextView mUserDistance;
    protected TextView mUserTime;
    protected TextView mUserPace;
    protected TextView mUserPosition;
    protected ImageView mUserImage;

    protected TextView mOppDistance;
    protected TextView mOppTime;
    protected TextView mOppPace;
    protected TextView mOppPosition;
    protected ImageView mOppImage;


    protected FirebaseDBHelper mHelper;

    User mOpponent;


    protected TextView mOppDistanceText;
    protected TextView mOppTimeText;
    protected Boolean mRequestingLocationUpdates;


    //Strings
    protected String mLastUpdateTime;

    //Location variables
    protected Location mCurrentLocation;
    protected Location mLastLocation;
    protected ArrayList<Location> coordinates = new ArrayList<Location>();


    //The distance the user has run.
    protected double runDistance;

    //The time the run was started
    private long timeStart;

    private long mTotalTime;

    //The tracked pace of the run
    private double trackedPace; //Distance / time

    //GoogleApiClient object to be used
    protected GoogleApiClient mGoogleApiClient;

    //LocationRequest used by FusedAPI in order to get coordinate updates at intervals
    protected LocationRequest mLocationRequest = new LocationRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);


        mHelper = new FirebaseDBHelper();
        mOpponentUid = getIntent().getStringExtra("oppuid");
        getIntent().getDoubleExtra("targetDistance", mTargetDistance);
        runDistance = 0;
        //Set the time the run was started
        timeStart = System.currentTimeMillis()/1000;
        Log.e("Run Start time: ", Long.toString(timeStart));

        //Get the textview objects to enter run details.
        mUserDistance = (TextView) findViewById(R.id.user_distance);
        mUserTime = (TextView) findViewById(R.id.user_time);
        mUserPace = (TextView) findViewById(R.id.user_pace);
        mUserPosition = (TextView) findViewById(R.id.user_position);
        mUserImage = (ImageView) findViewById(R.id.user_pic);

        mOppDistance = (TextView) findViewById(R.id.opp_user_distance);
        mOppTime = (TextView) findViewById(R.id.opp_time);
        mOppPace = (TextView) findViewById(R.id.opp_pace);
        mOppPosition = (TextView) findViewById(R.id.opp_position);
        mOppImage = (ImageView) findViewById(R.id.opp_pic);

        mStopButton = (Button) findViewById(R.id.stop_button);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                // We treat pressing this button as a forfeit
                mHelper.updateCurrentStats(0, 0);
                finishRace();
            }
        });


        // Begin to monitor the opponents details
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mOpponentUid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    mOpponent = dataSnapshot.getValue(User.class);
                    updateOppUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //true to automatically start recording data, false to wait for a callback (button press for example)
        mRequestingLocationUpdates = true;



        // Create an instance of GoogleAPIClient.
        buildGoogleApiClient();
    }

    //Builds the Google API client instance
    private void buildGoogleApiClient(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    //When a location update occurs we need to update the textview (UI) fields with the information
    private void updateUserUI(){
        Log.e(TAG, "UpdateUI called");
        if (mUserImage.getDrawable() == null)
        {
             String url = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
             mHelper.setImageFromUrl(mUserImage, url);
        }
        double distanceKm = runDistance / 1000;
        mUserDistance.setText(String.format("%4.2f", distanceKm));
        mUserTime.setText(String.format("%d", mTotalTime));
        int pace = (int)(mTotalTime / distanceKm);
        int paceMinutes = pace / 60;
        int paceSeconds = pace % 60;
        mUserPace.setText(String.format("%d:%d/%s", paceMinutes, paceSeconds, "km"));

        updatePosition();
    }

    private void updateOppUI(){
        if (mOppImage.getDrawable() == null)
        {
            mHelper.setImageFromUrl(mOppImage, mOpponent.photoURL);
        }


        Log.e("UpdateOppUI: ", "Currently updating UI for Opponent");
        double distanceKm = mOpponent.currentDistance / 1000;

        mOppTime.setText(String.valueOf(mOpponent.currentTime));
        mOppDistance.setText(String.format("%4.2f", distanceKm));


        int pace = (int)(mOpponent.currentTime / distanceKm);
        int paceMinutes = pace / 60;
        int paceSeconds = pace % 60;

        mOppPace.setText(String.format("%d:%d/%s", paceMinutes, paceSeconds, "km"));

        updatePosition();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Generally if we want to do something in the future once the API has connected we could do it here.
        //Right now we wait until the user presses the button before we actually get the lcoation so
        //it's not neccessary to do anything here.

        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
//        if (mCurrentLocation == null) {
//            Log.e(TAG, "onConnected");
//            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            updateUI();
//        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    //This method is used to create the location request object which we will use to get updates at regular intervals
    //(Used for tracking during running)
    protected void createLocationRequest() {
        Log.e(TAG, "Creating Location Request...");
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onlocationchanged");
        mLastLocation = mCurrentLocation;
        mCurrentLocation = location;

        //add the location to the list
        coordinates.add(location);

        //TODO: (Improve Accuracy) In order to get the most accuracy, have to put some measure in that checks if the points are too far apart.
        if (mLastLocation != null){
            //Get the distance between the two points
            double d = distFrom(mLastLocation, mCurrentLocation);
            Log.e(TAG, "Distance = " + d);
            Log.e(TAG, "Location Accuracy: " + location.getAccuracy());
            runDistance = runDistance + d;
            Log.e(TAG, "Run Distance = " + runDistance);

            //Calculate the current pace which is equal to the distance over the time
            mTotalTime = (System.currentTimeMillis() / 1000) - timeStart;

            Log.e("Status: " , "Trying to update stats");
            FirebaseDBHelper dbHelper = new FirebaseDBHelper();
            dbHelper.updateCurrentStats(runDistance, mTotalTime);

        }

        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


        updateUserUI();
    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        Log.e(TAG, "Starting Location Updates...");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }




    //This method is called when the run is finished. Run should be saved to SQLite entry
    //and information passed to next intent
    public void stopRunButtonCallback(View view){
        //Stop the updates
        stopLocationUpdates();

//        //TODO: Pass information to SQLite server
//
//        //Pass information to the summary screen (could also get the summary screen to read last
//        //entry to the database
//        Intent intent = new Intent(this, RunSummaryActivity.class);
//        intent.putExtra("RUN_DISTANCE", runDistance);
//        intent.putExtra("COORD_LIST", coordinates);
//        intent.putExtra("START_TIME", timeStart);
//        intent.putExtra("END_TIME", System.currentTimeMillis()/1000);
//        startActivity(intent);
//        finish();
    }

    private void checkForWinner() {
        //

    }

    private void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void updatePosition()
    {
        if (mOpponent.currentDistance >= runDistance)
        {
            mOppPosition.setText("1st");
            mUserPosition.setText("2nd");
        }
        else{
            mOppPosition.setText("2nd");
            mUserPosition.setText("1st");
        }
    }


    private void finishRace() {
        // Start the new Intent
        mHelper.resetReadyState(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Intent intent = new Intent(RaceActivity.this, ResultsActivity.class);
        intent.putExtra("OppUid", mOpponentUid);
        startActivity(intent);
    }

    private void checkForFinish()
    {
        if (runDistance >= mTargetDistance)
        {
            finishRace();
        }
    }
}