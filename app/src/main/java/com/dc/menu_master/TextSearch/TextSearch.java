package com.dc.menu_master.TextSearch;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dc.menu_master.DataTask.FetchDishInfoFromAPITask;
import com.dc.menu_master.DataTask.UploadSearchRecordsTask;
import com.dc.menu_master.R;

import java.util.HashSet;

/**
 * This Activity provides user with an alternate way of dish searching by typing in text.
 */

public class TextSearch extends AppCompatActivity {
    private EditText mEditText;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_search);

        mEditText = (EditText) findViewById(R.id.edittext);
        mButton = (Button) findViewById(R.id.searchDish);

        // Set up toolbar.
        Toolbar textToolbar = (Toolbar) findViewById(R.id.text_search_toolbar);
        setSupportActionBar(textToolbar);

        // Get a support ActionBar corresponding to this toolbar.
        ActionBar ab = getSupportActionBar();

        // Enable the Up button.
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Text Search");
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

    public void searchDish(View v) {
        String s = mEditText.getText().toString();
        if (s.length() > 0) {
            // If user typed in some texts.
            HashSet<String> dishes = new HashSet<>();
            dishes.add(s);
            // Upload search record.
            UploadSearchRecordsTask uploadSearchRecordsTask = new UploadSearchRecordsTask();
            uploadSearchRecordsTask.execute(dishes);
            // Fetch dish info from APIs.
            FetchDishInfoFromAPITask fetchDishInfoFromAPITask = new FetchDishInfoFromAPITask(TextSearch.this);
            fetchDishInfoFromAPITask.execute(dishes);
        } else {
            // If user didn't type in any texts, toast a message.
            Toast.makeText(this, "Please type in dish name",
                    Toast.LENGTH_LONG).show();
        }
    }
}
