package kwasilewski.marketplace.helper;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;

public class MRKSearchView extends SearchView {

    public MRKSearchView(Context context) {
        super(context);
    }

    public MRKSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MRKSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnQueryTextListener(final OnQueryTextListener listener) {
        System.out.println("");
        super.setOnQueryTextListener(listener);
        SearchAutoComplete mSearchSrcTextView = findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchSrcTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (listener != null) {
                    listener.onQueryTextSubmit(getQuery().toString());
                }
                return true;
            }
        });
    }

}
