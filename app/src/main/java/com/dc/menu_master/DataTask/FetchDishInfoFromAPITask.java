package com.dc.menu_master.DataTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dc.menu_master.Food.Food;
import com.dc.menu_master.MainActivity;
import com.dc.menu_master.R;

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
 * This AsyncTask is used to fetch dishes' info by calling food2fork API and edamam API.
 */

public class FetchDishInfoFromAPITask extends AsyncTask<HashSet<String>, Void, Void> {
    private ArrayList<String> uriBase = new ArrayList<>();
    private ArrayList<Food> results = new ArrayList<>();
    private JSONObject json;
    private WeakReference<Activity> mActivityReference;
    private ProgressDialog progressDialog;

    public FetchDishInfoFromAPITask(Activity activity){
        this.mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        // Show progress.
        progressDialog = new ProgressDialog(mActivityReference.get(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Fetching Dishes ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(HashSet<String>... params) {
        // Set API urls.
        HashSet<String> set = params[0];
        uriBase.add("http://food2fork.com/api/search?key=a835aa3ec93b11a34ba7473073eed150&q=");
        uriBase.add("http://food2fork.com/api/get?key=a835aa3ec93b11a34ba7473073eed150&rId=");
        uriBase.add("https://api.edamam.com/search?q=");
        uriBase.add("&app_id=3d14d921&app_key=12f5efb3a84e6fe4de99d49fbfbb4e3f&to=5");
        HttpClient httpClient = new DefaultHttpClient();
        try {
            // Post to food2fork API to get titles, images and ids.
            for (String s : set) {
                s = s.trim();
                s = s.replace(" ", "%20");

                String newUri =  uriBase.get(0) + s;
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
                    for (int i = 0; i < Math.min(recipes.length(), 5); i++) {
                        JSONObject recipe = recipes.getJSONObject(i);
                        Food temp = new Food();
                        temp.setName((String) recipe.get("title"));
                        temp.setImageUrl((String) recipe.get("image_url"));
                        temp.setRecipeId((String) recipe.get("recipe_id"));
                        results.add(temp);
                    }
                }
            }

            // Post to edamam API to get titles, images and ingredients.
            for (String s : set) {
                s = s.trim();
                s = s.replace(" ", "%20");

                String newUri =  uriBase.get(2) + s + uriBase.get(3);
                URI uri = new URI(newUri);

                HttpPost request = new HttpPost(uri);

                // Execute the REST API call and get the response entity.
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // Format and display the JSON response.
                    String jsonString = EntityUtils.toString(entity);
                    json = new JSONObject(jsonString);
                    JSONArray hits = json.getJSONArray("hits");
                    for (int i = 0; i < Math.min(hits.length(), 5); i++) {
                        JSONObject jsonObject = hits.getJSONObject(i);
                        JSONObject recipe = jsonObject.getJSONObject("recipe");
                        Food temp = new Food();
                        temp.setName((String) recipe.get("label"));
                        temp.setImageUrl((String) recipe.get("image"));
                        JSONArray ingredients = recipe.getJSONArray("ingredientLines");
                        for (int j = 0; j < ingredients.length(); j++) {
                            temp.appendRecipes(ingredients.getString(j));
                        }
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
        progressDialog.dismiss();
        if (results.isEmpty()) {
            // Toast message if can not find any dishes.
            Toast.makeText(mActivityReference.get(), "Sorry, no dishes found!",
                    Toast.LENGTH_LONG).show();
        } else {
            // Kill the old MainActivity.
            MainActivity.getInstance().finish();

            // Start new MainActivity.
            Activity activity = mActivityReference.get();
            Intent mainActivity = new Intent(activity, MainActivity.class);
            mainActivity.putExtra("resultsFromFoodAPI", results);
            activity.startActivity(mainActivity);
            activity.finish();
        }
    }
}
