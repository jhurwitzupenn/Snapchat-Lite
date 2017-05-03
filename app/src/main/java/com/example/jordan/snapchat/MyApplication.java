package com.example.jordan.snapchat;

import android.app.Application;
import com.parse.Parse;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(ApplicationConstants.APPLICATION_ID)
                .server(ApplicationConstants.SERVER_URL)
                .build());
    }
}
