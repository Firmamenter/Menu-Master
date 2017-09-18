package com.dc.menu_master.SplashScreen;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.dc.menu_master.DataTask.FetchLatestSearchedDishTask;
import com.dc.menu_master.MainActivity;
import com.dc.menu_master.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;

import static android.content.ContentValues.TAG;

/**
 * Splash screen of this app. If user has signed in, this activity will show user's latest searched dish in MainActivity.
 * If not, just start MainActivity.
 */

public class SplashScreen extends AppCompatActivity {
    // Set duration of wait.
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private TextView textMenu;
    private TextView textMaster;
    private Typeface mTypeFace;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String userEmail;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private HashSet<String> foodHistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        textMenu = (TextView) findViewById(R.id.splashScreen_menu);
        textMaster = (TextView) findViewById(R.id.splashScreen_master);
        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/alegreyasc-regular.ttf");
        textMenu.setTypeface(mTypeFace);
        textMaster.setTypeface(mTypeFace);

        // Set up realtime DB reference.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("user").child(userEmail.replace(".", ","));
            foodHistory = new HashSet<>();
        }

        // Set up FirebaseAuth UI.
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // If user is singed in, load info of the latest searched dish.
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String res = new String();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                res = (String) postSnapshot.getValue();
                            }

                            if (res.length() > 0) {
                                foodHistory.add(res);
                                FetchLatestSearchedDishTask fetchLatestSearchedDishTask = new FetchLatestSearchedDishTask(SplashScreen.this);
                                fetchLatestSearchedDishTask.execute(foodHistory);
                            } else {
                                /* New Handler to start the Menu-Activity
                                * and close this Splash-Screen after some seconds.*/
                                new Handler().postDelayed(new Runnable(){
                                    @Override
                                    public void run() {
                                /* Create an Intent that will start the Menu-Activity. */
                                        Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                                        SplashScreen.this.startActivity(mainIntent);
                                        SplashScreen.this.finish();
                                    }
                                }, SPLASH_DISPLAY_LENGTH);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message.
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });
                } else {
                    // User is singed out, then we shall go into the sign-in flow
                    /* New Handler to start the Menu-Activity
                     * and close this Splash-Screen after some seconds.*/
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            /* Create an Intent that will start the Menu-Activity. */
                            Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                            SplashScreen.this.startActivity(mainIntent);
                            SplashScreen.this.finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);
                }
            }
        };
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
}
