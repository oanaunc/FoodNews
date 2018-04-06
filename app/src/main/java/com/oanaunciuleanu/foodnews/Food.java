package com.oanaunciuleanu.foodnews;

public class Food {

    private String webTitle;
    private String sectionName;
    private String author;
    private String webPublicationDate;
    private String webUrl;

    //Constructor
    public Food(String webTitle, String sectionName, String author, String webPublicationDate, String webUrl) {
        this.webTitle = webTitle;
        this.sectionName = sectionName;
        this.author = author;
        this.webPublicationDate = webPublicationDate;
        this.webUrl = webUrl;
    }

    // Getters
    public String getWebTitle() {
        return webTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getAuthor() {
        return author;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
