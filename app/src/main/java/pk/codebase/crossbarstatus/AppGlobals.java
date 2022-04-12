package pk.codebase.crossbarstatus;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AppGlobals extends Application {

    public static Context sContext;
    public static final String KUCH_BHI_VALUE = "kuchbhi_";
    public static final String KEY_MUTE = "mute";

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

    public static void saveDataToSharedPreferences(String key, Boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static Boolean getDataFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(key, true);
    }
}
