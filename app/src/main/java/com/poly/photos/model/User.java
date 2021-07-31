package com.poly.photos.model;

public class User {
 private    String name;
  private String avartar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    public User(String name, String avartar) {

        this.name = name;
        this.avartar = avartar;
    }
}
