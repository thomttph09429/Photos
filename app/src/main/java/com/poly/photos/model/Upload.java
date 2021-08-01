package com.poly.photos.model;

public class Upload {
    private String name;
    private String imageUrl;

    public String getImagAvartar() {
        return imagAvartar;
    }

    public void setImagAvartar(String imagAvartar) {
        this.imagAvartar = imagAvartar;
    }

    public Upload(String name, String imageUrl, String imagAvartar) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        this.name = name;
        this.imageUrl = imageUrl;
        this.imagAvartar = imagAvartar;
    }

    private  String imagAvartar;

    public Upload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Upload() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
