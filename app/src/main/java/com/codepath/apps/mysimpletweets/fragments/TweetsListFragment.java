package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.TweetDetailActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.EndlessScrollListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class TweetsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TweetsArrayAdapter.OnRefreshListener {


    /* Holds the tweets and adapts it to the ListView */
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;


    /* The Handle to the SwipeRefresh */
    protected SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTweetList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        setupSwipeContainer(v);
        setupTweetListView(v);
        return v;
    }

    public void setupSwipeContainer(View v) {
        //// Set up the Swipe Container
        //Get the Swipe Container
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        //Set the Listener
        swipeContainer.setOnRefreshListener(this);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupTweetList() {
        tweets = new ArrayList<Tweet>();
        aTweets = new TweetsArrayAdapter(getActivity(), R.layout.item_tweet, tweets);
        aTweets.setOnRefreshListener(this);
    }

    /**
     * Initialize the tweets ArrayList, and set the adapter and the scroll listeners
     */
    private void setupTweetListView(View v) {
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);

        lvTweets.setAdapter(aTweets);


        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore();
            }
        });

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = aTweets.getItem(position);
                Log.d("DEBUG:","Item clicked at position " + position);

                Intent i = new Intent(getActivity(), TweetDetailActivity.class);
                i.putExtra(Constants.tweetDetail, tweet );
                startActivity(i);
            }
        });
    }

    public void addAll(List<Tweet> tweets, boolean clear ) {

        if ( true == clear ) {
            aTweets.clear();
        }
        aTweets.addAll(tweets);

    }

    public long getOldestTweetItemId() {
        return aTweets.getItem(aTweets.getCount() - 1).getUid();
    }

    public abstract void loadMore();

    @Override
    public void tweetDataSetChangedNotify() {
        onRefresh();
    }
}
