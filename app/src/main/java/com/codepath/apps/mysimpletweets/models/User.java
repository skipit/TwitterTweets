package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

@Table(name = "Users")
public class User extends Model implements Serializable {

    @Column(name = "UserUid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "Name")
    private String name;
    @Column(name = "ScreenName")
    private String screenName;
    @Column(name = "ProfileImageUrl")
    private String profileImageUrl;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public User() {
        super();
    }

    public List<User> users() {
        return getMany(User.class, "User");
    }


    public static User fromJSON(JSONObject json) {
        User user = new User();
        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");

            //TODO: Temporary Location to Save
            user.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
