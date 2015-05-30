package com.codepath.apps.mysimpletweets.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.activities.TweetComposeActivity;
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

import java.util.List;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private OnRefreshListener onRefreshListener;
    private User userInfo;


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

    public interface OnRefreshListener {
        public void tweetDataSetChangedNotify();
    }

    public TweetsArrayAdapter(Context context, int resource, List<Tweet> objects, User userInfo ) {
        super(context, resource, objects);

        this.userInfo = userInfo;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.onRefreshListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
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
        viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvSince.setText(Transform.getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        if (tweet.getFavoriteCount() > 0) {
            viewHolder.tvFavCount.setText("" + tweet.getFavoriteCount());
        } else {
            viewHolder.tvFavCount.setText("0");
        }

        if (tweet.getRetweetCount() > 0) {
            viewHolder.tvRetweetCount.setText("" + tweet.getRetweetCount());
        } else {
            viewHolder.tvRetweetCount.setText("0");
        }

        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .into(viewHolder.ivProfileImage);

        if (tweet.isFavorited()) {
            viewHolder.ivFavImage.setImageResource(R.drawable.ic_fav_favorited);
        } else {
            viewHolder.ivFavImage.setImageResource(R.drawable.ic_fav);
        }

        if (tweet.isRetweeted()) {
            viewHolder.ivRetweetImage.setImageResource(R.drawable.ic_retweeted);
        } else {
            viewHolder.ivRetweetImage.setImageResource(R.drawable.ic_retweet);
        }

        /* Do not set OnClickListeners if offline */
        if (NetStatus.getInstance(getContext()).isOnline() == true) {
            viewHolder.ivReplyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG:", "Image Clicked");
                    Intent i = new Intent(getContext(), TweetComposeActivity.class);
                    i.putExtra(Constants.userInfo, userInfo);
                    i.putExtra(Constants.tweetDetail, (java.io.Serializable) tweet);
                    getContext().startActivity(i);

                    Log.d("DEBUG:", userInfo.getScreenName());
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
                            onRefreshListener.tweetDataSetChangedNotify();
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
                    client.retweet(tweet.isRetweeted(), tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("DEBUG:", "Retweeted Successfully " + response.toString());
                            onRefreshListener.tweetDataSetChangedNotify();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG:", "Could not retweet " + errorResponse.toString());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("DEBUG:", "Could not retweet " + responseString);
                        }
                    });
                }
            });
        }

        return convertView;
    }


}
