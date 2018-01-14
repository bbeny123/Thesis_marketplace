package kwasilewski.marketplace.util;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MRKUtil {

    public static void connectionProblem(final Context context) {
        toast(context, "Connection problem. Try again later", Gravity.TOP);
    }

    public static void toast(final Context context, String msg) {
        toast(context, msg, Gravity.BOTTOM);
    }

    public static void toast(final Context context, String msg, int gravity) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    public static void hideKeyboard(final AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}
