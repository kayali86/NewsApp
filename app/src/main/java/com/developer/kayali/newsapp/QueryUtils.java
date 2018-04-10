package com.developer.kayali.newsapp;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    // Declare a Log tag that will be used with Log messages and Exceptions
    private static final String LOG_TAG = QueryUtils.class.getName();
    // String constants to store the response key values
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String WEB_TITLE = "webTitle";
    private static final String SECTION_NAME = "sectionName";
    private static final String WEB_URL = "webUrl";
    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String TAGS = "tags";

    // Constructor
    private QueryUtils() {
    }

    // Parsing the JSON response.
    private static List<Story> extractResultFromJson(String storyJSON) {
        // When the JSON string is empty or null
        if (TextUtils.isEmpty(storyJSON)) {
            return null;
        }
        // Declare an ArrayList to store the stories
        List<Story> stories = new ArrayList<>();
        // Fetching stories data from the JSON response
        try {
            JSONObject baseJsonResponse = new JSONObject(storyJSON);
            JSONObject response = baseJsonResponse.getJSONObject(RESPONSE);
            JSONArray storyArray = response.getJSONArray(RESULTS);
            // For loop to create a Story Objects to store it in stories ArrayList
            for (int i = 0; i < storyArray.length(); i++) {
                JSONObject currentStory = storyArray.getJSONObject(i);
                String storyTitle = currentStory.getString(WEB_TITLE);
                String storySection = currentStory.getString(SECTION_NAME);
                String storyUrl = currentStory.getString(WEB_URL);
                String publicationDate = currentStory.getString(WEB_PUBLICATION_DATE);
                // Get the authors from tags JSONArray if available
                JSONArray tagsArray = currentStory.getJSONArray(TAGS);
                String author = "";
                if (tagsArray.length() == 0) {
                    author = null;
                } else {
                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject tagsObject = tagsArray.getJSONObject(j);
                        String newAuthor = tagsObject.getString(WEB_TITLE);
                        builder.append(newAuthor);
                        builder.append(". ");
                        author = builder.toString();
                    }
                }
                // Declare a Story Object with the new retrieved data
                Story story = new Story(storyTitle, storySection, storyUrl, author, publicationDate);
                // Add the new Story Object to the list of stories.
                stories.add(story);
            }
            // If there is a problem parsing the Story JSON results
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the Story JSON results", e);
        }
        // Return the list of stories
        return stories;
    }

    // Fetching data of stories using the response Url as input
    public static List<Story> fetchStoryData(String requestUrl) {
        // Declare URL object
        URL url = createUrl(requestUrl);
        // Receive a JSON response using the makeHttpRequest method
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Create a list of Stories that contains the extracted data from JSON response
        return extractResultFromJson(jsonResponse);
    }

    // Casting the String url to Url Object
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    // Perform all required operations to get data from JSON response using HTTP connection and get method
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null
        if (url == null) {
            return jsonResponse;
        }
        // Declare HttpURLConnection and InputStream
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // RequestCode = 200 it means the request was successful
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Story JSON results.", e);
            // We used finally to close Url connection and inputStream after using
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // Casting the InputStream to String using InputStreamReader and BufferedReader
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
