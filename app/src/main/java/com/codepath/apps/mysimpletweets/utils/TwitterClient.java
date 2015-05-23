package com.codepath.apps.mysimpletweets.utils;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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

    public void getOlderTweets(long olderThan, AsyncHttpResponseHandler handler ) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", NEXT_PAGE_COUNT);
        params.put("max_id", (olderThan -1));
        getClient().get(apiUrl, params, handler);
    }

    /**
     * Utility to get account details
     *
     * @param handler
     */
    public void getAccountInformation(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/settings.json");
        getClient().get(apiUrl, handler);
    }


    public void postTweet(String tweet, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        getClient().post(apiUrl, params, handler);
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