package com.example.gp2021.data.model;

public class test {
    String examID;
    String questionID;
    String A;
    String B;
    String C;
    String D;
    String correctAnswer;

    public test() {
    }

    public test(String examID, String questionID, String a, String b, String c, String d, String correctAnswer) {
        this.examID = examID;
        this.questionID = questionID;
        A = a;
        B = b;
        C = c;
        D = d;
        this.correctAnswer=correctAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
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

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }
}
