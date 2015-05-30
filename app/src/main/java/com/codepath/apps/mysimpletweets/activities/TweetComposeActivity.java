package com.codepath.apps.mysimpletweets.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.UserAccountInformation;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.NetStatus;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class TweetComposeActivity extends ActionBarActivity {

    private Menu menu;
    private UserAccountInformation info;
    private Tweet tweet;
    MenuItem miRemainingChar;
    /* Used to call the REST APIs */
    private TwitterClient client;

    EditText etTweet;

    private final int MAX_TWEET_LENGTH = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_compose);

        info = (UserAccountInformation) getIntent().getSerializableExtra(Constants.userInfo);
        tweet = (Tweet) getIntent().getSerializableExtra(Constants.tweetDetail);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();


        actionBar.setTitle("@" + info.getScreenName());
        client = TwitterApplication.getRestClient();
        setupComposeTweetActivity();

    }


    private void setupComposeTweetActivity() {
        etTweet = (EditText) findViewById(R.id.etTweetBody);
        if (tweet != null) {
            etTweet.append("@" + tweet.getUser().getScreenName() + " ");
        }
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textLength = etTweet.getText().length();
                int remainingCharacters = MAX_TWEET_LENGTH - textLength;
                miRemainingChar.setTitle("" + remainingCharacters);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_compose, menu);

        this.menu = menu;
        miRemainingChar = menu.findItem(R.id.tweet_length);
        miRemainingChar.setTitle("" + MAX_TWEET_LENGTH);

        return true;
    }

    private void sendTweet() {
        Log.d("DEBUG", "Sending Tweet");

        final Activity parentActivity = this;

        if (tweet == null) {
            client.postTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", "Post Updated");
                    etTweet.setText("");

                    //-- Lets go back to the parent Activity.
                    NavUtils.navigateUpFromSameTask(parentActivity);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Toast.makeText(getApplicationContext(), "Could not post the tweet\n Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            client.postTweetReply(etTweet.getText().toString(), tweet.getUid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", "Reply Updated");
                    etTweet.setText("");

                    //-- Lets go back to the parent Activity.
                    NavUtils.navigateUpFromSameTask(parentActivity);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Toast.makeText(getApplicationContext(), "Could not post the tweet\n Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        }
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

        if (id == R.id.action_send) {
            if (NetStatus.getInstance(this).isOnline() == true) {
                sendTweet();
            } else {
                Toast.makeText(this, R.string.offline_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
