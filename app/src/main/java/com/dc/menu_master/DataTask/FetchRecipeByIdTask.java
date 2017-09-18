package com.dc.menu_master.DataTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.dc.menu_master.Food.Food;
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

/**
 * This AsyncTask is used to fetch dish's recipe from food2fork API by dish's Id.
 */

public class FetchRecipeByIdTask extends AsyncTask<Food, Void, Food> {
    private String uriBase = "http://food2fork.com/api/get?key=a835aa3ec93b11a34ba7473073eed150&rId=";
    private JSONObject json;
    private WeakReference<Activity> mActivityReference;
    private ProgressDialog progressDialog;

    public FetchRecipeByIdTask(Activity activity){
        this.mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        // Show progress.
        progressDialog = new ProgressDialog(mActivityReference.get(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Food doInBackground(Food... params) {
        Food food = params[0];
        HttpClient httpClient = new DefaultHttpClient();
        try {
            // Post to food2fork API to get ingredients.
            String newUri =  uriBase + food.getRecipeId();
            URI uri = new URI(newUri);

            HttpPost request = new HttpPost(uri);

            // Execute the REST API call and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                json = new JSONObject(jsonString);
                JSONObject jsonObject = json.getJSONObject("recipe");
                JSONArray ingredients = jsonObject.getJSONArray("ingredients");
                for (int i = 0; i < ingredients.length(); i++) {
                    food.appendRecipes(ingredients.getString(i));
                }
            }
        } catch (Exception e) {
            // Display error message.
            Log.d("Error", e.getMessage());
        }
        return food;
    }

    @Override
    protected void onPostExecute(Food food) {
        super.onPostExecute(food);
        progressDialog.dismiss();
    }
}
