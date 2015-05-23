package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.UserAccountInformation;

public class TweetComposeActivity extends ActionBarActivity {

    private Menu menu;
    private UserAccountInformation info;
    MenuItem etRemainingChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_compose);

        info = (UserAccountInformation) getIntent().getSerializableExtra("user_info");

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("@" + info.getScreenName());

        setupComposeTweetActivity();
    }


    private void setupComposeTweetActivity() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_compose, menu);

        this.menu = menu;
        etRemainingChar = menu.findItem(R.id.tweet_length);
        etRemainingChar.setTitle(""+140);

        return true;
    }

    private void sendTweet() {
        Log.d("DEBUG", "Sending Tweet");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if ( id == R.id.action_send ) {
            sendTweet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
