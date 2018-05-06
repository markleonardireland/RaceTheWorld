package com.example.mark.racetheworld.FireBase;

import java.util.Map;

public class User {
    public String name;
    public String email;
    public int totalDistance;
    public int totalTime;
    public int racesWon;
    public int racesDone;
    public double currentDistance;
    public int currentTime;

    public User(){

    }

    public User(String name, String email){
        this.name = name;
        this.email = email;
        this.totalDistance = 0;
        this.totalTime = 0;
        this.racesWon = 0;
        this.racesDone = 0;
        this.currentDistance = 0.0;
        this.currentTime = 0;
    }
}
