package com.developer.kayali.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class StoryLoader extends AsyncTaskLoader<List<Story>> {
    // Url to get the JSON response from the server
    private String mUrl;

    // Constructor - url as second input
    public StoryLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // The method QueryUtils.fetchStoryData Perform the network request, parse the response,
        // and retrieve the stories from JSON response
        return QueryUtils.fetchStoryData(mUrl);
    }
}
