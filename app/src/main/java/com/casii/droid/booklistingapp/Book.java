package com.casii.droid.booklistingapp;

/**
 * Created by Casi on 07.07.2017.
 */

public class Book {
    private String title;
    private String description;
    private String author;

    public Book(String title, String author, String description) {
        this.title = title;
        this.author = author;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

}
