package kwasilewski.marketplace.util;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import java.util.regex.Pattern;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.helper.HintSpinner;

public class ValidUtil {

    public static boolean spinnerEmpty(Context context, Long selectedItem, HintSpinner spinner) {
        if (selectedItem == null) {
            spinner.setError(context.getString(R.string.error_field_required));
            return true;
        }
        return false;
    }

    public static boolean fieldEmpty(Context context, String fieldText, TextInputEditText field) {
        if (TextUtils.isEmpty(fieldText)) {
            field.setError(context.getString(R.string.error_field_required));
            return true;
        }
        return false;
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

}
