package com.example.mark.racetheworld.FireBase;

public class Challenge {
    public String issuedByEmail;
    public String issuedToEmail;
    public double distance;
    public String issuedByUid;

    public Challenge(){

    }

    public Challenge(String issuedToEmail, double distance){
        this.issuedToEmail = issuedToEmail;
        this.distance = distance;
    }
}
