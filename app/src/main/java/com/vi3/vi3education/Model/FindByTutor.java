package com.vi3.vi3education.Model;

public class FindByTutor {
    private  String tutor_id;
    private  String tutor_name;
    private  String tutor_email;
    private  String tutor_contact;
    private  String tutor_location;
    private  String tutor_subject;

    public FindByTutor() {
    }

    public FindByTutor(String tutor_id, String tutor_name, String tutor_email, String tutor_contact, String tutor_location, String tutor_subject) {
        this.tutor_id = tutor_id;
        this.tutor_name = tutor_name;
        this.tutor_email = tutor_email;
        this.tutor_contact = tutor_contact;
        this.tutor_location = tutor_location;
        this.tutor_subject = tutor_subject;
    }

    public String getTutor_id() {
        return tutor_id;
    }

    public void setTutor_id(String tutor_id) {
        this.tutor_id = tutor_id;
    }

    public String getTutor_name() {
        return tutor_name;
    }

    public void setTutor_name(String tutor_name) {
        this.tutor_name = tutor_name;
    }

    public String getTutor_email() {
        return tutor_email;
    }

    public void setTutor_email(String tutor_email) {
        this.tutor_email = tutor_email;
    }

    public String getTutor_contact() {
        return tutor_contact;
    }

    public void setTutor_contact(String tutor_contact) {
        this.tutor_contact = tutor_contact;
    }

    public String getTutor_location() {
        return tutor_location;
    }

    public void setTutor_location(String tutor_location) {
        this.tutor_location = tutor_location;
    }

    public String getTutor_subject() {
        return tutor_subject;
    }

    public void setTutor_subject(String tutor_subject) {
        this.tutor_subject = tutor_subject;
    }
}
