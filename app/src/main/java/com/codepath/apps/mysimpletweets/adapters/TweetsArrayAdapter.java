package com.codepath.apps.mysimpletweets.adapters;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

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
    }

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    public TweetsArrayAdapter(Context context, int resource, List<Tweet> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);

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
            viewHolder.tvFavCount.setText(tweet.getFavoriteCount());
        } else {
            viewHolder.tvFavCount.setText("0");
        }
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .into(viewHolder.ivProfileImage);


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
