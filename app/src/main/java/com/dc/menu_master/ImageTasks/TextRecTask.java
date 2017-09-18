package com.dc.menu_master.ImageTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.dc.menu_master.MultiChoiceView.MulActivity;
import com.dc.menu_master.MainActivity;
import com.dc.menu_master.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This AsyncTask will call MicroSoft Azure Computer Vision API to detect and recognize texts in uploaded picture and return results.
 */

public class TextRecTask extends AsyncTask<Void, Void, Void> {
    public static final String subscriptionKey = "275a6fc019f0446294229ba71f69c73d";
    public static final String uriBase = "https://eastus2.api.cognitive.microsoft.com/vision/v1.0/ocr?en&true";
    private JSONObject json;
    private List<String> textsFromImage = new ArrayList<>();
    private WeakReference<Activity> mActivityReference;
    private ProgressDialog progressDialog;

    public TextRecTask(Activity activity){
        this.mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        // Show progress.
        progressDialog = new ProgressDialog(mActivityReference.get(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Recognizing Picture ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    protected Void doInBackground(Void... params) {

        HttpClient httpClient = new DefaultHttpClient();
        try {
            URI uri = new URI(uriBase);
            HttpPost request = new HttpPost(uri);

            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body.
            StringEntity requestEntity =
                    new StringEntity("{\"url\":\"https://storage.googleapis.com/menu-master-v1.appspot.com/images/" + MainActivity.userName + "/Menu.jpg\"}");

            request.setEntity(requestEntity);

            // Execute the REST API call and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                json = new JSONObject(jsonString);
                JSONArray regions = json.getJSONArray("regions");
                for (int i = 0; i < regions.length(); i++) {
                    JSONObject region = regions.getJSONObject(i);
                    JSONArray lines = region.getJSONArray("lines");
                    for (int j = 0; j < lines.length(); j++) {
                        JSONObject line = lines.getJSONObject(j);
                        JSONArray words = line.getJSONArray("words");
                        StringBuilder string = new StringBuilder();
                        for (int k = 0; k < words.length(); k++) {
                            JSONObject word = words.getJSONObject(k);
                            string.append(word.get("text"));
                            string.append(" ");
                        }
                        // Regular expression to filter out some unconventional dish names.
                        String temp = string.toString().trim();
                        if (!(Pattern.matches("[0-9]*", temp) || temp.contains("$") || !Pattern.matches(".*[a-zA-Z].*", temp))) {
                            textsFromImage.add(temp);
                        }
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
    protected void onPostExecute(Void args) {
        super.onPostExecute(args);
        progressDialog.dismiss();
        // Start MulActivity to let user multi select dishes they are interested in.
        Activity activity = mActivityReference.get();
        Intent mulActivity = new Intent(activity, MulActivity.class);
        mulActivity.putStringArrayListExtra("recTexts", (ArrayList<String>) textsFromImage);
        activity.startActivity(mulActivity);
    }
}
