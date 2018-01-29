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

    public static boolean spinnerEmpty(final Context context, Long value, HintSpinner spinner) {
        if (value == null) {
            spinner.setError(context.getString(R.string.error_field_required));
            return true;
        }
        return false;
    }

    public static boolean fieldEmpty(final Context context, String text, TextInputEditText field) {
        if (TextUtils.isEmpty(text)) {
            field.setError(context.getString(R.string.error_field_required));
            return true;
        }
        return false;
    }

    public static boolean emailValid(final Context context, String email, TextInputEditText emailField) {
        if (fieldEmpty(context, email, emailField)) {
            return false;
        } else if (!email.contains("@")) {
            emailField.setError(context.getString(R.string.error_email_invalid));
            return false;
        }
        return true;
    }

    public static boolean passwordValid(final Context context, String password, TextInputEditText passwordField) {
        if (fieldEmpty(context, password, passwordField)) {
            return false;
        } else if (password.length() < 4) {
            passwordField.setError(context.getString(R.string.error_password_short));
            return false;
        }
        return true;
    }

    public static boolean phoneValid(final Context context, String phone, TextInputEditText phoneField, boolean nullable) {
        if (TextUtils.isEmpty(phone)) {
            return nullable || !fieldEmpty(context, phone, phoneField);
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            phoneField.setError(context.getString(R.string.error_phone_invalid));
            return false;
        }
        return true;
    }

    public static TextWatcher positiveNumber() {
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
