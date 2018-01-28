package kwasilewski.marketplace.helper;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

public class HintSpinner extends AppCompatAutoCompleteTextView {

    private boolean isPopup;
    private Long itemId;

    public HintSpinner(Context context) {
        super(context);
    }

    public HintSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public HintSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
            setKeyListener(null);
            dismissDropDown();
        } else {
            isPopup = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!super.isEnabled()) return false;
        performClick();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (isPopup) {
                    dismissDropDown();
                } else {
                    requestFocus();
                    showDropDown();
                }
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.isEnabled() && super.performClick();
    }

    @Override
    public void showDropDown() {
        super.showDropDown();
        isPopup = true;
    }

    @Override
    public void dismissDropDown() {
        super.dismissDropDown();
        isPopup = false;
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), android.R.drawable.arrow_down_float);
        if (dropdownIcon != null) {
            right = dropdownIcon;
            right.mutate().setAlpha(66);
        }
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);

    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
