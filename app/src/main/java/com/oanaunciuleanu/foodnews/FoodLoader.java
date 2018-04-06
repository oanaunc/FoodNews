package com.oanaunciuleanu.foodnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class FoodLoader extends AsyncTaskLoader<List<Food>> {

    private String mUrl;

    public FoodLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public List<Food> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Food> Foods = null;
        try {
            Foods = QueryUtils.fetchFoodData(mUrl);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Foods;
    }
}
