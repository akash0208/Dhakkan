package com.shopby.dhakkan.app;

import android.app.Application;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Ashiq on 4/13/2017.
 */

public class MyApplication extends Application {

    //private static final String PROPERTY_ID = "UA-62282011-4";

   // public enum TrackerName {
     //   APP_TRACKER, // Tracker used only in this app.
       // GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
       // ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    //}

    //HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseMessaging.getInstance().subscribeToTopic("notification");

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("==", "Refreshed token: " + refreshedToken);

    }

    //synchronized public Tracker getTracker(TrackerName trackerId) {
      //  if (!mTrackers.containsKey(trackerId)) {

        //    GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
          //  Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
          //          : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
          //          : analytics.newTracker(R.xml.global_tracker);
//            mTrackers.put(trackerId, t);
//
  //      }
    //    return mTrackers.get(trackerId);
    //}
}
