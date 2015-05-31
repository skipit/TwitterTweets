package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.Transform;
import com.squareup.picasso.Picasso;

public class TweetDetailActivity extends ActionBarActivity {

    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        tweet = (Tweet) getIntent().getSerializableExtra(Constants.tweetDetail);

        setupTweetInfo(tweet);
    }

    private void setupTweetInfo(Tweet tweet) {
        ImageView ivTweetImage = (ImageView) findViewById(R.id.ivTweetDetailProfileImage);
        TextView tvUserName = (TextView) findViewById(R.id.tvTweetDetailUserName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvTweetDetailScreenName);
        TextView tvBody = (TextView) findViewById(R.id.tvTweetDetailBody);
        TextView tvSince = (TextView) findViewById(R.id.tvTweetDetailSince);

        Picasso.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .into(ivTweetImage);

        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvSince.setText(Transform.getRelativeTimeAgo(tweet.getCreatedAt()));
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
