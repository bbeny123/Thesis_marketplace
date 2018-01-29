package kwasilewski.marketplace.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

public class DialogItem {

    private String name;
    private Integer icon;

    public DialogItem(String name, Integer icon) {
        this.name = name;
        this.icon = icon;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

}
