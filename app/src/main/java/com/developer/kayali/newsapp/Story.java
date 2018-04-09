package com.developer.kayali.newsapp;

public class Story {
    // Declare the statements for Story Object
    private String storyTitle, storySection, storyUrl, publicationDate;

    // Constructor
    public Story(String storyTitle, String storySection, String storyUrl, String publicationDate) {
        this.storyTitle = storyTitle;
        this.storySection = storySection;
        this.storyUrl = storyUrl;
        this.publicationDate = publicationDate;
    }

    // Getters
    public String getStoryTitle() {
        return storyTitle;
    }

    public String getStorySection() {
        return storySection;
    }

    public String getStoryUrl() {
        return storyUrl;
    }

    public String getPublicationDate() {
        return publicationDate;
    }
}
