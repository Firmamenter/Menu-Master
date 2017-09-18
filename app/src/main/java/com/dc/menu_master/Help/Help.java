package com.dc.menu_master.Help;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.dc.menu_master.R;

/**
 * This Activity is used to show Help view.
 */

public class Help extends AppCompatActivity {
    private CardView anwsers;
    private CardView feedback;
    private Typeface mTypeface;
    private TextView title1;
    private TextView title2;
    private TextView qa1;
    private TextView qa2;
    private TextView qa3;
    private TextView feedback_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        anwsers = (CardView) findViewById(R.id.anwsers);
        feedback = (CardView) findViewById(R.id.feedback);
        title1 = (TextView) findViewById(R.id.title1);
        title2 = (TextView) findViewById(R.id.title2);
        qa1 = (TextView) findViewById(R.id.qa1);
        qa2 = (TextView) findViewById(R.id.qa2);
        qa3 = (TextView) findViewById(R.id.qa3);
        feedback_email = (TextView) findViewById(R.id.feedback_email);

        // Set text font.
        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/alegreya-regular.ttf");
        title1.setTypeface(mTypeface);
        title2.setTypeface(mTypeface);
        qa1.setTypeface(mTypeface);
        qa2.setTypeface(mTypeface);
        qa3.setTypeface(mTypeface);
        feedback_email.setTypeface(mTypeface);

        // Set up card view.
        anwsers.setRadius(16);
        feedback.setRadius(16);
        anwsers.setCardElevation(8);
        feedback.setCardElevation(8);

        // Set up toolbars.
        Toolbar helpToolbar = (Toolbar) findViewById(R.id.help_toolbar);
        setSupportActionBar(helpToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Help");
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
