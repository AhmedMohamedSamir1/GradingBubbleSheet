package com.example.gp2021.data.model;

public class student {
    String stdID;
    String stdName;
    String gender;
    String email;

    public student()
    {

    }

    public student(String stdID, String stdName, String gender, String email) {
        this.stdID = stdID;
        this.stdName = stdName;
        this.gender = gender;
        this.email = email;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
