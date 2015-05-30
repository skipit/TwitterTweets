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

public class MentionsTimelineFragment extends TweetsListFragment {
    /* Tracks the oldest tweet-ID that was received */
    private long oldestId;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMentionsTweets();
    }

    public static MentionsTimelineFragment Instance(UserAccountInformation info) {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable("user_info", info);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Utility function to get the first set of tweets
     */
    private void getMentionsTweets() {

        if (NetStatus.getInstance(getActivity()).isOnline() == true) {
            client.getMentionsTimeline(new JsonHttpResponseHandler() {
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
    private void getMentionsOlderTweets() {

        Log.d("DEBUG", "Getting Older Tweets");

        if (NetStatus.getInstance(getActivity()).isOnline() == true) {
            client.getMentionsOlderTweets(oldestId, new JsonHttpResponseHandler() {
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
        getMentionsTweets();
        // Notify the Container that refresh has completed
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void loadMore() {
        Log.d("DEBUG:", "MentionsTimeline loadMore() called");
        getMentionsOlderTweets();
    }
}
