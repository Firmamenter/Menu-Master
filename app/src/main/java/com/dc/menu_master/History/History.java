package com.dc.menu_master.History;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dc.menu_master.DataTask.FetchDishInfoFromAPITask;
import com.dc.menu_master.DataTask.UploadSearchRecordsTask;
import com.dc.menu_master.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static android.content.ContentValues.TAG;

/**
 * This Activity will show a list of dish names according to user's search history.
 */

public class History extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> history;
    private HistoryAdapter mHistoryAdapter;
    private String userEmail;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        listView = (ListView) findViewById(R.id.history_list);
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user").child(userEmail.replace(".", ","));
        history = new ArrayList<>();

        // Fetch user's history.
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d("Food Item", (String) postSnapshot.getValue());
                    history.add((String) postSnapshot.getValue());
                }
                Collections.reverse(history);
                // Set up HistoryAdapter.
                AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        // Get ViewHolder instance.
                        HistoryAdapter.ViewHolder viewHolder = (HistoryAdapter.ViewHolder) view.getTag();
                        // When you click one of the history dishes, app will search that dish and show info in MainActivity.
                        // Also, app will recognizes this as a search behavior and record it in user's search history.
                        HashSet<String> set = new HashSet<>();
                        set.add(history.get(position));
                        UploadSearchRecordsTask uploadSearchRecordsTask = new UploadSearchRecordsTask();
                        uploadSearchRecordsTask.execute(set);
                        FetchDishInfoFromAPITask fetchDishInfoFromAPITask = new FetchDishInfoFromAPITask(History.this);
                        fetchDishInfoFromAPITask.execute(set);
                    }
                };
                mHistoryAdapter = new HistoryAdapter(History.this, history);
                listView.setAdapter(mHistoryAdapter);
                listView.setOnItemClickListener(listItemClickListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        // Set up toolbar.
        Toolbar helpToolbar = (Toolbar) findViewById(R.id.history_toolbar);
        setSupportActionBar(helpToolbar);

        // Get a support ActionBar corresponding to this toolbar.
        ActionBar ab = getSupportActionBar();

        // Enable the Up button.
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Search History");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the toorbar's NavigationIcon as up/home button
            case android.R.id.home:
                //NavigationIcon
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
