package com.maxleap.todo;

import android.app.Application;
import com.maxleap.MLObject;
import com.maxleap.MaxLeap;

public class App extends Application {

    public static final String APP_ID = "Replace this with your App Id";
    public static final String API_KEY = "Replace this with your Rest Key";

    @Override
    public void onCreate() {
        super.onCreate();

        if (APP_ID.startsWith("Replace") || API_KEY.startsWith("Replace")) {
            throw new IllegalArgumentException("Please replace with your app id and api key first before" +
                    "using MaxLeap SDK.");
        }
        /*
         * Fill in this section with your MaxLeap credentials
		 */
        MaxLeap.setLogLevel(MaxLeap.LOG_LEVEL_VERBOSE);
        MaxLeap.initialize(this, APP_ID, API_KEY, MaxLeap.REGION_CN);

        MLObject.registerSubclass(TodoList.class);
        MLObject.registerSubclass(TodoItem.class);
    }

}
