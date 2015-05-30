package com.codepath.apps.mysimpletweets.fragments;


import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.UserAccountInformation;
import com.codepath.apps.mysimpletweets.utils.NetStatus;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeTimeLineFragment extends TweetsListFragment {

    /* Tracks the oldest tweet-ID that was received */
    private long oldestId;


    public static HomeTimeLineFragment Instance(UserAccountInformation info) {
        HomeTimeLineFragment fragment = new HomeTimeLineFragment();
        Bundle args = new Bundle();
        args.putSerializable("user_info", info);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTweets();
    }

    private void loadOfflineTweets() {
        Log.d("DEBUG:", "Loading Tweets from Offline Database");
        // Query ActiveAndroid for list of data
        List<Tweet> queryResults = new Select().from(Tweet.class)
                .orderBy("TweetUID DESC").execute();
        addAll(queryResults, true);
    }

    /**
     * Utility function to get the first set of tweets
     */
    private void getTweets() {

        if (NetStatus.getInstance(getActivity()).isOnline() == true) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());

                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);

                    addAll(tweets, true);

                    oldestId = getOldestTweetItemId();
                    Log.d("DEBUG:", "OldestID=" + oldestId);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.load_offline, Toast.LENGTH_SHORT).show();
            List<Tweet> queryResults = new Select().from(Tweet.class)
                    .orderBy("TweetUid").limit(100).execute();
            addAll(queryResults, true);
        }
    }

    /**
     * Method to get older tweets to allow for infinite pagination
     */
    private void getOlderTweets() {

        Log.d("DEBUG", "Getting Older Tweets");

        if (NetStatus.getInstance(getActivity()).isOnline() == true) {
            client.getOlderTweets(oldestId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());

                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                    addAll(tweets, false);

                    oldestId = getOldestTweetItemId();
                    Log.d("DEBUG:", "OldestID=" + oldestId);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.offline_cannot_getmore, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        getTweets();
        // Notify the Container that refresh has completed
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void loadMore() {
        Log.d("DEBUG:", "HomeTimeline loadMore() called");
        getOlderTweets();
    }
}
