package com.codepath.apps.mysimpletweets.utils;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "htuvjlcG4K228mqql1pB3Q644";       // Change this
    public static final String REST_CONSUMER_SECRET = "CjCdCiFlBBisKRRDUNnmsb3L6RX6O6SuxDEH5MdRyk82sv2J43"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

    private static final int NEXT_PAGE_COUNT = 30;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", NEXT_PAGE_COUNT);
        getClient().get(apiUrl, params, handler);
    }

    public void getOlderTweets(long olderThan, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", NEXT_PAGE_COUNT);
        params.put("max_id", (olderThan - 1));
        getClient().get(apiUrl, params, handler);
    }

    public void retweet(boolean retweetStatus, long tweetId, AsyncHttpResponseHandler handler) {

        String apiUrl;

        if (retweetStatus == false) {
            apiUrl = getApiUrl("statuses/retweet/" + tweetId + ".json");
        } else {
            apiUrl = getApiUrl("statuses/destroy/" + tweetId);
        }
        //RequestParams params = new RequestParams();
        //params.put("id", tweetId);
        Log.d("DEBUG:", "URL: " + apiUrl);
        getClient().post(apiUrl, handler);

    }

    /**
     * Utility to get account details
     *
     * @param handler
     */
    public void getUserInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        Log.d("DEBUG:", apiUrl);
        getClient().get(apiUrl, handler);
    }

    public void postTweet(String tweet, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        getClient().post(apiUrl, params, handler);
    }

    public void postTweetReply(String tweet, long replyID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        params.put("in_reply_to_status_id", replyID);
        getClient().post(apiUrl, params, handler);
    }

    public void postFavTweet(boolean favoriteStatus, long favTweet, AsyncHttpResponseHandler handler) {
        String apiUrl;
        if (favoriteStatus == false) {
            apiUrl = getApiUrl("favorites/create.json");
        } else {
            apiUrl = getApiUrl("favorites/destroy.json");
        }
        RequestParams params = new RequestParams();
        params.put("id", favTweet);
        getClient().post(apiUrl, params, handler);
    }

    public void getTweetInfo(long tweetId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/show.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        getClient().post(apiUrl, params, handler);
    }

    public void getMentionsTimeline(JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", NEXT_PAGE_COUNT);
        getClient().get(apiUrl, params, handler);
    }

    public void getMentionsOlderTweets(long olderThan, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", NEXT_PAGE_COUNT);
        params.put("max_id", (olderThan - 1));
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", NEXT_PAGE_COUNT);
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);
    }

    public void getOlderUserTimeline(String screenName, long olderThan, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("count", NEXT_PAGE_COUNT);
        params.put("max_id", (olderThan - 1));
        getClient().get(apiUrl, params, handler);
    }


	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}