package com.codepath.apps.mysimpletweets.models;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tweet {
    private String body;
    private long uid;
    private User user;
    private String createdAt;
    private int retweetCount;
    private int favoriteCount;

    public Tweet() {
        retweetCount = 0;
        favoriteCount = 0;
    }


    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public static Tweet fromJSON(JSONObject json) {
        Tweet tweet = new Tweet();

        try {
            tweet.body = json.getString("text");
            tweet.uid = json.getLong("id");

            tweet.user = User.fromJSON(json.getJSONObject("user"));

            tweet.createdAt = json.getString("created_at");
            try {
                tweet.retweetCount = json.getInt("retweet_count");
            } catch (JSONException e){
                Log.d("DEBUG:", "no retweet count found");
                tweet.retweetCount = 0;
            }

            try {
                tweet.favoriteCount = json.getInt("favourites_count");
            } catch (JSONException e) {
                Log.d("DEBUG:","no favorite count found");
                tweet.favoriteCount = 0;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {

        ArrayList<Tweet> tweets = new ArrayList<>();


        for ( int i = 0; i < jsonArray.length(); i ++ ) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);

                if ( tweet != null ) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue; // Process the rest of the tweets.
            }
            Tweet tweet = Tweet.fromJSON(tweetJson);

        }
        return tweets;
    }
}
