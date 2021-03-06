package com.parse.starter;
/**
 * Created by Keith
 */
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Image.class);

        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this,"qCc96qf5rwxydMXeA95wweyt1L3HkdIKDF6ZQQpD","MFnf8a3skT7mTurYRDvbCSnV7kkYTtZVhqG86xkD");

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        //ParseUser.getCurrentUser().saveInBackground();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
