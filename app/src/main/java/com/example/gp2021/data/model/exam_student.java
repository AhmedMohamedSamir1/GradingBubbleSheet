package com.example.gp2021.data.model;

public class exam_student {
    String examID;
    String stdID;
    String grade;

    public exam_student(){}

    public exam_student(String examID, String stdID, String grade) {
        this.examID = examID;
        this.stdID = stdID;
        this.grade = grade;
    }

    public String getExamID() {
        return examID;
    }

    public void setExamID(String examID) {
        this.examID = examID;
    }

    public String getStdID() {
        return stdID;
    }

    public void setStdID(String stdID) {
        this.stdID = stdID;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
