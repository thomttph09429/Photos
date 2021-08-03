package com.poly.photos.model;

public class User {
    private String avartar;
    private String name;
    private String email;
    private String cover;
    private String phone;
    private String id;
    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCover() {
        return cover;
    }

    public User() {
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPhone() {
        return phone;
    }

    public User(String avartar, String name, String email, String cover, String phone, String id) {
        this.avartar = avartar;
        this.name = name;
        this.email = email;
        this.cover = cover;
        this.phone = phone;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }





}
