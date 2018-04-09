package com.developer.kayali.newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StoryAdapter extends ArrayAdapter<Story> {
    // Declare a Log tag that will be used with Log messages and Exceptions
    private static final String LOG_TAG = StoryAdapter.class.getName();

    // Constructor
    public StoryAdapter(Activity context, ArrayList<Story> stories) {
        super(context, 0, stories);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        // Find the current story
        Story currentStory = getItem(position);
        // Initialize a TextView to display Story title
        TextView storyTitleView = (TextView) listItemView.findViewById(R.id.story_title);
        storyTitleView.setText(currentStory.getStoryTitle());
        // Initialize a TextView to display Story section
        TextView storySectionView = (TextView) listItemView.findViewById(R.id.section);
        storySectionView.setText(currentStory.getStorySection());
        // Initialize a TextView to display Story Author
        TextView storyAuthorView = (TextView) listItemView.findViewById(R.id.author);
        if (currentStory.getAuthor() == null){
            storyAuthorView.setVisibility(View.GONE);
        }
        storyAuthorView.setText(currentStory.getAuthor());
        // Initialize a TextView to display Story publication date with the correct format using formattedDate method
        TextView dateView = listItemView.findViewById(R.id.date);
        String formattedDate = formatDate(currentStory.getPublicationDate());
        dateView.setText(formattedDate);
        // Initialize a TextView to display Story publication time with the correct format using formattedTime method
        TextView timeView = listItemView.findViewById(R.id.time);
        String formattedTime = formatTime(currentStory.getPublicationDate());
        timeView.setText(formattedTime);
        // Initialize an ImageView to display icons for each Story according to section(using getStorySection method)
        ImageView sectionImage = listItemView.findViewById(R.id.section_image);
        sectionImage.setImageResource(getSectionImageResourceID(currentStory.getStorySection()));
        return listItemView;
    }

    // Return the formatted date string
    private String formatDate(String dateFromJson) {
        SimpleDateFormat jsonFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(dateFromJson);
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat("MMM d, yyy", Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing JSON date: ", e);
            return "";
        }
    }

    // Return the formatted time string
    private String formatTime(String dateFromJson) {
        SimpleDateFormat jsonFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(dateFromJson);
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat("h:mm a", Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing JSON date: ", e);
            return "";
        }
    }

    // Return the right image resource ID according to Story section
    private int getSectionImageResourceID(String section) {
        int sectionImageResourceID;
        switch (section) {
            case "Sport":
                sectionImageResourceID = R.drawable.sport;
                break;
            case "Football":
                sectionImageResourceID = R.drawable.football;
                break;
            case "US news":
                sectionImageResourceID = R.drawable.us_news;
                break;
            case "Business":
                sectionImageResourceID = R.drawable.business;
                break;
            case "World news":
                sectionImageResourceID = R.drawable.world;
                break;
            case "News":
                sectionImageResourceID = R.drawable.news;
                break;
            case "Technology":
                sectionImageResourceID = R.drawable.technology;
                break;
            case "Australia news":
                sectionImageResourceID = R.drawable.australia;
                break;
            case "Film":
                sectionImageResourceID = R.drawable.film;
                break;
            case "UK news":
                sectionImageResourceID = R.drawable.uk;
                break;
            case "Politics":
                sectionImageResourceID = R.drawable.politics;
                break;
            case "Opinion":
                sectionImageResourceID = R.drawable.opinion;
                break;
            case "Education":
                sectionImageResourceID = R.drawable.education;
                break;
            case "Society":
                sectionImageResourceID = R.drawable.society;
                break;
            case "Environment":
                sectionImageResourceID = R.drawable.environment;
                break;
            case "Music":
                sectionImageResourceID = R.drawable.music;
                break;
            case "Science":
                sectionImageResourceID = R.drawable.science;
                break;
            default:
                sectionImageResourceID = R.drawable.blank;
        }
        return sectionImageResourceID;
    }
}
