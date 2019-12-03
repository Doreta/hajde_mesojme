package com.fiek.hajde_mesojme.Common;

// Category1 eshte klasa e cila eshte e lidhur me tabelen Category


public class Category1
{
    private String Name;
    private String Image;

    public Category1()
    {

    }

    public Category1(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
