package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends ActionBarActivity {

    private User userInfo;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userInfo = (User) getIntent().getSerializableExtra(Constants.userInfo);
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("@" + userInfo.getScreenName());

        client = TwitterApplication.getRestClient();

        setupUserTimelineActivity(savedInstanceState);
        populateProfileHeader();
    }

    private void populateProfileHeader() {
        TextView tvUserName = (TextView)findViewById(R.id.tvProfileName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvProfileScreenName);
        TextView tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        TextView tvTweetCount = (TextView) findViewById(R.id.tvTweetValue);
        TextView tvFollowerCount = (TextView) findViewById(R.id.tvFollowerValue);
        TextView tvFollowingCount = (TextView) findViewById(R.id.tvFollowingValue);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileActImage);

        tvUserName.setText(userInfo.getName());
        tvScreenName.setText(userInfo.getScreenName());
        tvTagLine.setText(userInfo.getTagLine());
        tvTweetCount.setText(""+userInfo.getTweetCount());
        tvFollowerCount.setText(""+userInfo.getFollowerCount());
        tvFollowingCount.setText(""+userInfo.getFollowingCount());

        Picasso.with(this)
                .load(userInfo.getProfileImageUrl())
                .into(ivProfileImage);

    }

    private void setupUserTimelineActivity(Bundle savedInstanceState) {

        if ( savedInstanceState == null ) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.Instance(userInfo);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
