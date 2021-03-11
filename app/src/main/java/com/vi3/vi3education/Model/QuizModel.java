package com.vi3.vi3education.Model;

public class QuizModel {
    private  String quiz_id;
    private  String quiz_no;
    private  String questions;
    private  String option_1;
    private  String option_2;
    private  String option_3;
    private  String option_4;
    private  String answer;

    public QuizModel() {
    }

    public QuizModel(String quiz_id, String quiz_no, String questions, String option_1, String option_2, String option_3, String option_4, String answer) {
        this.quiz_id = quiz_id;
        this.quiz_no = quiz_no;
        this.questions = questions;
        this.option_1 = option_1;
        this.option_2 = option_2;
        this.option_3 = option_3;
        this.option_4 = option_4;
        this.answer = answer;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getQuiz_no() {
        return quiz_no;
    }

    public void setQuiz_no(String quiz_no) {
        this.quiz_no = quiz_no;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getOption_1() {
        return option_1;
    }

    public void setOption_1(String option_1) {
        this.option_1 = option_1;
    }

    public String getOption_2() {
        return option_2;
    }

    public void setOption_2(String option_2) {
        this.option_2 = option_2;
    }

    public String getOption_3() {
        return option_3;
    }

    public void setOption_3(String option_3) {
        this.option_3 = option_3;
    }

    public String getOption_4() {
        return option_4;
    }

    public void setOption_4(String option_4) {
        this.option_4 = option_4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
