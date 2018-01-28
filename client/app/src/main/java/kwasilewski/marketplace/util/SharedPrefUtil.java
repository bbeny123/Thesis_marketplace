package kwasilewski.marketplace.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import kwasilewski.marketplace.dto.user.UserData;

public class SharedPrefUtil {

    private static SharedPrefUtil instance;
    private final SharedPreferences sharedPref;
    private final Gson gson = new Gson();
    private boolean tokenSet;
    private boolean userDataSet;
    private String token;
    private UserData userData;

    private SharedPrefUtil(Context context) {
        sharedPref = context.getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPrefUtil.class) {
                if (instance == null) {
                    instance = new SharedPrefUtil(context);
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

    private void removePref(String prefName) {
        SharedPreferences.Editor preferencesEditor = sharedPref.edit();
        preferencesEditor.remove(prefName);
        preferencesEditor.apply();
    }

    public String getToken() {
        if (!tokenSet) {
            setToken(getString(AppConstants.SHARED_PREF_TOKEN));
        }
        return token;
    }

    private void setToken(String token) {
        tokenSet = true;
        this.token = token;
    }

    private void saveToken(String token) {
        saveString(AppConstants.SHARED_PREF_TOKEN, token);
        setToken(token);
    }

    public UserData getUserData() {
        if (!userDataSet) {
            setUserData(gson.fromJson(getString(AppConstants.SHARED_PREF_USER), UserData.class));
        }
        return userData;
    }

    private void setUserData(UserData user) {
        userDataSet = true;
        userData = user;
    }

    public void saveUserData(UserData user) {
        saveString(AppConstants.SHARED_PREF_USER, gson.toJson(user));
        setUserData(user);
    }

    public void saveLoginData(UserData user) {
        saveUserData(user);
        saveToken(user.getToken());
    }

    public void removeUserData() {
        removePref(AppConstants.SHARED_PREF_TOKEN);
        removePref(AppConstants.SHARED_PREF_USER);
        setToken(null);
        setUserData(null);
    }

}
