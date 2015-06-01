package com.codepath.apps.mysimpletweets.models;


import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
    @Column(name = "TweetUid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "Body")
    private String body;
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "Media", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Media media;
    @Column(name = "CreatedAt")
    private String createdAt;
    @Column(name = "RetweetCount")
    private int retweetCount;
    @Column(name = "FavoriteCount")
    private int favoriteCount;
    @Column(name = "Favorited")
    private boolean favorited;
    @Column(name = "Retweeted")
    private boolean retweeted;

    @Column(name = "RetweetId")
    private long retweetId;

    public Tweet() {
        super();
        retweetCount = 0;
        favoriteCount = 0;
        favorited = false;
        retweeted = false;

    }


    public long getRetweetId() {
        return retweetId;
    }

    public Media getMedia() {
        return media;
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

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public List<Tweet> tweets() {
        return getMany(Tweet.class, "Tweet");
    }

    public static Tweet fromJSON(JSONObject json) {
        Tweet tweet = new Tweet();

        try {
            tweet.body = json.getString("text");
            tweet.uid = json.getLong("id");

            tweet.user = User.fromJSON(json.getJSONObject("user"));

            try {
                /* For now just have reference to the first Media */
                tweet.media = Media.fromJSONArray(json.getJSONObject("entities").getJSONArray("media")).get(0);
                Log.d("DEBUG:", "Media Found");
            } catch (JSONException e) {
                Log.d("DEBUG:", "no media found @ " + tweet.getBody());
                tweet.media = null;
            }

            tweet.createdAt = json.getString("created_at");
            try {
                tweet.retweetCount = json.getInt("retweet_count");
            } catch (JSONException e) {
                Log.d("DEBUG:", "no retweet count found");
                tweet.retweetCount = 0;
            }

            try {
                tweet.favoriteCount = json.getInt("favorite_count");
            } catch (JSONException e) {
                Log.d("DEBUG:", "no favorite count found");
                tweet.favoriteCount = 0;
            }

            tweet.favorited = json.getBoolean("favorited");
            tweet.retweeted = json.getBoolean("retweeted");

            try {
                tweet.retweetId = json.getJSONObject("retweeted_status").getLong("id");
            } catch (JSONException e) {
                Log.d("DEBUG:", "No Retweet Status found");
                tweet.favoriteCount = 0;
            }
            // TODO: Temporary Location to save tweet.
            tweet.save();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {

        ArrayList<Tweet> tweets = new ArrayList<>();


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);

                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue; // Process the rest of the tweets.
            }
        }
        return tweets;
    }
}
