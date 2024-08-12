package com.example.stylish.model;

public class User {
    private Integer id;
    private String provider;
    private String name;
    private String email;
    private String password;
    private String picture;

    // Constructors
    public User() {}

    public User(Integer id, String provider, String name, String email, String password, String picture) {
        this.id = id;
        this.provider = provider;
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = picture;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
