package com.dc.menu_master.DataTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dc.menu_master.Food.Food;
import com.dc.menu_master.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This AsyncTask is used to fetch information from Firebase realtime DB about user's latest searched dish.
 * This AsyncTask will only be executed by SplashScreen Activity.
 */

public class FetchLatestSearchedDishTask extends AsyncTask<HashSet<String>, Void, Void> {
    private String uriBase = "http://food2fork.com/api/search?key=a835aa3ec93b11a34ba7473073eed150&q=";
    private ArrayList<Food> results = new ArrayList<>();
    private JSONObject json;
    private WeakReference<Activity> mActivityReference;

    public FetchLatestSearchedDishTask(Activity activity){
        this.mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected Void doInBackground(HashSet<String>... params) {
        HashSet<String> set = params[0];

        HttpClient httpClient = new DefaultHttpClient();
        try {
            // Post to food2fork API to get titles, images and ids.
            for (String s : set) {
                s = s.trim();
                s = s.replace(" ", "%20");
                String newUri =  uriBase + s;

                URI uri = new URI(newUri);

                HttpPost request = new HttpPost(uri);

                // Execute the REST API call and get the response entity.
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // Format and display the JSON response.
                    String jsonString = EntityUtils.toString(entity);
                    json = new JSONObject(jsonString);
                    JSONArray recipes = json.getJSONArray("recipes");
                    for (int i = 0; i < Math.min(recipes.length(), 15); i++) {
                        JSONObject recipe = recipes.getJSONObject(i);
                        Food temp = new Food();
                        temp.setName((String) recipe.get("title"));
                        temp.setImageUrl((String) recipe.get("image_url"));
                        temp.setRecipeId((String) recipe.get("recipe_id"));
                        results.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            // Display error message.
            Log.d("Error", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void res) {
        super.onPostExecute(res);
        // Start MainActivity from SplashScreen and kill this activity.
        Activity activity = mActivityReference.get();
        Intent mainActivity = new Intent(activity, MainActivity.class);
        mainActivity.putExtra("resultsFromFoodAPI", results);
        activity.startActivity(mainActivity);
        activity.finish();
    }
}
