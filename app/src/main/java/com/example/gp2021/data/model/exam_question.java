package com.example.gp2021.data.model;

public class exam_question {

    String catID;
    String examID;
    String questionAnswer;
    String questionGrade;
    String questionID;

    public exam_question(){}

    public exam_question(String catID, String examID, String questionAnswer, String questionGrade, String questionID) {
        this.catID = catID;
        this.examID = examID;
        this.questionAnswer = questionAnswer;
        this.questionGrade = questionGrade;
        this.questionID = questionID;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getExamID() {
        return examID;
    }

    public void setExamID(String examID) {
        this.examID = examID;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public String getQuestionGrade() {
        return questionGrade;
    }

    public void setQuestionGrade(String questionGrade) {
        this.questionGrade = questionGrade;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }
}
