package com.example.mark.racetheworld.FireBase;

public class Challenge {
    public String issuedToEmail;
    public String issuedByUid;
    public double distance;
    public String issuedByName;
    public String issuedByEmail;

    public Challenge(){

    }

    public Challenge(String issuedToEmail, double distance, String issuedByName, String issuedByUid, String issuedByEmail){
        this.issuedToEmail = issuedToEmail;
        this.distance = distance;
        this.issuedByName = issuedByName;
        this.issuedByUid = issuedByUid;
        this.issuedByEmail = issuedByEmail;
    }
}
