package kwasilewski.marketplace.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.configuration.AppConstants;
import kwasilewski.marketplace.helper.DialogItem;

public class MRKUtil {

    public static void connectionProblem(final AppCompatActivity activity) {
        toast(activity, activity.getString(R.string.error_connection_problem));
    }

    public static void toast(final AppCompatActivity activity, String msg) {
        hideKeyboard(activity);
        Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static void hideKeyboard(final AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static void showProgressBarHideView(final AppCompatActivity activity, final View viewToHide, final View progressBar, final boolean show) {
        hideKeyboard(activity);
        int shortAnimTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);
        viewToHide.setVisibility(show ? View.GONE : View.VISIBLE);
        viewToHide.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewToHide.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public static String encodePassword(String email, String password) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        String combinedPassword = email + password;
        byte[] result = messageDigest.digest(combinedPassword.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte aResult : result) {
            sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    public static boolean isPhoneValid(String phone) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phone);
    }

    public static ListAdapter getDialogAdapter(final Context context, final List<DialogItem> items) {
        return new ArrayAdapter<DialogItem>(context, android.R.layout.select_dialog_item, android.R.id.text1, items){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View option = super.getView(position, convertView, parent);
                TextView text = option.findViewById(android.R.id.text1);
                text.setText(items.get(position).getName());
                text.setCompoundDrawablesWithIntrinsicBounds(items.get(position).getIcon(), 0, 0, 0);
                text.setCompoundDrawablePadding((int) (10 * context.getResources().getDisplayMetrics().density + 1f));
                return option;
            }
        };
    }

    public static void setToolbar(final AppCompatActivity activity, android.support.v7.widget.Toolbar toolbar) {
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public static boolean checkIme(int id) {
        return id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL;
    }

    public static LinearLayout.LayoutParams getPagerLayout(final AppCompatActivity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new LinearLayout.LayoutParams(size.x,size.x);
    }

    public static Map<String, String> getAdSearchQuery(int offset, int sortingMethod, String title, Long prvId, Long catId, Long priceMin, Long priceMax) {
        Map<String, String> searchQuery = new HashMap<>();
        searchQuery.put("offset", String.valueOf(offset));
        searchQuery.put("maxResults", String.valueOf(AppConstants.MAX_RESULTS));
        searchQuery.put("sortingMethod", String.valueOf(sortingMethod));
        if (!TextUtils.isEmpty(title)) {
            searchQuery.put("title", title);
        }
        if (prvId != null) {
            searchQuery.put("prvId", String.valueOf(prvId));
        }
        if (catId != null) {
            searchQuery.put("catId", String.valueOf(catId));
        }
        if (priceMin != null) {
            searchQuery.put("priceMin", String.valueOf(priceMin));
        }
        if (priceMax != null) {
            searchQuery.put("priceMax", String.valueOf(priceMax));
        }
        return searchQuery;
    }

}
