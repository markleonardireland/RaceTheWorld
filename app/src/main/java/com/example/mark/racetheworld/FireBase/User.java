package com.example.mark.racetheworld.FireBase;

import java.util.Map;

public class User {
    public String name;
    public String email;
    public String photoURL;
    public long totalDistance;
    public long totalTime;
    public long racesWon;
    public long racesDone;
    public double currentDistance;
    public long currentTime;
    public Boolean ready;

    public User(){

    }

    public User(String name, String email, String photoURL){
        this.name = name;
        this.email = email;
        this.ready = false;
        this.totalDistance = 0;
        this.totalTime = 0;
        this.racesWon = 0;
        this.racesDone = 0;
        this.currentDistance = 0.0;
        this.currentTime = 0;
        this.photoURL = photoURL;
    }
}
