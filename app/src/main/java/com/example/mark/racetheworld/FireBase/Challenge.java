package com.example.mark.racetheworld.FireBase;

public class Challenge {
    public String issuedByEmail;
    public String issuedToEmail;
    public double distance;
    public String issuedByUid;

    public Challenge(){

    }

    public Challenge(String issuedByEmail, String issuedToEmail, double distance, String issuedByUid){
        this.issuedByEmail = issuedByEmail;
        this.issuedToEmail = issuedToEmail;
        this.distance = distance;
        this.issuedByUid = issuedByUid;
    }
}
