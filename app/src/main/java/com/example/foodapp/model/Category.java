package com.example.foodapp.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class Category implements Serializable {
    @PropertyName("Id")
    private int id;
    @PropertyName("Name")
    private String name;
    @PropertyName("ImagePath")
    private String imgCategory;

    public Category() {
    }

    public Category(int id, String name, String imgCategory) {
        this.id = id;
        this.name = name;
        this.imgCategory = imgCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgCategory() {
        return imgCategory;
    }

    public void setImgCategory(String imgCategory) {
        this.imgCategory = imgCategory;
    }
}
