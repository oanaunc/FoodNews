package com.oanaunciuleanu.foodnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Food>> {

    // My url for searching the The Guardian for food stories, querying for: food, eat and cook

    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?order-by=newest&show-tags=contributor&page-size=20&q=food%20AND%20eat%20AND%20cook&api-key=f1fe43a0-c2e7-445c-bb4a-16e0805d7d3b";


    private static final int NEWS_LOADER_ID = 1;
    private FoodAdapter foodAdapter;
    private String warningMessage;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_item);

        //find ListView in food_list_item.xml
        ListView newsListView = findViewById(R.id.articleList);

        //if there are no articles found, display a message
        mEmptyStateTextView = findViewById(R.id.noArticles);
        newsListView.setEmptyView(mEmptyStateTextView);

        // new adapter
        foodAdapter = new FoodAdapter(this, new ArrayList<Food>());
        newsListView.setAdapter(foodAdapter);


        //onItemClick listener to open web page for articles
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Food clickedNews = foodAdapter.getItem(position);
                assert clickedNews != null;
                Uri newsURI = Uri.parse(clickedNews.getWebUrl());
                Intent foodIntent = new Intent(Intent.ACTION_VIEW, newsURI);
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(foodIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    startActivity(foodIntent);
                } else {
                    String message = getString(R.string.browser_not_found);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }

            }
        });


        // Check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading);
            loadingIndicator.setVisibility(View.GONE);
            warningMessage = (String) getText(R.string.no_internet_connection);
            WarningMessage(warningMessage);
        }
    }

    //Loader
    @Override
    public Loader<List<Food>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the URL
        return new FoodLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Food>> loader, List<Food> foodList) {
        View loadingIndicator = findViewById(R.id.loading);
        loadingIndicator.setVisibility(View.GONE);
        foodAdapter.clear();

        if (foodList != null && !foodList.isEmpty()) {
            foodAdapter.addAll(foodList);

            if (foodList.isEmpty()) {
                warningMessage = (String) getText(R.string.no_news);
                WarningMessage(warningMessage);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Food>> loader) {
        foodAdapter.clear();
    }

    //Display a warning message
    private void WarningMessage(String message) {
        View loadingIndicator = findViewById(R.id.loading);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setVisibility(View.VISIBLE);
        mEmptyStateTextView.setText(message);
    }
}