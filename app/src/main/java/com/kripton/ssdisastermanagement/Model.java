package com.kripton.ssdisastermanagement;

public class Model {
    String title,desc,Image,url;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Model(String title, String desc, String image, String url) {
        this.title = title;
        this.desc = desc;
        Image = image;
        this.url = url;
    }
}
