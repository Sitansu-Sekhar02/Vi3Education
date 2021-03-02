package com.vi3.vi3education.Model;

public class GetAllCourseModel {
    private  String video_id;
    private  String video_name;
    private  String video_image;
    private  String video_price;
    private  String video_rating;
    private  String video_review;

    public GetAllCourseModel() {
    }

    public GetAllCourseModel(String video_id, String video_name, String video_image, String video_price, String video_rating, String video_review) {
        this.video_id = video_id;
        this.video_name = video_name;
        this.video_image = video_image;
        this.video_price = video_price;
        this.video_rating = video_rating;
        this.video_review = video_review;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_image() {
        return video_image;
    }

    public void setVideo_image(String video_image) {
        this.video_image = video_image;
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
