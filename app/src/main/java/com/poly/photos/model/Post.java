package com.poly.photos.model;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String publisher;
    private String avartar;


    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public Post() {
    }

    public Post(String postid, String postimage, String description, String publisher, String avartar) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
        this.avartar = avartar;
    }

    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }




}
