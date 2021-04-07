package com.example.gp2021.data.model;

public class exam {
    String examID;
    String examName;
    String examDate;
    String examGrade;
    String userID;

    public exam()
    {

    }

    public exam(String examID, String examName, String examDate, String examGrade, String userID) {
        this.examID = examID;
        this.examName = examName;
        this.examDate = examDate;
        this.examGrade = examGrade;
        this.userID = userID;

    }

    public String getExamID() {
        return examID;
    }

    public void setExamID(String examID) {
        this.examID = examID;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getExamGrade() {
        return examGrade;
    }

    public void setExamGrade(String examGrade) {
        this.examGrade = examGrade;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


}
