package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.NetStatus;
import com.codepath.apps.mysimpletweets.utils.Transform;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class TweetDetailActivity extends ActionBarActivity {

    private Tweet tweet;
    private User userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        tweet = (Tweet) getIntent().getSerializableExtra(Constants.tweetDetail);
        userInfo = (User) getIntent().getSerializableExtra(Constants.userInfo);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("@" + userInfo.getScreenName());

        setupTweetInfo(tweet);
    }

    private void setupTweetInfo(final Tweet tweet) {
        ImageView ivTweetImage = (ImageView) findViewById(R.id.ivTweetDetailProfileImage);
        TextView tvUserName = (TextView) findViewById(R.id.tvTweetDetailUserName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvTweetDetailScreenName);
        TextView tvBody = (TextView) findViewById(R.id.tvTweetDetailBody);
        TextView tvSince = (TextView) findViewById(R.id.tvTweetDetailSince);
        TextView tvRetweetValue = (TextView) findViewById(R.id.tvRetweetValue);
        TextView tvFavValue = (TextView) findViewById(R.id.tvFavoritesValue);
        ImageView ivReplyImage = (ImageView) findViewById(R.id.ivTweetDetailReply);

        ImageView ivRetweetImage = (ImageView) findViewById(R.id.ivTweetDetailRetweet);
        ImageView ivFavImage = (ImageView) findViewById(R.id.ivTweetDetailFavorites);
        ImageView ivTweetMedia = (ImageView) findViewById(R.id.ivTweetDetailMedia);

        Picasso.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .into(ivTweetImage);

        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvSince.setText(Transform.getRelativeTimeAgo(tweet.getCreatedAt()));
        tvRetweetValue.setText(""+tweet.getRetweetCount());
        tvFavValue.setText(""+tweet.getFavoriteCount());

        if ( tweet.getMedia() != null ) {
            ivTweetMedia.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(tweet.getMedia().getPhotoUrl())
                    .into(ivTweetMedia);
        } else {
            ivTweetMedia.setVisibility(View.INVISIBLE);
        }
        if (tweet.isFavorited()) {
            ivFavImage.setImageResource(R.drawable.ic_fav_favorited);
        } else {
            ivFavImage.setImageResource(R.drawable.ic_fav);
        }

        if (tweet.isRetweeted()) {
            ivRetweetImage.setImageResource(R.drawable.ic_retweeted);
        } else {
            ivRetweetImage.setImageResource(R.drawable.ic_retweet);
        }

        if (NetStatus.getInstance(this).isOnline() == true) {

            ivReplyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG:", "Image Clicked");
                    Intent i = new Intent(TweetDetailActivity.this, TweetComposeActivity.class);
                    i.putExtra(Constants.userInfo, userInfo);
                    i.putExtra(Constants.tweetDetail, tweet);
                    startActivity(i);

                    Log.d("DEBUG:", userInfo.getScreenName());
                }
            });

            ivFavImage.setOnClickListener(new View.OnClickListener() {

                final TwitterClient client = TwitterApplication.getRestClient();
                @Override
                public void onClick(View v) {
                    client.postFavTweet(tweet.isFavorited(), tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("DEBUG:", "Updated Favorites " + response.toString());
                            Tweet tweet = Tweet.fromJSON(response);

                            /* Reload the Activity with new information */
                            Intent refresh = new Intent(TweetDetailActivity.this, TweetDetailActivity.class);
                            refresh.putExtra(Constants.tweetDetail, tweet);
                            refresh.putExtra(Constants.userInfo, userInfo);
                            startActivity(refresh);//Start the same Activity
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG:", "Could not Update Favorites " + errorResponse.toString());
                        }
                    });
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
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
