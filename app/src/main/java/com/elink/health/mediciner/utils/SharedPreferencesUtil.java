package com.elink.health.mediciner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * Created by TT on 2018-01-10.
 */

public class SharedPreferencesUtil {

    private static final String APP_SHARED_FILENAME = "Mediciner";

    public static synchronized void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor edit = context.getSharedPreferences(APP_SHARED_FILENAME,
                Context.MODE_PRIVATE).edit();
        edit.putBoolean(key, value);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit);
    }

    public static boolean getBoolean(Context context, String key) {
        return context.getSharedPreferences(APP_SHARED_FILENAME,
                Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public static synchronized void putString(Context context, String key, String value)
            throws Exception {
        SharedPreferences.Editor edit = context.getSharedPreferences(APP_SHARED_FILENAME,
                Context.MODE_PRIVATE).edit();
        String encode = SecurityUtil.encrypt(value);
        edit.putString(key, encode);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit);
    }

    public static String getString(Context context, String key) throws Exception {
        String value = context.getSharedPreferences(APP_SHARED_FILENAME,
                Context.MODE_PRIVATE).getString(key, null);
        return SecurityUtil.decrypt(value);
    }

    public static synchronized void putInt(Context context, String key, int value)
            throws Exception {
        SharedPreferences.Editor edit = context.getSharedPreferences(APP_SHARED_FILENAME,
                Context.MODE_PRIVATE).edit();
        String encode = SecurityUtil.encrypt(Integer.toString(value));
        edit.putString(key, encode);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit);
    }

    public static int getInt(Context context, String key) throws Exception {
        String value = context.getSharedPreferences(APP_SHARED_FILENAME,
                Context.MODE_PRIVATE).getString(key, null);
        int i = 0;
        if (value != null) {
            String decode = SecurityUtil.decrypt(value);
            i = Integer.parseInt(decode);
        }
        return i;
    }


}
