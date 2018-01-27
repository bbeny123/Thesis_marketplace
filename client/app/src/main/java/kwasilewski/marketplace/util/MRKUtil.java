package kwasilewski.marketplace.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import java.util.regex.Pattern;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.hint.CategoryData;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.helper.DialogItem;
import kwasilewski.marketplace.helper.HintSpinner;

public class MRKUtil {

    private static Toast toast;

    public static void connectionProblem(final Activity activity) {
        toast(activity, activity.getString(R.string.error_connection_problem));
    }

    public static void toast(final Activity activity, String msg) {
        hideKeyboard(activity);
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void hideKeyboard(final Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static void showProgressBarHideView(final Activity activity, final View viewToHide, final View progressBar, final boolean show) {
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

    public static boolean isEmailValid(Context context, String email, TextInputEditText emailEditText) {
        if (fieldEmpty(context, email, emailEditText)) {
            return false;
        } else if (!email.contains("@")) {
            emailEditText.setError(context.getString(R.string.error_invalid_email));
            return false;
        }
        return true;
    }

    public static boolean isPasswordValid(Context context, String password, TextInputEditText passwordEditText) {
        if (fieldEmpty(context, password, passwordEditText)) {
            return false;
        } else if (password.length() < 4) {
            passwordEditText.setError(context.getString(R.string.error_invalid_password));
            return false;
        }
        return true;
    }

    public static boolean fieldEmpty(Context context, String fieldText, TextInputEditText field) {
        if (TextUtils.isEmpty(fieldText)) {
            field.setError(context.getString(R.string.error_field_required));
            return true;
        }
        return false;
    }

    public static boolean spinnerEmpty(Context context, Long selectedItem, HintSpinner spinner) {
        if (selectedItem == null) {
            spinner.setError(context.getString(R.string.error_field_required));
            return true;
        }
        return false;
    }


    public static boolean isPhoneValid(Context context, String phoneText, TextInputEditText phone, boolean nullable) {
        if (nullable && TextUtils.isEmpty(phoneText)) {
            return true;
        } else if (fieldEmpty(context, phoneText, phone)) {
            return false;
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneText)) {
            phone.setError(context.getString(R.string.error_incorrect_phone));
            return false;
        }
        return true;
    }

    public static ListAdapter getDialogAdapter(final Context context, final List<DialogItem> items) {
        return new ArrayAdapter<DialogItem>(context, android.R.layout.select_dialog_item, android.R.id.text1, items) {
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
        return new LinearLayout.LayoutParams(size.x, size.x);
    }

    public static Map<String, String> getAdSearchQuery(int offset, int sortingMethod, String title, Long prvId, Long catId, String priceMin, String priceMax) {
        Map<String, String> searchQuery = getFavouriteAdSearchQuery(offset);
        searchQuery.put("sortingMethod", String.valueOf(sortingMethod));
        if (!TextUtils.isEmpty(title)) {
            searchQuery.put("title", title);
        }
        if (prvId != null && prvId != 0) {
            searchQuery.put("prvId", String.valueOf(prvId));
        }
        if (catId != null && catId != 0) {
            searchQuery.put("catId", String.valueOf(catId));
        }
        if (!TextUtils.isEmpty(priceMin)) {
            searchQuery.put("priceMin", priceMin);
        }
        if (!TextUtils.isEmpty(priceMax)) {
            searchQuery.put("priceMax", priceMax);
        }
        return searchQuery;
    }

    public static Map<String, String> getUserAdSearchQuery(int offset, boolean active) {
        Map<String, String> searchQuery = getFavouriteAdSearchQuery(offset);
        if(!active) {
            searchQuery.put("active", "false");
        }
        return searchQuery;
    }

    public static Map<String, String> getFavouriteAdSearchQuery(int offset) {
        Map<String, String> searchQuery = new HashMap<>();
        searchQuery.put("offset", String.valueOf(offset));
        searchQuery.put("maxResults", String.valueOf(AppConstants.MAX_RESULTS));
        return searchQuery;
    }


    public static TextWatcher getTextWatcherPositiveNumber() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int length = text.length();
                if (length > 0 && !Pattern.matches("^[1-9][0-9]*$", text)) {
                    s.delete(length - 1, length);
                }
            }
        };

    }

    public static boolean compareNumericStrings(String s1, String s2) {
        return !s1.isEmpty() && !s2.isEmpty() && Long.parseLong(s1) > Long.parseLong(s2);
    }

    public static void backButtonClicked(Activity activity, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            activity.finish();
        }
    }

    public static void getClickedItemId(AdapterView<?> parent, int position, HintSpinner spinner) {
        spinner.setItemId(((HintData) parent.getItemAtPosition(position)).getId());
        spinner.setError(null);
    }

    public static void getClickedItemId(AdapterView<?> parent, int position, HintSpinner spinner, Context context, HintSpinner subSpinner) {
        CategoryData item = (CategoryData) parent.getItemAtPosition(position);
        if (!item.getId().equals(spinner.getItemId())) {
            spinner.setItemId(item.getId());
            refreshSpinner(context, subSpinner, item.getSubcategories());
        }
        spinner.setError(null);
    }

    public static void enableSpinner(HintSpinner spinner, boolean enabled) {
        spinner.setEnabled(enabled);
        spinner.setClickable(enabled);
    }

    public static void setHintAdapter(Context context, HintSpinner spinner, List<? extends HintData> hintData) {
        ArrayAdapter<?> adapter = new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, hintData);
        spinner.setAdapter(adapter);
    }

    public static void refreshSpinner(Context context, HintSpinner spinner, List<HintData> hintData) {
        spinner.setText(null);
        spinner.setItemId(null);
        setHintAdapter(context, spinner, hintData);
        enableSpinner(spinner, true);
    }

}

