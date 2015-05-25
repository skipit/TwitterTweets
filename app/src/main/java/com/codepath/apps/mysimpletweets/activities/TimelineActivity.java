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

public class TimelineActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    /* Used to call the REST APIs */
    private TwitterClient client;

    /* Holds the tweets and adapts it to the ListView */
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;

    private UserAccountInformation accountInfo;

    /* The Handle to the SwipeRefresh */
    private SwipeRefreshLayout swipeContainer;

    /* Tracks the oldest tweet-ID that was received */
    private long oldestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setupSwipeContainer();
        setupTweetListView();

        client = TwitterApplication.getRestClient();
        getUserInformation();
    }

    @Override
    protected void onResume() {
        loadOfflineTweets();
        getTweets();
        super.onResume();
    }

    private void loadOfflineTweets() {
        Log.d("DEBUG:", "Loading Tweets from Offline Database");
        // Query ActiveAndroid for list of data
        List<Tweet> queryResults = new Select().from(Tweet.class)
                .orderBy("TweetUID DESC").execute();
        aTweets.addAll(queryResults);
    }

    public UserAccountInformation getAccountInfo() {
        return accountInfo;
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

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = aTweets.getItem(position);
                Log.d("DEBUG:","Item clicked at position " + position);

                Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                i.putExtra(Constants.tweetDetail, tweet );
                startActivity(i);
            }
        });
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

                    aTweets.clear(); /* Clear the items in the array to reload */
                    aTweets.addAll(tweets);


                    oldestId = aTweets.getItem(aTweets.getCount() - 1).getUid();
                    Log.d("DEBUG:", "OldestID=" + oldestId);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }else {
            Toast.makeText(this, R.string.load_offline, Toast.LENGTH_SHORT).show();
            aTweets.clear();
            List<Tweet> queryResults = new Select().from(Tweet.class)
                    .orderBy("TweetUid").limit(100).execute();
            aTweets.addAll(queryResults);
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
                    aTweets.addAll(tweets);

                    oldestId = aTweets.getItem(aTweets.getCount() - 1).getUid();
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
        // Notify the Container that refresh has completed
        swipeContainer.setRefreshing(false);
    }



}
