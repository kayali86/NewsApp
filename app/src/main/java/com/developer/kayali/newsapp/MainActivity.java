package com.developer.kayali.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {
    // The class MainActivity implements a LoaderManager that used to retrieve data from API servers
    // Loader ID
    private static final int STORY_LOADER_ID = 1;
    // Url to get the JSON response from the server
    private static final String REQUEST_URL =
            "http://content.guardianapis.com/search";
    // Declaring an adapter for the list of Stories
    private StoryAdapter mAdapter;
    // A TextView to display when the list of stories is empty
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // EmptyTextView initializing
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        // Declaring a listView to display stories
        ListView storyListView = (ListView) findViewById(R.id.list);
        // Set an EmptyView to the listView to use when there is no stories to display
        storyListView.setEmptyView(mEmptyStateTextView);
        // An adapter that takes an empty list of stories as input
        mAdapter = new StoryAdapter(this, new ArrayList<Story>());
        storyListView.setAdapter(mAdapter);

        // Checking state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // Call the Loader to get data when there is a connection
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(STORY_LOADER_ID, null, this);
        } else {
            // Declaring an Indicator (ProgressBar) to display during retrieving data from the server
            View loadingIndicator = findViewById(R.id.loading_indicator);
            // Hide the indicator when there is no internet connection
            loadingIndicator.setVisibility(View.GONE);
            // Update emptyTextView with no connection error message
            mEmptyStateTextView.setText(getString(R.string.no_internet_connection));
        }

        // When an Item from Stories ListView clicked
        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current Story that was clicked on
                Story currentStory = mAdapter.getItem(position);
                // Intent that uses Story Url to call view the story using web browser
                Uri storyUri = Uri.parse(currentStory.getStoryUrl());
                Intent storyIntent = new Intent(Intent.ACTION_VIEW, storyUri);
                // Check if there is a browser on device to execute the intent
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(storyIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe) {
                    startActivity(storyIntent);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Create a new loader
    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String pageSize = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(REQUEST_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", pageSize);
        uriBuilder.appendQueryParameter("api-key", "5c70d9f0-f6a0-42bc-8487-99fde6b0ffbf");
        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=
        // geojson&limit=10&minmag=minMagnitude&orderby=time
        return new StoryLoader(this, uriBuilder.toString());
    }
    // When loader in done
    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {
        // When the retrieved response is null
        mEmptyStateTextView.setText(R.string.no_stories);
        // Hide the indicator
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Clear tha adapter because no new data
        mAdapter.clear();
        // Add the new retrieved stories to the adapter
        if (stories != null && !stories.isEmpty()) {
            mAdapter.addAll(stories);
        }
    }
    // When the activity is closed or When the Loader interrupted
    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
    @Override
    // Inflate the menu Layout
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    // If an option from menu selected
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Open Settings Activity when settings option selected
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}