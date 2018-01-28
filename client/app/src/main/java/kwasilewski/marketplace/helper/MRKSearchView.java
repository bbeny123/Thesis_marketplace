package kwasilewski.marketplace.helper;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;

public class MRKSearchView extends SearchView {

    private TitleListener titleListener;
    private MenuItem searchBar;
    private final SearchView.OnQueryTextListener queryListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (!TextUtils.isEmpty(query)) {
                MRKSearchView.super.clearFocus();
                titleListener.setTitle(query);
            } else {
                searchBar.collapseActionView();
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    public MRKSearchView(Context context) {
        super(context);
    }

    public MRKSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MRKSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void prepareSearchView(MenuItem searchBar, final TitleListener listener) {
        this.setOnQueryTextListener(queryListener);
        this.searchBar = searchBar;
        this.titleListener = listener;
        this.searchBar.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                titleListener.setTitle("");
                return true;
            }
        });
    }

    @Override
    public void setOnQueryTextListener(final OnQueryTextListener listener) {
        super.setOnQueryTextListener(listener);
        SearchAutoComplete mSearchSrcTextView = findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchSrcTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (listener != null) {
                listener.onQueryTextSubmit(getQuery().toString());
            }
            return true;
        });
    }

    public void setSearchBar(String title) {
        if (!TextUtils.isEmpty(title)) {
            searchBar.expandActionView();
            setQuery(title, false);
            clearFocus();
        }
    }

    public interface TitleListener {

        void setTitle(String query);

    }

}
