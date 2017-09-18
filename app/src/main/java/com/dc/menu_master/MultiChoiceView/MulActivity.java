package com.dc.menu_master.MultiChoiceView;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dc.menu_master.DataTask.FetchDishInfoFromAPITask;
import com.dc.menu_master.DataTask.UploadSearchRecordsTask;
import com.dc.menu_master.R;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This Activity shows recognized dish names from TextRecTask.
 * User may choose multiple dishes and click the button to search information about them.
 */

public class MulActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> groups;
    private MulAdapter adapter;
    private Button showChecked;
    private HashSet<String> returnTexts;
    private FetchDishInfoFromAPITask fetchDishInfoFromAPITask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_texts);

        listView = (ListView) this.findViewById(R.id.list);
        showChecked = (Button) findViewById(R.id.showChecked);
        returnTexts = new HashSet<>();

        // Get the recognized texts from TextRecTask.
        groups = getIntent().getStringArrayListExtra("recTexts");
        if (groups == null || groups.size() == 0) {
            for (int i = 0; i < 2; i++) {
                Toast.makeText(this, "Sorry, we can't detect any texts in the uploaded picture, please try again.",
                        Toast.LENGTH_LONG).show();
            }
        }
        AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // Get ViewHolder instance.
                MulAdapter.ViewHolder viewHolder = (MulAdapter.ViewHolder) view.getTag();
                viewHolder.cb.toggle();
                // Record status of this checkbox.
                MulAdapter.getIsSelected().put(position, viewHolder.cb.isChecked());
                if (viewHolder.cb.isChecked()) {
                    returnTexts.add(groups.get(position));
                } else {
                    returnTexts.remove(groups.get(position));
                }
            }
        };
        adapter = new MulAdapter(this, groups);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listItemClickListener);

        Toolbar recTextsToolbar = (Toolbar) findViewById(R.id.rec_texts_toolbar);
        setSupportActionBar(recTextsToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Please Select Dishes");
    }

    public void showCheckedItem (View v) {
        if (returnTexts.isEmpty()) {
            // If user didn't select any dishes, toast a notification.
            Toast.makeText(this, "Please select some dishes.",
                    Toast.LENGTH_LONG).show();
        } else {
            // If user chose some dishes, upload these choices as search history. Meanwhile, call food APIs to get dishes' info.
            UploadSearchRecordsTask uploadSearchRecordsTask = new UploadSearchRecordsTask();
            uploadSearchRecordsTask.execute(returnTexts);
            fetchDishInfoFromAPITask = new FetchDishInfoFromAPITask(MulActivity.this);
            fetchDishInfoFromAPITask.execute(returnTexts);
        }
    }
}
