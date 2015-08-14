package as.leap.sample.todolist;

import android.app.Application;

import as.leap.LeapCloud;

public class App extends Application {

    public static final String APP_ID = "Replace this with your App Id";
    public static final String API_KEY = "Replace this with your Rest Key";

    @Override
    public void onCreate() {
        super.onCreate();

        if (APP_ID.startsWith("Replace") || API_KEY.startsWith("Replace")) {
            throw new IllegalArgumentException("Please replace with your app id and api key first before" +
                    "using LeapCloud SDK.");
        }

		/*
         * Fill in this section with your LAS credentials
		 */
        LeapCloud.setLogLevel(LeapCloud.LOG_LEVEL_ERROR);
        LeapCloud.initialize(getApplicationContext(), APP_ID, API_KEY);
    }

}
