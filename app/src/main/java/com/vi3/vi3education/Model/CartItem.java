package com.vi3.vi3education.Model;

public class CartItem {

    private  String cart_id;
    private  String video_id;
    private  String video_name;
    private  String product_image;
    private  String video_price;
    private  String user_id;



    public CartItem() {
    }

    public CartItem(String cart_id, String video_id, String video_name, String product_image, String video_price,String user_id) {
        this.cart_id = cart_id;
        this.video_id = video_id;
        this.video_name = video_name;
        this.product_image = product_image;
        this.video_price = video_price;
        this.user_id=user_id;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
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

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getVideo_price() {
        return video_price;
    }

    public void setVideo_price(String video_price) {
        this.video_price = video_price;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
