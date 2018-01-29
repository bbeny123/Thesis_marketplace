package kwasilewski.marketplace.helper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class MRKRecyclerView extends RecyclerView {

    private AdListViewAdapter adapter;

    public MRKRecyclerView(Context context) {
        super(context);
    }

    public MRKRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MRKRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public AdListViewAdapter getAdapter() {
        return adapter;
    }

    public int getItemCount() {
        return adapter.getItemCount();
    }

    public void prepareRecycler(AdListViewAdapter adAdapter, RecyclerListener listener) {
        adapter = adAdapter;
        super.setHasFixedSize(true);
        super.setAdapter(adapter);
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        super.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int threshold = 3;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findLastVisibleItemPosition() >= adapter.getItemCount() - threshold) {
                    listener.pullAds();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && layoutManager.findLastVisibleItemPosition() >= adapter.getItemCount() - threshold) {
                    listener.pullAds();
                }
            }
        });
    }

    public interface RecyclerListener {

        void pullAds();

    }

}
