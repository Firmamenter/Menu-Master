package com.dc.menu_master.Account;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dc.menu_master.MainActivity;
import com.dc.menu_master.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This activity is used to show account view.
 */

public class Account extends AppCompatActivity {
    private TextView userName;
    private TextView userEmail;
    private Button mButton;
    private Typeface mTypeFace;
    private CardView mCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        mButton = (Button) findViewById(R.id.signOut);
        mCardView = (CardView) findViewById(R.id.account_cardview);

        // Set up card view.
        mCardView.setRadius(16);
        mCardView.setCardElevation(8);

        // Fetch and set user's information.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userName.setText("Name: " + user.getDisplayName());
        userEmail.setText("Email: " + user.getEmail());
        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/alegreya-regular.ttf");
        userName.setTypeface(mTypeFace);
        userEmail.setTypeface(mTypeFace);

        // Set up toolbar.
        Toolbar accountToolbar = (Toolbar) findViewById(R.id.account_toolbar);
        setSupportActionBar(accountToolbar);

        // Get a support ActionBar corresponding to this toolbar.
        ActionBar ab = getSupportActionBar();

        // Enable the Up button.
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Account");
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

    // When clicked, user gets signed out.
    public void signOut (View v) {
        AuthUI.getInstance().signOut(this);
        MainActivity.userName = null;
        finish();
    }
}
