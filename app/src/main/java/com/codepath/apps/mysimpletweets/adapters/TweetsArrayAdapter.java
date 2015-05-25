package com.codepath.apps.mysimpletweets.adapters;


import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.activities.TimelineActivity;
import com.codepath.apps.mysimpletweets.activities.TweetComposeActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.UserAccountInformation;
import com.codepath.apps.mysimpletweets.utils.AppStatus;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvSince;
        TextView tvFavCount;
        TextView tvRetweetCount;
        ImageView ivReplyImage;
        ImageView ivFavImage;
        ImageView ivRetweetImage;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    public TweetsArrayAdapter(Context context, int resource, List<Tweet> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);

        ViewHolder viewHolder;
        if ( convertView == null ) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_tweet, parent, false);

            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvSince = (TextView) convertView.findViewById(R.id.tvSince);
            viewHolder.tvFavCount = (TextView) convertView.findViewById(R.id.tvFavCount);
            viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.ivReplyImage = (ImageView) convertView.findViewById(R.id.ivReply);
            viewHolder.ivFavImage = (ImageView) convertView.findViewById(R.id.ivFav);
            viewHolder.ivRetweetImage = (ImageView) convertView.findViewById(R.id.ivRetweet);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText("@"+tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvSince.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        if ( tweet.getFavoriteCount() > 0 ) {
            viewHolder.tvFavCount.setText(""+tweet.getFavoriteCount());
        } else {
            viewHolder.tvFavCount.setText("0");
        }

        if ( tweet.getRetweetCount() > 0 ) {
            viewHolder.tvRetweetCount.setText(""+tweet.getRetweetCount());
        } else {
            viewHolder.tvRetweetCount.setText("0");
        }

        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .into(viewHolder.ivProfileImage);

        if (tweet.isFavorited()) {
            viewHolder.ivFavImage.setImageResource(R.drawable.ic_fav_favorited );
        } else {
            viewHolder.ivFavImage.setImageResource(R.drawable.ic_fav);
        }

        /* Do not set OnClickListeners if offline */
        if (AppStatus.getInstance(getContext()).isOnline() == true ) {
            viewHolder.ivReplyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAccountInformation info = ((TimelineActivity) getContext()).getAccountInfo();
                    Log.d("DEBUG:", "Image Clicked");
                    Intent i = new Intent(getContext(), TweetComposeActivity.class);
                    i.putExtra(Constants.userInfo, info);
                    i.putExtra(Constants.tweetDetail, (java.io.Serializable) tweet);
                    getContext().startActivity(i);

                    Log.d("DEBUG:", info.getScreenName());
                }
            });

            viewHolder.ivFavImage.setOnClickListener(new View.OnClickListener() {
                final TwitterClient client = TwitterApplication.getRestClient();

                @Override
                public void onClick(View v) {
                    client.postFavTweet(tweet.isFavorited(), tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("DEBUG:", "Updated Favorites " + response.toString());
                            ((TimelineActivity) getContext()).onRefresh();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG:", "Could not Update Favorites " + errorResponse.toString());
                        }
                    });
                }
            });

            viewHolder.ivRetweetImage.setOnClickListener(new View.OnClickListener() {
                final TwitterClient client = TwitterApplication.getRestClient();

                @Override
                public void onClick(View v) {
                    client.retweet(tweet.getId(), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("DEBUG:", "Retweeted Successfully " + response.toString());
                            ((TimelineActivity) getContext()).onRefresh();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG:", "Could not retweet " + errorResponse.toString());
                        }
                    });
                }
            });
        }

        return convertView;
    }


    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            Date postedDate = sf.parse(rawJsonDate);
            relativeDate = getDateDifferenceForDisplay(postedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    //Got from StackOverflow
    public static String getDateDifferenceForDisplay(Date inputdate) {
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();

        now.setTime(new Date());
        then.setTime(inputdate);

        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = then.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = nowMs - thenMs;

        // Calculate difference in seconds
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffMinutes < 60) {
            return diffMinutes + "m ";

        } else if (diffHours < 24) {
            return diffHours + "h ";

        } else if (diffDays < 7) {
            return diffDays + "d ";

        } else {

            SimpleDateFormat todate = new SimpleDateFormat("MMM dd",
                    Locale.ENGLISH);

            return todate.format(inputdate);
        }
    }
}
