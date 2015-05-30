package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragments.HomeTimeLineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.NetStatus;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity {


    /* Used to call the REST APIs */
    private TwitterClient client;

    private User userInfo;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient();
        getUserInformation();

        setupViewPager();
    }

    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);

    }

    private void getUserInformation() {
        if ((userInfo == null) &&
                (NetStatus.getInstance(this).isOnline() == true)) {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    userInfo = User.fromJSON(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
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

        if (NetStatus.getInstance(this).isOnline() == true) {
            if (id == R.id.action_compose) {
                composeMessage();
            } else if (id == R.id.action_profile) {
                showProfileInfo();
            }
            return true;
        } else {
            Toast.makeText(this, R.string.offline_error, Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    private void showProfileInfo() {
        Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
        i.putExtra(Constants.userInfo, userInfo);
        startActivity(i);
    }

    private void composeMessage() {
        Intent i = new Intent(TimelineActivity.this, TweetComposeActivity.class);
        i.putExtra(Constants.userInfo, userInfo);
        startActivity(i);
    }

    // Return the order of fragments in the view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;

        private HomeTimeLineFragment homeTimeLineFragment = null;
        private MentionsTimelineFragment mentionsTimelineFragment = null;
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                case 0:
                    if (homeTimeLineFragment == null) {
                        homeTimeLineFragment = HomeTimeLineFragment.Instance(userInfo);
                    }
                    return homeTimeLineFragment;

                case 1:
                    if (mentionsTimelineFragment == null) {
                        mentionsTimelineFragment = MentionsTimelineFragment.Instance(userInfo);
                    }

                    return mentionsTimelineFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

}
