package kwasilewski.marketplace.util;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import kwasilewski.marketplace.dto.hint.CategoryData;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.helper.HintSpinner;

public class SpinnerUtil {

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

    private static void refreshSpinner(Context context, HintSpinner spinner, List<HintData> hintData) {
        spinner.setText(null);
        spinner.setItemId(null);
        setHintAdapter(context, spinner, hintData);
        enableSpinner(spinner, true);
    }

    public static void setSpinnerAdapterAndName(Context context, HintSpinner spinner, List<? extends HintData> hintData) {
        if (hintData != null) {
            hintData.stream().filter(hint -> hint.getId().equals(spinner.getItemId())).findAny().ifPresent(hint -> spinner.setText(hint.getName()));
        }
        setHintAdapter(context, spinner, hintData);
    }

}
