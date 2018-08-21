package com.rashmi.rrp.chitchat;

import android.app.Application;
import android.content.Context;

public class GetTimeAgo extends Application {

    private static final int SECONDS_MILIS = 1000;
    private static final int MINUTE_MILIS = 60 * SECONDS_MILIS;
    private static final int HOUR_MILIS = 60 * MINUTE_MILIS;
    private static final int DAY_MILIS = 24 * HOUR_MILIS;

    public static String getTimeAgo(long time, Context ctx) {
        if(time < 1000000000000L) {
            // if timestamp given in seconds, convert to milis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if(time > now || time <= 0) {

            return null;

        }

        //TODO: localize
        final long diff = now - time;
        if(diff < MINUTE_MILIS) {
            return "just now";
        } else if(diff < 2 * MINUTE_MILIS) {
            return "a minute ago";
        } else if(diff < 50 * MINUTE_MILIS) {
            return diff / MINUTE_MILIS + " minutes ago";
        } else if(diff < 90 * MINUTE_MILIS) {
            return "an hour ago";
        } else if(diff < 24 * HOUR_MILIS) {
            return diff / HOUR_MILIS + " hours ago";
        } else if(diff < 48 * HOUR_MILIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILIS + " days ago";
        }
    }

}




















