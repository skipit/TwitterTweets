package com.codepath.apps.mysimpletweets.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

@Table(name = "Medias")
public class Media extends Model implements Serializable {
    @Column(name = "MediaUid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;

    @Column(name = "PhotoUrl")
    private String photoUrl;

    public long getUid() {
        return uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public static ArrayList<Media> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Media> mediaList = new ArrayList<>();


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject mediaJson = null;
            try {
                mediaJson = jsonArray.getJSONObject(i);
                Media media = Media.fromJSON(mediaJson);

                if (media != null) {
                    mediaList.add(media);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue; // Process the rest of the tweets.
            }

        }
        return mediaList;
    }

    private static Media fromJSON(JSONObject mediaJson) {

        Media media = new Media();

        try {
            media.uid = mediaJson.getLong("id");
            media.photoUrl = mediaJson.getString("media_url");
            Log.d("DEBUG", "Photo:" + media.photoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }
}
