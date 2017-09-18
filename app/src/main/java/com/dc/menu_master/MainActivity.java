package com.dc.menu_master;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dc.menu_master.Account.Account;
import com.dc.menu_master.Food.Food;
import com.dc.menu_master.Food.FoodDetail;
import com.dc.menu_master.Help.Help;
import com.dc.menu_master.History.History;
import com.dc.menu_master.ImageTasks.ImageUploadTask;
import com.dc.menu_master.MyFaves.MyFaves;
import com.dc.menu_master.TextSearch.TextSearch;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * MainActivity of the entire app.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int REQUEST_TAKE_PHOTO = 1;
    public static final int RC_SIGN_IN = 123;
    static MainActivity mainActivity;
    public static String userName;
    private String mCurrentPhotoPath;
    private ListView foodItems;
    private FloatingActionButton fab;

    private ArrayList<Food> resultsFromFoodAPI;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView nav_welcome;
    private TextView nav_userName;
    private Typeface mTypeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        // Set up FirebaseAuth UI.
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is singed in.
                } else {
                    // User is singed out, then we shall go into the sign-in flow.
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setIsSmartLockEnabled(false)
                                    .setTheme(R.style.DarkTheme)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        // Visualize list view if got data.
        resultsFromFoodAPI = (ArrayList<Food>) getIntent().getSerializableExtra("resultsFromFoodAPI");
        foodItems = (ListView) findViewById(R.id.foodList);
        if (resultsFromFoodAPI != null) {
            foodItems.setAdapter(new MainActivityAdapter(this, resultsFromFoodAPI));
            foodItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    Food foodData = (Food) foodItems.getItemAtPosition(position);
                    Intent showFoodDetail = new Intent(MainActivity.this, FoodDetail.class);
                    showFoodDetail.putExtra("foodDetail", foodData);
                    startActivity(showFoodDetail);
                }
            });
        }

        // Set up toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up floating action button.
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                "com.dc.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });

        // Set up navigation view.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        nav_welcome = (TextView) header.findViewById(R.id.nav_welcome);
        nav_userName = (TextView) header.findViewById(R.id.nav_userName);

        // Set texts in navigation view.
        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/alegreyasc-regular.ttf");
        nav_welcome.setTypeface(mTypeFace);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            nav_userName.setText(userName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // If image is taken and stored in internal storage, then we can upload it to server.
            ImageUploadTask imageUploadTask = new ImageUploadTask(MainActivity.this);
            imageUploadTask.execute(mCurrentPhotoPath);
        } else if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            } else if (resultCode == RESULT_OK) {
                userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                nav_userName.setText(userName);
                foodItems.setAdapter(null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static MainActivity getInstance(){
        return mainActivity;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_text_search:
                Intent textSearch = new Intent(MainActivity.this, TextSearch.class);
                startActivity(textSearch);
                return true;
            case R.id.action_signout:
                AuthUI.getInstance().signOut(this);
                userName = null;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Timer t = new Timer();
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Account.class));
                }
            }, 250);
        } else if (id == R.id.nav_history) {
            Timer t = new Timer();
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, History.class));
                }
            }, 250);
        } else if (id == R.id.nav_myfaves) {
            Timer t = new Timer();
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, MyFaves.class));
                }
            }, 250);
        } else if (id == R.id.nav_help) {
            Timer t = new Timer();
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Help.class));
                }
            }, 250);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Save image to internal storage.
    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, "Menu.jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
