package com.example.gp2021.data.model;

public class student {
    String stdID;
    String stdName;
    String email;
    String travelTime;
    String studyTime;
    String failures;
    String activities;
    String freeTime;
    String goOut;
    String health;
    String gender;
    String absence;


    public student(){}

    public student(String stdID, String stdName, String email, String gender, String travelTime, String studyTime,
                   String failures, String activities, String freeTime, String goOut, String health, String absence) {
        this.stdID = stdID;
        this.stdName = stdName;
        this.email = email;
        this.gender = gender;
        this.travelTime = travelTime;
        this.studyTime = studyTime;
        this.failures = failures;
        this.activities = activities;
        this.freeTime = freeTime;
        this.goOut = goOut;
        this.health = health;
        this.absence = absence;
    }

    public String getStdID() {
        return stdID;
    }

    public void setStdID(String stdID) {
        this.stdID = stdID;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public String getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(String studyTime) {
        this.studyTime = studyTime;
    }

    public String getFailures() {
        return failures;
    }

    public void setFailures(String failures) {
        this.failures = failures;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getFreeTime() {
        return freeTime;
    }

    public void setFreeTime(String freeTime) {
        this.freeTime = freeTime;
    }

    public String getGoOut() {
        return goOut;
    }

    public void setGoOut(String goOut) {
        this.goOut = goOut;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getAbsence() {
        return absence;
    }

    public void setAbsence(String absence) {
        this.absence = absence;
    }
}
