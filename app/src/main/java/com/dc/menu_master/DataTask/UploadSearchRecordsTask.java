package com.dc.menu_master.DataTask;

import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;

/**
 * This AsyncTask is used to upload search records to Firebase realtime database and save them as user's search history.
 */

public class UploadSearchRecordsTask extends AsyncTask<HashSet<String>, Void, Void> {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userEmail;

    @Override
    protected Void doInBackground(HashSet<String>... params) {
        // Get key.
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        HashSet<String> set = params[0];

        // Get Firebase database reference.
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user").child(userEmail.replace(".", ","));

        // Upload data.
        for (String s : set) {
            myRef.push().setValue(s);
        }
        return null;
    }
}
