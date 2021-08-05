package com.poly.photos.model;

public class User {
    private String cover;
    private String avartar;
    private String name;
    private String email;
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


    public User() {
    }


    public User(String cover, String avartar, String name, String email, String phone, String id) {
        this.cover = cover;
        this.avartar = avartar;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPhone() {
        return phone;
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
