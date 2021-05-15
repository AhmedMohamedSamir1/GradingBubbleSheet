package com.example.gp2021.data.model;

public class exam_question_student {
    String examID;
    String questionID;
    String stdAnswer;
    String stdID;

    public exam_question_student() {
    }

    public exam_question_student(String examID, String questionID, String stdAnswer, String stdID) {
        this.examID = examID;
        this.questionID = questionID;
        this.stdAnswer = stdAnswer;
        this.stdID = stdID;
    }

    public String getExamID() {
        return examID;
    }

    public void setExamID(String examID) {
        this.examID = examID;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getStdAnswer() {
        return stdAnswer;
    }

    public void setStdAnswer(String stdAnswer) {
        this.stdAnswer = stdAnswer;
    }

    public String getStdID() {
        return stdID;
    }

    public void setStdID(String stdID) {
        this.stdID = stdID;
    }
}
