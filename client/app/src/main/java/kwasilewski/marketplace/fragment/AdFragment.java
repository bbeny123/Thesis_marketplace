package kwasilewski.marketplace.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.dto.ad.AdSearchData;
import kwasilewski.marketplace.helper.AdListViewAdapter;
import kwasilewski.marketplace.helper.MRKSearchView;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.util.MRKUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String LIST_MODE = "mode";
    private int listMode;

    private int sortingMethod = AdSearchData.SortingMethod.NEWEST;
    private String title = "avs";
    private Long prvId = 0L;
    private Long catId = 0L;
    private Long priceMin = 0L;
    private Long priceMax = 0L;

    private List<AdMinimalData> ads = new ArrayList<>();

    private MenuItem searchBar;
    private MRKSearchView searchView;
    private RecyclerView recyclerView;
    private AdListViewAdapter adapter;
    private TextView filterLabel;
    private TextView filterActiveLabel;
    private TextView sortLabel;

    public AdFragment() {
    }

    public static AdFragment newInstance(int listMode) {
        AdFragment fragment = new AdFragment();

        Bundle args = new Bundle();
        args.putInt(LIST_MODE, listMode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            listMode = getArguments().getInt(LIST_MODE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ad_list, container, false);

        recyclerView = view.findViewById(R.id.ad_list_recycler);
        adapter = new AdListViewAdapter(ads, getContext());
        setAdapter();

        filterLabel = view.findViewById(R.id.ad_list_filters_label);
        filterActiveLabel = view.findViewById(R.id.ad_list_filters_active);
        sortLabel = view.findViewById(R.id.ad_list_sort_label);
        View sortByButton = view.findViewById(R.id.ad_list_sort);
        sortByButton.setOnClickListener(listenerSort);

        getAds();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_with_searchbar, menu);
        searchBar = menu.findItem(R.id.action_search);
        searchView = (MRKSearchView) searchBar.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint(getString(R.string.label_search));
        searchView.findViewById(R.id.search_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.collapseActionView();
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchBar, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                titleSet("");
                return true;
            }
        });
    }

    private void setAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int threshold = 2;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && adapter.getLastItemPosition() >= adapter.getItemCount() - threshold) {
                    getAds();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && adapter.getLastItemPosition() >= adapter.getItemCount() - threshold) {
                    getAds();
                }
            }
        });
    }

    public Map<String, String> getSearchQuery() {
        return MRKUtil.getAdSearchQuery(adapter.getItemCount(), sortingMethod, title, null, null, null, null);
    }

    private void getAds() {
        Call<List<AdMinimalData>> callAd = RetrofitService.getInstance().getAdService().getAds(getSearchQuery());
        callAd.enqueue(new Callback<List<AdMinimalData>>() {
            @Override
            public void onResponse(Call<List<AdMinimalData>> call, Response<List<AdMinimalData>> response) {
                if (response.isSuccessful()) {
                    addAds(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AdMinimalData>> call, Throwable t) {
            //    if (!call.isCanceled()) connectionProblemAtStart();
            }
        });
    }

    private void addAds(List<AdMinimalData> newAds) {
        if (!newAds.isEmpty()) {
            ads.addAll(newAds);
            adapter.notifyDataSetChanged();
        }
    }

    private void resetAdapter() {
        ads.clear();
        getAds();
    }

    private void titleSet(String title) {
        if (!TextUtils.equals(this.title, title)) {
            this.title = title;
            resetAdapter();
            setFilterLabel();
        }
    }

    private void setFilterLabel() {
        int activeFilters = 0;
        activeFilters += TextUtils.isEmpty(title) ? 0 : 1;
        activeFilters += prvId == 0 ? 0 : 1;
        activeFilters += catId == 0 ? 0 : 1;
        activeFilters += priceMin == 0 ? 0 : 1;
        activeFilters += priceMax == 0 ? 0 : 1;

        if (activeFilters != 0) {
            filterActiveLabel.setText(String.format(getString(R.string.label_filters_active), activeFilters));
            filterLabel.setText(null);
            filterActiveLabel.setVisibility(View.VISIBLE);
        } else {
            filterActiveLabel.setVisibility(View.GONE);
            filterLabel.setText(R.string.label_filters);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        if(!TextUtils.isEmpty(text)) {
            searchView.clearFocus();
            titleSet(text);
        } else {
            searchBar.collapseActionView();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        return false;
    }

    private View.OnClickListener listenerSort = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getContext() == null) {
                return;
            }
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.inflate(R.menu.ads_sort_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.sort_newest) {
                        sortingMenuItemClicked(R.string.label_sort_default, AdSearchData.SortingMethod.NEWEST);
                    } else if (item.getItemId() == R.id.sort_cheapest) {
                        sortingMenuItemClicked(R.string.label_sort_cheapest, AdSearchData.SortingMethod.CHEAPEST);
                    } else if (item.getItemId() == R.id.sort_most_expensive) {
                        sortingMenuItemClicked(R.string.label_sort_most_expensive, AdSearchData.SortingMethod.MOSTEXPENSIVE);
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    };

    private void sortingMenuItemClicked(int labelId, int sortingMethod) {
        if(this.sortingMethod != sortingMethod) {
            sortLabel.setText(getString(labelId));
            this.sortingMethod = sortingMethod;
            resetAdapter();
        }
    }

    public interface ListModes {
        int NORMAL_MODE = 1;
        int ACITVE_MODE = 2;
        int INACTIVE_MODE = 3;
        int FAVOURITE_MODE = 4;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Long id);
    }

}
