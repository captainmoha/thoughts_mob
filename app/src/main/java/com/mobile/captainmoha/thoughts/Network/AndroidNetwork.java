package com.mobile.captainmoha.thoughts.Network;

/**
 * Created by captainmoha on 3/15/18.
 */


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



/**
 * Created by captainmoha on 8/6/16.
 */
public class AndroidNetwork {

    private Activity activity;

    public AndroidNetwork(Activity a) {
        activity = a;
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
