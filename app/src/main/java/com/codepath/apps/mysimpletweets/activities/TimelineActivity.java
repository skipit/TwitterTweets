package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.fragments.HomeTimeLineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.UserAccountInformation;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.codepath.apps.mysimpletweets.utils.NetStatus;

import org.apache.http.Header;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity {


    /* Used to call the REST APIs */
    private TwitterClient client;

    private UserAccountInformation accountInfo;

    private HomeTimeLineFragment fragmentTweetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient();
        getUserInformation();


        /* If not null, the fragment is already inflated in memory */
        if (savedInstanceState == null) {
            fragmentTweetsList = (HomeTimeLineFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }
    }

    public UserAccountInformation getAccountInfo() {

        return accountInfo;
    }

    private void getUserInformation() {
        if ( ( accountInfo == null ) &&
                ( NetStatus.getInstance(this).isOnline() == true ) ) {
            client.getAccountInformation(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    accountInfo = UserAccountInformation.fromJSONObject(response);
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

        /* Cannot compose if not online */
        if ( id == R.id.action_compose ) {
            if ( NetStatus.getInstance(this).isOnline() == true ) {
                composeMessage();
            } else {
                Toast.makeText(this, R.string.offline_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void composeMessage() {
        UserAccountInformation info = accountInfo;

        Intent i =  new Intent(TimelineActivity.this, TweetComposeActivity.class);
        i.putExtra(Constants.userInfo, info);
        startActivity(i);
    }

    public void onRefresh() {
        fragmentTweetsList.onRefresh();
    }


}
