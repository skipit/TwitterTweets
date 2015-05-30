package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment;
import com.codepath.apps.mysimpletweets.models.UserAccountInformation;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.codepath.apps.mysimpletweets.utils.NetStatus;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity implements TweetsListFragment.RefreshListListener, TweetsListFragment.LoadMoreItemListener {


    /* Tracks the oldest tweet-ID that was received */
    private long oldestId;

    /* Used to call the REST APIs */
    private TwitterClient client;

    private UserAccountInformation accountInfo;

    private TweetsListFragment fragmentTweetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient();
        getUserInformation();


        /* If not null, the fragment is already inflated in memory */
        if (savedInstanceState == null) {
            fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }

        getTweets();
    }

    public UserAccountInformation getAccountInfo() {
        return accountInfo;
    }


    private void loadOfflineTweets() {
        Log.d("DEBUG:", "Loading Tweets from Offline Database");
        // Query ActiveAndroid for list of data
        List<Tweet> queryResults = new Select().from(Tweet.class)
                .orderBy("TweetUID DESC").execute();
        fragmentTweetsList.addAll(queryResults, true);
    }

    private void getUserInformation() {
        if ( ( accountInfo == null ) &&
                ( NetStatus.getInstance(this).isOnline() == true ) ) {
            client.getAccountInformation(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    accountInfo = UserAccountInformation.fromJSONObject(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }


    /**
     * Utility function to get the first set of tweets
     */
    private void getTweets() {

        if ( NetStatus.getInstance(this).isOnline() == true ) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());

                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);

                    fragmentTweetsList.addAll(tweets, true);

                    oldestId = fragmentTweetsList.getOldestTweetItemId();
                    Log.d("DEBUG:", "OldestID=" + oldestId);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }else {
            Toast.makeText(this, R.string.load_offline, Toast.LENGTH_SHORT).show();
            List<Tweet> queryResults = new Select().from(Tweet.class)
                    .orderBy("TweetUid").limit(100).execute();
            fragmentTweetsList.addAll(queryResults, true);
        }
    }

    /**
     * Method to get older tweets to allow for infinite pagination
     */
    private void getOlderTweets () {

        Log.d("DEBUG", "Getting Older Tweets");

        if ( NetStatus.getInstance(this).isOnline() == true ) {
            client.getOlderTweets(oldestId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());

                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                    fragmentTweetsList.addAll(tweets, false);

                    oldestId = fragmentTweetsList.getOldestTweetItemId();
                    Log.d("DEBUG:", "OldestID=" + oldestId);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        } else {
            Toast.makeText(this, R.string.offline_cannot_getmore, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
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

        /* Cannot compose if not online */
        if ( id == R.id.action_compose ) {
            if ( NetStatus.getInstance(this).isOnline() == true ) {
                composeMessage();
            } else {
                Toast.makeText(this, R.string.offline_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void composeMessage() {
        UserAccountInformation info = accountInfo;

        Intent i =  new Intent(TimelineActivity.this, TweetComposeActivity.class);
        i.putExtra(Constants.userInfo, info);
        startActivity(i);
    }

    @Override
    public void onRefresh() {
        getTweets();
    }

    @Override
    public void onLoadMore() {
        getOlderTweets();
    }


}
