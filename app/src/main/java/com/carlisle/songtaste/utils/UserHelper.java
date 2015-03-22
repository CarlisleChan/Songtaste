package com.carlisle.songtaste.utils;

/**
 * Created by carlisle on 3/22/15.
 */
public class UserHelper {
    private static UserHelper userHelper;

    public static UserHelper getInstance() {
        if (userHelper == null) {
            userHelper = new UserHelper();
        }

        return userHelper;
    }

    public String getUID() {
        return "6973651";
    }
}
