package com.codepath.apps.mysimpletweets;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    /* Used to call the REST APIs */
    private TwitterClient client;

    /* Holds the tweets and adapts it to the ListView */
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;

    /* The Handle to the SwipeRefresh */
    private SwipeRefreshLayout swipeContainer;

    /* Tracks the most recent tweet-ID that was received */
    private long recentTweetId;

    /* Tracks the oldest tweet-ID that was received */
    private long oldestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setupSwipeContainer();
        setupTweetListView();

         /* Initialize the TweetId */
        recentTweetId = 1;

        client = TwitterApplication.getRestClient();

        getTweets();
    }

    public void setupSwipeContainer() {
        //// Set up the Swipe Container
        //Get the Swipe Container
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        //Set the Listener
        swipeContainer.setOnRefreshListener(this);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * Initialize the tweets ArrayList, and set the adapter and the scroll listeners
     */
    private void setupTweetListView() {
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>();
        aTweets = new TweetsArrayAdapter(this, R.layout.item_tweet, tweets);
        lvTweets.setAdapter(aTweets);



        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getOlderTweets();
            }
        });
    }

    /**
     * Utility function to get the first set of tweets
     */
    private void getTweets() {

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);

                aTweets.clear(); /* Clear the items in the array to reload */
                aTweets.addAll(tweets);

                oldestId = aTweets.getItem(aTweets.getCount()-1).getUid();
                Log.d("DEBUG:", "OldestID="+oldestId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        } );
    }

    /**
     * Method to get older tweets to allow for infinite pagination
     */
    private void getOlderTweets () {

        Log.d("DEBUG", "Getting Older Tweets");

        client.getOlderTweets(oldestId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                aTweets.addAll(tweets);

                oldestId = aTweets.getItem(aTweets.getCount() - 1).getUid();
                Log.d("DEBUG:", "OldestID=" + oldestId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getTweets();
        // Notify the Container that refresh has completed
        swipeContainer.setRefreshing(false);
    }
}
