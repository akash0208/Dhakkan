package com.shopby.dhakkan.app;

import android.app.Application;
import io.branch.referral.Branch;

public class CustomApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Branch object initialization
        Branch.getAutoInstance(this);
    }
}