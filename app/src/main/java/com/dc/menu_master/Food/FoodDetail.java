package com.dc.menu_master.Food;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dc.menu_master.DataTask.FetchRecipeByIdTask;
import com.dc.menu_master.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

/**
 * This Activity is used to show dish's detail information. You can go into this activity either from MainActivity or MyFaves.
 */

public class FoodDetail extends AppCompatActivity {
    private TextView foodTitle;
    private ImageView foodImage;
    private Food foodItem;
    private TextView ingredients;
    private ScrollView mScrollView;
    private Typeface mTypeFace;
    private Button mButton;
    private Boolean flag;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userEmail;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_detail);

        foodTitle = (TextView) findViewById(R.id.foodTitle);
        foodImage = (ImageView) findViewById(R.id.foodImage);
        ingredients = (TextView) findViewById(R.id.ingredients);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mButton = (Button) findViewById(R.id.fave);

        // Set up toolbar.
        Toolbar foodDetailToolbar = (Toolbar) findViewById(R.id.food_detail_toolbar);
        setSupportActionBar(foodDetailToolbar);

        // Get a support ActionBar corresponding to this toolbar.
        ActionBar ab = getSupportActionBar();

        // Enable the Up button.
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Dish Detail");

        // Get food item from intent.
        foodItem = (Food)getIntent().getExtras().getSerializable("foodDetail");
        foodTitle.setText(foodItem.getName());
        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/alegreya-regular.ttf");
        foodTitle.setTypeface(mTypeFace);

        // Loading image.
        Picasso.with(this)
                .load(foodItem.getImageUrl())
                .placeholder(R.drawable.ic_block_black)
                .error(R.drawable.ic_error_outline)
                .into(foodImage);

        // Check if the food item has ingredient information. If no, execute FetchRecipeByIdTask. Otherwise, just show ingredients.
        if (foodItem.getRecipes().size() == 0) {
            FetchRecipeByIdTask fetchRecipeTask = new FetchRecipeByIdTask(FoodDetail.this);
            try {
                foodItem = (Food) fetchRecipeTask.execute(foodItem).get();
                String temp = "Ingredients :" + "\n";
                for (String ingredient : foodItem.getRecipes()) {
                    temp += ingredient + "\n";
                }
                ingredients.setText(temp);
                ingredients.setTypeface(mTypeFace);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            String temp = "Ingredients :" + "\n";
            for (String ingredient : foodItem.getRecipes()) {
                temp += ingredient + "\n";
            }
            ingredients.setText(temp);
            ingredients.setTypeface(mTypeFace);
        }

        // Set key for storing favorite dish in Firebase DB.
        key = foodItem.getName().trim().replace(".", ",").replace(" ", "%20").replace("/", "%10") +
                foodItem.getImageUrl().trim().replace(".", ",").replace(" ", "%20").replace("/", "%10");

        // Connect to Firebase DB.
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("myFaves").child(userEmail.replace(".", ","));

        // Check whether user has favored this dish before and set button image.
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(key)) {
                    flag = true;
                    mButton.setBackgroundResource(R.drawable.ic_menu_myfaves);
                } else {
                    flag = false;
                    mButton.setBackgroundResource(R.drawable.ic_favorite_border);
                }
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

    public void faves(View v) {
        if (flag == false) {
            // If user hasn't favored this dish, upload dish information to Firebase DB.
            myRef.child(key).setValue(foodItem);

            // Set flag to true.
            flag = true;

            // Change button image.
            mButton.setBackgroundResource(R.drawable.ic_menu_myfaves);

            // Show success.
            Toast.makeText(this, "I like this one!",
                    Toast.LENGTH_LONG).show();
        } else {
            // If user has already favored this dish, delete this dish information from Firebase DB.
            myRef.child(key).removeValue();

            // Set flag to false.
            flag = false;

            // Change button image.
            mButton.setBackgroundResource(R.drawable.ic_favorite_border);

            // Show success.
            Toast.makeText(this, "I don't love this any more!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
