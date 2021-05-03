package com.example.gp2021.data.model;

public class subject_student {
   String absence;
   String quiz1;
   String  quiz2;
   String stdID;

    public subject_student(){}
    public subject_student(String absence, String quiz1, String quiz2, String stdID) {
        this.absence = absence;
        this.quiz1 = quiz1;
        this.quiz2 = quiz2;
        this.stdID = stdID;
    }

    public String getAbsence() {
        return absence;
    }

    public void setAbsence(String absence) {
        this.absence = absence;
    }

    public String getQuiz1() {
        return quiz1;
    }

    public void setQuiz1(String quiz1) {
        this.quiz1 = quiz1;
    }

    public String getQuiz2() {
        return quiz2;
    }

    public void setQuiz2(String quiz2) {
        this.quiz2 = quiz2;
    }

    public String getStdID() {
        return stdID;
    }

    public void setStdID(String stdID) {
        this.stdID = stdID;
    }
}
