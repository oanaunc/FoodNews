package com.oanaunciuleanu.foodnews;

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

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String response = "response";
    private static final String results = "results";
    private static final String sectionName = "sectionName";
    private static final String webPublicationDate = "webPublicationDate";
    private static final String webTitle = "webTitle";
    private static final String webUrl = "webUrl";
    private static final String tags = "tags";
    private static final String authorWebTitle = "webTitle";

    //Constructor
    private QueryUtils() {
    }


    public static List<Food> fetchFoodData(String requestUrl) throws InterruptedException {

        URL url = returnUrl(requestUrl);

        // Receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = httpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "HTTP request failed.", e);
        }

        // Extracting the needed fields from JSON
        List<Food> listFromJson = extractFromJason(jsonResponse);
        return listFromJson;
    }

    private static URL returnUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "URL building problem.", e);
        }
        return url;
    }


    //HTTP request
    private static String httpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        // Initialize variables
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "There was a problem retrieving JSON results and the connection was not established.", e);
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

    // A string with the JSON response
    private static String readStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Reading the data
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    //Returning the Food object list
    private static List<Food> extractFromJason(String foodJson) {
        if (TextUtils.isEmpty(foodJson)) {
            return null;
        }
        List<Food> foodList = new ArrayList<>();
        try {

            JSONObject baseJsonResponse = new JSONObject(foodJson);
            JSONObject responseJsonFood = baseJsonResponse.getJSONObject(response);
            JSONArray foodArray = responseJsonFood.getJSONArray(results);

            // Create objects based on the articles information
            for (int i = 0; i < foodArray.length(); i++) {
                JSONObject currentArticle = foodArray.getJSONObject(i);
                String articleSectionName = currentArticle.getString(sectionName);
                String newsDate = "Date Not Available";

                if (currentArticle.has(webPublicationDate)) {
                    newsDate = currentArticle.getString(webPublicationDate);
                }
                String newsTitle = currentArticle.getString(webTitle);
                String newsUrl = currentArticle.getString(webUrl);
                JSONArray currentAuthorArray = currentArticle.getJSONArray(tags);

                String articleAuthor = "Author Not Available";
                int length = currentAuthorArray.length();
                if (length == 1) {
                    JSONObject currentArticleAuthor = currentAuthorArray.getJSONObject(0);
                    String author = currentArticleAuthor.getString(authorWebTitle);
                    articleAuthor = "Author: " + author;
                }

                // A new object with the details from Json
                Food foodObject = new Food(newsTitle, articleSectionName, articleAuthor, newsDate, newsUrl);
                foodList.add(foodObject);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "There was a problem parsing the JSON results.");
        }
        return foodList;
    }
}