package com.example.changelanguage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LanguageManager {

    private static final String PREFS_NAME = "LanguagePrefs";
    private static final String LANGUAGE_KEY = "selected_language";

    public static void setLocale(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Save to SharedPreferences
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).edit();
        editor.putString(LANGUAGE_KEY, langCode);
        editor.apply();
    }

    public static void loadLocale(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        String language = prefs.getString(LANGUAGE_KEY, "en");
        setLocale(activity, language);
    }

    public static String getCurrentLanguage(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getString(LANGUAGE_KEY, "en");
    }
}
