package com.vi3.vi3education.Model;

public class DashboardModel {

    private  String video_url;
    private  String video_id;
    private  String subject_name;
    private  String video_price;
    private  String video_rating;
    private  String video_review;

    public DashboardModel() {
    }

    public DashboardModel(String video_url, String video_id, String subject_name, String video_price, String video_rating, String video_review) {
        this.video_url = video_url;
        this.video_id = video_id;
        this.subject_name = subject_name;
        this.video_price = video_price;
        this.video_rating = video_rating;
        this.video_review = video_review;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getVideo_price() {
        return video_price;
    }

    public void setVideo_price(String video_price) {
        this.video_price = video_price;
    }

    public String getVideo_rating() {
        return video_rating;
    }

    public void setVideo_rating(String video_rating) {
        this.video_rating = video_rating;
    }

    public String getVideo_review() {
        return video_review;
    }

    public void setVideo_review(String video_review) {
        this.video_review = video_review;
    }
}
