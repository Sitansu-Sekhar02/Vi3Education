package com.vi3.vi3education.Model;

public class YourCourseModel {
    private  String course_id;
    private  String course_name;
    private  String course_video_url;
    private  String course_image;

    public YourCourseModel() {
    }

    public YourCourseModel(String course_id, String course_name, String course_video_url, String course_image) {
        this.course_id = course_id;
        this.course_name = course_name;
        this.course_video_url = course_video_url;
        this.course_image = course_image;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getCourse_video_url() {
        return course_video_url;
    }

    public void setCourse_video_url(String course_video_url) {
        this.course_video_url = course_video_url;
    }

    public String getCourse_image() {
        return course_image;
    }

    public void setCourse_image(String course_image) {
        this.course_image = course_image;
    }
}
