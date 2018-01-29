package kwasilewski.marketplace.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.activity.FilterActivity;

public class MRKUtil {

    private static Toast toast;

    public static void toast(final Activity activity, final String msg) {
        hideKeyboard(activity);
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void connectionProblem(final Activity activity) {
        toast(activity, activity.getString(R.string.error_connection_problem));
    }

    public static void showProgressBar(final Activity activity, final View viewToHide, final View progressBar, final boolean show) {
        hideKeyboard(activity);
        viewToHide.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public static void hideKeyboard(final Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static void setToolbar(final AppCompatActivity activity, final android.support.v7.widget.Toolbar toolbar) {
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public static void backButtonClicked(final Activity activity, final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            activity.finish();
        }
    }

    public static boolean checkIme(final int id) {
        return id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL;
    }

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean compareNumericStrings(final String s1, final String s2) {
        return !s1.isEmpty() && !s2.isEmpty() && Long.parseLong(s1) > Long.parseLong(s2);
    }

    public static String encodePassword(final String email, final String password) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte aResult : messageDigest.digest((email + password).getBytes())) {
            sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static Intent getFilterIntent(final Context context, final String title, final String priceMin, final String priceMax, final Long prvId, final Long catId, final Long sctId) {
        Intent intent = new Intent(context, FilterActivity.class);
        intent.putExtra(AppConstants.TITLE_KEY, title);
        intent.putExtra(AppConstants.PRICE_MIN_KEY, priceMin);
        intent.putExtra(AppConstants.PRICE_MAX_KEY, priceMax);
        intent.putExtra(AppConstants.PROVINCE_KEY, prvId);
        intent.putExtra(AppConstants.CATEGORY_KEY, catId);
        intent.putExtra(AppConstants.SUBCATEGORY_KEY, sctId);
        return intent;
    }

}

