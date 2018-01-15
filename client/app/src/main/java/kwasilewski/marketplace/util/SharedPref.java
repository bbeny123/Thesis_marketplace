package kwasilewski.marketplace.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import kwasilewski.marketplace.configuration.AppConstants;
import kwasilewski.marketplace.dto.user.UserData;

public class SharedPref {

    private static SharedPref instance;
    private SharedPreferences sharedPref;
    private Gson gson;
    private String token;
    private UserData userData;

    private SharedPref(Context context) {
        sharedPref = context.getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static SharedPref getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPref.class) {
                if (instance == null) {
                    instance = new SharedPref(context);
                }
            }
        }
        return instance;
    }

    private String getString(String prefName) {
        return sharedPref.getString(prefName, null);
    }

    private void saveString(String prefName, String data) {
        SharedPreferences.Editor preferencesEditor = sharedPref.edit();
        preferencesEditor.putString(prefName, data);
        preferencesEditor.apply();
    }

    public String getToken() {
        if (token == null) {
            token = getString(AppConstants.SHARED_PREF_TOKEN);
        }
        return token;
    }

    public void saveToken(String token) {
        saveString(AppConstants.SHARED_PREF_TOKEN, token);
        this.token = token;
    }

    public UserData getUserData() {
        if (userData == null) {
            userData = gson.fromJson(getString(AppConstants.SHARED_PREF_USER), UserData.class);
        }
        return userData;
    }

    public void saveUserData(UserData user) {
        saveString(AppConstants.SHARED_PREF_USER, gson.toJson(user));
        userData = user;
    }


}