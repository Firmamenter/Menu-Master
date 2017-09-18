package com.dc.menu_master.MyFaves;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dc.menu_master.Food.Food;
import com.dc.menu_master.Food.FoodDetail;
import com.dc.menu_master.MainActivity;
import com.dc.menu_master.MainActivityAdapter;
import com.dc.menu_master.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * This Activity shows a list of favorite dishes of a user.
 */

public class MyFaves extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userEmail;
    private ArrayList<Food> myFaves;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_faves);

        myFaves = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.fave_list);

        // Set up toolbar.
        Toolbar myfavesToolbar = (Toolbar) findViewById(R.id.myfaves_toolbar);
        setSupportActionBar(myfavesToolbar);

        // Get a support ActionBar corresponding to this toolbar.
        ActionBar ab = getSupportActionBar();

        // Enable the Up button.
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("My Faves");

        // Connect to Firebase DB.
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("myFaves").child(userEmail.replace(".", ","));

        // Fetch data.
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Food foodItem = postSnapshot.getValue(Food.class);
                    myFaves.add(foodItem);
                }
                // Set up listview.
                mListView.setAdapter(new MyfavesAdapter(MyFaves.this, myFaves));
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        Food foodData = (Food) mListView.getItemAtPosition(position);
                        Intent showFoodDetail = new Intent(MyFaves.this, FoodDetail.class);
                        showFoodDetail.putExtra("foodDetail", foodData);
                        startActivity(showFoodDetail);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError + "");
            }
        });
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
