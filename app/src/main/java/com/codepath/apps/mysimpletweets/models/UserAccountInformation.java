package com.codepath.apps.mysimpletweets.models;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class UserAccountInformation implements Serializable {


    private String screenName;

    public String getScreenName() {
        return screenName;
    }

    public static UserAccountInformation fromJSONObject(JSONObject json) {
        UserAccountInformation info = new UserAccountInformation();

        try {
            info.screenName = json.getString("screen_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }
}
