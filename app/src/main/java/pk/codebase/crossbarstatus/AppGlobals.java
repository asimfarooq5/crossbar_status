package pk.codebase.crossbarstatus;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AppGlobals extends Application {

    public static Context sContext;
    public static final String KUCH_BHI_VALUE = "kuchbhi_";

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }


    public static Context getContext() {
        return sContext;
    }



    public static SharedPreferences getPreferenceManager() {
        return getContext().getSharedPreferences("shared_prefs", MODE_PRIVATE);
    }

    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }
}
