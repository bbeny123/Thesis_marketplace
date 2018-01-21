package kwasilewski.marketplace.fragment;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.activity.FilterActivity;
import kwasilewski.marketplace.activity.NetErrorActivity;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.dto.ad.AdSearchData;
import kwasilewski.marketplace.helper.AdListViewAdapter;
import kwasilewski.marketplace.helper.MRKSearchView;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.AdService;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AdFragment extends Fragment implements AdListViewAdapter.OnButtonsClickListener {

    private static final int FILTER_ACTIVITY_CODE = 1;
    private static final int REFRESH_ACTION = 1;
    private static final int STATUS_ACTION = 2;
    private static final int FAVOURITE_ACTION = 3;
    private static final String LIST_MODE = "mode";
    private int listMode = ListModes.NORMAL_MODE;

    //filter params
    private int sortingMethod = AdSearchData.SortingMethod.NEWEST;
    private String title = "";
    private Long prvId = 0L;
    private Long catId = 0L;
    private Long sctId = 0L;
    private String priceMin;
    private String priceMax;
    private String token;

    private final List<AdMinimalData> ads = new ArrayList<>();
    private final AdService adService = RetrofitService.getInstance().getAdService();
    private Call<List<AdMinimalData>> callAds;
    private final Callback<List<AdMinimalData>> callbackAds = new Callback<List<AdMinimalData>>() {
        @Override
        public void onResponse(Call<List<AdMinimalData>> call, Response<List<AdMinimalData>> response) {
            if (response.isSuccessful()) {
                addAdsToAdapter(response.body());
            } else {
                connectionProblem();
            }
        }

        @Override
        public void onFailure(Call<List<AdMinimalData>> call, Throwable t) {
            if (!call.isCanceled() && ads.size() == 0) {
                connectionProblemAtStart();
            } else {
                connectionProblem();
            }
        }
    };
    private Call<ResponseBody> callAd;
    private boolean callActive = false;

    //listeners - init code at the bottom (except recycler)
    private final RecyclerView.OnScrollListener listenerRecycler = new RecyclerView.OnScrollListener() {
        private final int threshold = 2;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && adapter.getLastItemPosition() >= adapter.getItemCount() - threshold) {
                pullAds();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && adapter.getLastItemPosition() >= adapter.getItemCount() - threshold) {
                pullAds();
            }
        }
    };
    private View.OnClickListener listenerFilter;
    private View.OnClickListener listenerSort;
    private PopupMenu.OnMenuItemClickListener listenerPopupMenu;
    private SearchView.OnQueryTextListener listenerSearchQuery;
    private View.OnClickListener listenerSearchClear;
    private MenuItemCompat.OnActionExpandListener listenerSearchClose;

    private View progressBar;
    private View filterButton;
    private RecyclerView recyclerView;
    private AdListViewAdapter adapter;
    private TextView emptyListTextView;
    private TextView filterLabel;
    private TextView filterActiveLabel;
    private TextView sortLabel;
    private MenuItem searchBar;
    private MRKSearchView searchView;

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
        if (getArguments() != null) {
            listMode = getArguments().getInt(LIST_MODE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ad_list, container, false);

        progressBar = view.findViewById(R.id.ad_list_progress);
        emptyListTextView = view.findViewById(R.id.ad_list_empty);
        recyclerView = view.findViewById(R.id.ad_list_recycler);
        adapter = new AdListViewAdapter(ads, getContext(), this, listMode);
        setRecyclerAdapter();

        if (listMode == ListModes.NORMAL_MODE) {
            setNormalModeListeners();
            setHasOptionsMenu(true);
            View filterView = view.findViewById(R.id.ad_list_filters_bar);
            filterView.setVisibility(View.VISIBLE);
            filterLabel = view.findViewById(R.id.ad_list_filters_label);
            filterActiveLabel = view.findViewById(R.id.ad_list_filters_active);
            sortLabel = view.findViewById(R.id.ad_list_sort_label);
            filterButton = view.findViewById(R.id.ad_list_filters);
            filterButton.setOnClickListener(listenerFilter);
            View sortByButton = view.findViewById(R.id.ad_list_sort);
            sortByButton.setOnClickListener(listenerSort);
        } else {
            token = SharedPrefUtil.getInstance(getContext()).getToken();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (listMode != ListModes.NORMAL_MODE) {
            return;
        }
        menu.clear();
        inflater.inflate(R.menu.menu_with_searchbar, menu);
        searchBar = menu.findItem(R.id.action_search);
        searchView = (MRKSearchView) searchBar.getActionView();
        searchView.setOnQueryTextListener(listenerSearchQuery);
        searchView.setQueryHint(getString(R.string.label_search));
        searchView.findViewById(R.id.search_close_btn).setOnClickListener(listenerSearchClear);
        MenuItemCompat.setOnActionExpandListener(searchBar, listenerSearchClose);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_ACTIVITY_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                setFilterParams(extras);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listMode == ListModes.NORMAL_MODE) {
            filterButton.setEnabled(true);
        }
        if(ads.size() == 0) {
            pullAds();
        }
    }

    @Override
    public void onPause() {
        if (callAds != null) {
            callAds.cancel();
        }
        if (callAd != null) {
            callAd.cancel();
        }
        super.onPause();
    }

    private void setFilterParams(Bundle extras) {
        title = extras.getString(FilterActivity.TITLE_KEY);
        priceMin = extras.getString(FilterActivity.PRICE_FROM_KEY);
        priceMax = extras.getString(FilterActivity.PRICE_TO_KEY);
        catId = extras.getLong(FilterActivity.CATEGORY_KEY);
        sctId = extras.getLong(FilterActivity.SUBCATEGORY_KEY);
        prvId = extras.getLong(FilterActivity.PROVINCE_KEY);
        resetAdapter();
        setSearchBar();
        setFilterLabel();
    }

    private void setSearchBar() {
        if (!TextUtils.isEmpty(title)) {
            searchBar.expandActionView();
            searchView.setQuery(title, false);
            searchView.clearFocus();
        }
    }

    private void setFilterLabel() {
        int activeFilters = 0;
        activeFilters += TextUtils.isEmpty(title) ? 0 : 1;
        activeFilters += prvId == 0 ? 0 : 1;
        activeFilters += catId == 0 ? 0 : 1;
        activeFilters += TextUtils.isEmpty(priceMin) ? 0 : 1;
        activeFilters += TextUtils.isEmpty(priceMax) ? 0 : 1;

        if (activeFilters != 0) {
            filterActiveLabel.setText(String.format(getString(R.string.label_filters_active), activeFilters));
            filterLabel.setText(null);
            filterActiveLabel.setVisibility(View.VISIBLE);
        } else {
            filterActiveLabel.setVisibility(View.GONE);
            filterLabel.setText(R.string.label_filters);
        }
    }

    private void setRecyclerAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(listenerRecycler);
    }

    private void resetAdapter() {
        ads.clear();
        pullAds();
    }

    private void pullAds() {
        if (callActive) {
            return;
        }
        callActive = true;
        showProgress(true);
        setAdsCall();
        callAds.enqueue(callbackAds);
    }

    private void setAdsCall() {
        if(listMode == ListModes.NORMAL_MODE) {
            Long categoryId = sctId != 0L ? sctId : catId;
            callAds = adService.getAds(MRKUtil.getAdSearchQuery(adapter.getItemCount(), sortingMethod, title, prvId, categoryId, priceMin, priceMax));
        } else if (listMode == ListModes.ACTIVE_MODE) {
            callAds = adService.getUserAds(token, MRKUtil.getUserAdSearchQuery(adapter.getItemCount(), true));
        } else if (listMode == ListModes.INACTIVE_MODE) {
            callAds = adService.getUserAds(token, MRKUtil.getUserAdSearchQuery(adapter.getItemCount(), false));
        } else if (listMode == ListModes.FAVOURITE_MODE) {
            callAds = adService.getUserFavourites(token, MRKUtil.getFavouriteAdSearchQuery(adapter.getItemCount()));
        }
    }

    private void addAdsToAdapter(List<AdMinimalData> newAds) {
        if (!newAds.isEmpty()) {
            ads.addAll(newAds);
            adapter.notifyDataSetChanged();
        }
        if (!ads.isEmpty()) {
            emptyListTextView.setVisibility(View.GONE);
        } else {
            emptyListTextView.setText(getResources().getStringArray(R.array.ad_list_empty)[listMode-1]);
            emptyListTextView.setVisibility(View.VISIBLE);
        }
        endOfCall();
    }

    private void showProgress(final boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void connectionProblem() {
        showProgress(false);
        MRKUtil.connectionProblem(getActivity());
    }

    private void connectionProblemAtStart() {
        startActivity(new Intent(getActivity(), NetErrorActivity.class));
    }

    private void setNormalModeListeners() {

        listenerFilter = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                Intent filterIntent = new Intent(getContext(), FilterActivity.class);
                filterIntent.putExtra(FilterActivity.TITLE_KEY, title);
                filterIntent.putExtra(FilterActivity.PRICE_FROM_KEY, priceMin);
                filterIntent.putExtra(FilterActivity.PRICE_TO_KEY, priceMax);
                filterIntent.putExtra(FilterActivity.CATEGORY_KEY, catId);
                filterIntent.putExtra(FilterActivity.SUBCATEGORY_KEY, sctId);
                filterIntent.putExtra(FilterActivity.PROVINCE_KEY, prvId);
                startActivityForResult(filterIntent, FILTER_ACTIVITY_CODE);
            }
        };

        listenerSort = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() == null) {
                    return;
                }
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.ads_sort_menu);
                popupMenu.setOnMenuItemClickListener(listenerPopupMenu);
                popupMenu.show();
            }
        };

        listenerPopupMenu = new PopupMenu.OnMenuItemClickListener() {
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
        };

        listenerSearchQuery = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchView.clearFocus();
                    setTitle(query);
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

        listenerSearchClear = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.collapseActionView();
            }
        };

        //searchView.setOnCloseListener is bugged since 4.1, deprecated methods are only way to do that
        listenerSearchClose = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setTitle("");
                return true;
            }
        };

    }

    private void sortingMenuItemClicked(int labelId, int sortingMethod) {
        if (this.sortingMethod != sortingMethod) {
            sortLabel.setText(getString(labelId));
            this.sortingMethod = sortingMethod;
            resetAdapter();
        }
    }

    private void setTitle(String title) {
        if (!TextUtils.equals(this.title, title)) {
            this.title = title;
            resetAdapter();
            setFilterLabel();
        }
    }

    @Override
    public void editAd(final Long id) {
        //go to editActivity
    }

    @Override
    public void refreshAd(final Long id, final Button button) {
        changeAd(id, REFRESH_ACTION, 0, button);
    }

    @Override
    public void changeAdStatus(final Long id, final int position) {
        changeAd(id, STATUS_ACTION, position, null);
    }

    @Override
    public void removeFavourite(final Long id, final int position) {
        changeAd(id, FAVOURITE_ACTION, position, null);
    }

    public interface ListModes {
        int NORMAL_MODE = 1;
        int ACTIVE_MODE = 2;
        int INACTIVE_MODE = 3;
        int FAVOURITE_MODE = 4;
    }

    private void changeAd(Long id, final int action, final int position, final Button button) {
        if (callActive) {
            return;
        }
        callActive = true;
        showProgress(true);
        setAdCall(action, id);
        callAd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    changeSuccess(action, position, button);
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    connectionProblem();
                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    adNotFound(position);
                } else if (response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                    favouriteNotExists(position);
                } else {
                    connectionProblem();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!call.isCanceled()) connectionProblem();
            }
        });
    }

    private void setAdCall(int action, Long id) {
        if (action == REFRESH_ACTION) {
            callAd = adService.refreshAd(token, id);
        } else if (action == STATUS_ACTION) {
            callAd = adService.changeUserAdStatus(token, id);
        } else if (action == FAVOURITE_ACTION) {
            callAd = adService.removeFavourite(token, id);
        }
    }

    private void removeAdFromAdapter(int position) {
        ads.remove(position);
        adapter.notifyDataSetChanged();
        endOfCall();
    }

    private void changeSuccess(int action, int position, Button button) {
        if (action == REFRESH_ACTION) {
            MRKUtil.toast(getActivity(), getString(R.string.toast_ad_refreshed));
            button.setEnabled(false);
            endOfCall();
        } else {
            if (action == STATUS_ACTION) {
                if (listMode == ListModes.ACTIVE_MODE) {
                    MRKUtil.toast(getActivity(), getString(R.string.toast_ad_deactivated));
                } else {
                    MRKUtil.toast(getActivity(), getString(R.string.toast_ad_activated));
                }
            } else if (action == FAVOURITE_ACTION) {
                MRKUtil.toast(getActivity(), getString(R.string.toast_removed_favourite));
            }
            removeAdFromAdapter(position);
        }
    }

    private void endOfCall() {
        showProgress(false);
        callActive = false;
    }

    private void adNotFound(int position) {
        removeAdFromAdapter(position);
        MRKUtil.toast(getActivity(), getString(R.string.toast_ad_not_exist));
    }

    private void favouriteNotExists(int position) {
        removeAdFromAdapter(position);
        MRKUtil.toast(getActivity(), getString(R.string.toast_not_favourite));
    }

}
