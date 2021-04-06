package com.example.gp2021.data.model;

public class user {
    String userID;
    String userName;
    String userEmail;
    String state;

    public user()
    {

    }

    public user(String userID, String userName, String userEmail, String state) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.state = state;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
