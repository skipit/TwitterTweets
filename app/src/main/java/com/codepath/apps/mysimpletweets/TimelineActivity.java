package com.codepath.apps.mysimpletweets;

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

public class TimelineActivity extends ActionBarActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;

    /* Tracks the most recent tweet-ID that was received */
    private long sinceId;

    /* Tracks the oldest tweet-ID that was received */
    private long oldestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>();
        aTweets = new TweetsArrayAdapter(this, R.layout.item_tweet, tweets);
        lvTweets.setAdapter(aTweets);

        sinceId = 1;

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getOlderTweets();
            }
        });
        client = TwitterApplication.getRestClient();

        getTweets();
    }

    private void getTweets() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
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

    private void getOlderTweets () {
        Log.d("DEBUG", "Getting Older Tweets");

        client.getOlderTweets(oldestId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
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

    private void refreshHomeTimeline() {

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
}
