package kwasilewski.marketplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.helper.AdListViewAdapter;
import kwasilewski.marketplace.helper.MRKRecyclerView;
import kwasilewski.marketplace.helper.MRKSearchView;
import kwasilewski.marketplace.retrofit.listener.AdListener;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.manager.AdManager;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;

import static android.app.Activity.RESULT_OK;

public class AdFragment extends Fragment implements AdListViewAdapter.OnButtonsClickListener, AdListener, ErrorListener, MRKSearchView.TitleListener, MRKRecyclerView.RecyclerListener {

    private final List<AdMinimalData> ads = new ArrayList<>();
    private int listMode = AppConstants.MODE_NORMAL;
    private boolean inProgress;
    private AdManager adManager;
    private AdManager adButtonManager;
    private int sortingMethod = AdMinimalData.SortingMethod.NEWEST;
    private String title = "";
    private String priceMin;
    private String priceMax;
    private Long prvId = 0L;
    private Long catId = 0L;
    private Long sctId = 0L;

    private View progressBar;
    private View filterButton;
    private MRKRecyclerView recyclerView;
    private MRKSearchView searchView;
    private TextView emptyText;
    private TextView filterText;
    private TextView filterActiveText;
    private TextView sortText;

    public AdFragment() {
    }

    public static AdFragment newInstance(int listMode) {
        AdFragment fragment = new AdFragment();

        Bundle args = new Bundle();
        args.putInt(AppConstants.MODE_KEY, listMode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listMode = getArguments() != null ? getArguments().getInt(AppConstants.MODE_KEY) : AppConstants.MODE_NORMAL;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_ad, container, false);

        adManager = new AdManager(getActivity(), this);

        progressBar = view.findViewById(R.id.ad_list_progress);
        emptyText = view.findViewById(R.id.ad_list_empty);
        recyclerView = view.findViewById(R.id.ad_list_recycler);
        recyclerView.prepareRecycler(new AdListViewAdapter(ads, getActivity(), this, listMode), this);

        if (listMode == AppConstants.MODE_NORMAL) {
            prepareNormalView(view);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchBar = menu.findItem(R.id.menu_search);
        searchView = (MRKSearchView) searchBar.getActionView();
        searchView.prepareSearchView(searchBar, this);
        searchView.setQueryHint(getString(R.string.label_search));
        searchView.findViewById(R.id.search_close_btn).setOnClickListener(v -> searchBar.collapseActionView());
    }

    @Override
    public void onResume() {
        super.onResume();
        inProgress = false;
        if (listMode == AppConstants.MODE_NORMAL) {
            filterButton.setEnabled(true);
        }
        if (ads.size() == 0) {
            pullAds();
        }
    }

    @Override
    public void onPause() {
        if (adManager != null) {
            adManager.cancelCalls();
        }
        if (adButtonManager != null) {
            adButtonManager.cancelCalls();
        }
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.FILTER_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                setFilterParams(extras);
            }
        } else if (requestCode == AppConstants.REMOVABLE_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                removeAd(extras.getInt(AppConstants.AD_POS_KEY));
            }
        }
    }

    private void prepareNormalView(View view) {
        setHasOptionsMenu(true);

        View filterView = view.findViewById(R.id.ad_list_filters_bar);
        filterView.setVisibility(View.VISIBLE);
        filterText = view.findViewById(R.id.ad_list_filters_label);
        filterActiveText = view.findViewById(R.id.ad_list_filters_active);
        sortText = view.findViewById(R.id.ad_list_sort_label);
        filterButton = view.findViewById(R.id.ad_list_filters);
        filterButton.setOnClickListener(v -> {
            v.setEnabled(false);
            startActivityForResult(MRKUtil.getFilterIntent(getContext(), title, priceMin, priceMax, prvId, catId, sctId), AppConstants.FILTER_CODE);
        });

        View sortButton = view.findViewById(R.id.ad_list_sort);
        sortButton.setOnClickListener(v -> {
            if (getContext() == null) {
                return;
            }
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.inflate(R.menu.menu_sort);
            popupMenu.setOnMenuItemClickListener(item -> sortItemClicked(item.getItemId()));
            popupMenu.show();
        });
    }

    private boolean sortItemClicked(int id) {
        switch (id) {
            case R.id.menu_sort_newest:
                sort(R.string.menu_sort_default, AdMinimalData.SortingMethod.NEWEST);
                break;
            case R.id.menu_sort_cheapest:
                sort(R.string.menu_sort_cheapest, AdMinimalData.SortingMethod.CHEAPEST);
                break;
            case R.id.menu_sort_most_expensive:
                sort(R.string.menu_sort_most_expensive, AdMinimalData.SortingMethod.MOSTEXPENSIVE);
                break;
        }
        return true;
    }

    private void sort(int labelId, int sortingMethod) {
        if (this.sortingMethod != sortingMethod) {
            sortText.setText(getString(labelId));
            this.sortingMethod = sortingMethod;
            resetAdapter();
        }
    }

    private void setFilterParams(Bundle extras) {
        title = extras.getString(AppConstants.TITLE_KEY);
        priceMin = extras.getString(AppConstants.PRICE_MIN_KEY);
        priceMax = extras.getString(AppConstants.PRICE_MAX_KEY);
        catId = extras.getLong(AppConstants.CATEGORY_KEY);
        sctId = extras.getLong(AppConstants.SUBCATEGORY_KEY);
        prvId = extras.getLong(AppConstants.PROVINCE_KEY);
        resetAdapter();
        searchView.setSearchBar(title);
        setFilterLabel();
    }

    private void resetAdapter() {
        ads.clear();
        pullAds();
    }

    private void setFilterLabel() {
        long activeFilters = Stream.of(TextUtils.isEmpty(title), prvId == 0, catId == 0, TextUtils.isEmpty(priceMin), TextUtils.isEmpty(priceMax)).filter(bool -> !bool).count();
        if (activeFilters != 0) {
            filterActiveText.setText(String.format(getString(R.string.text_ad_filters), activeFilters));
            filterActiveText.setVisibility(View.VISIBLE);
            filterText.setText(null);
        } else {
            filterActiveText.setVisibility(View.GONE);
            filterText.setText(R.string.label_filters);
        }
    }

    private void showProgress(final boolean show) {
        inProgress = show;
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setEmptyListTextView() {
        if (!ads.isEmpty()) {
            emptyText.setVisibility(View.GONE);
        } else {
            emptyText.setText(getResources().getStringArray(R.array.ad_list_empty)[listMode]);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setTitle(String title) {
        if (!TextUtils.equals(this.title, title)) {
            this.title = title;
            resetAdapter();
            setFilterLabel();
        }
    }

    @Override
    public void pullAds() {
        if (inProgress) {
            return;
        }
        showProgress(true);
        switch (listMode) {
            case AppConstants.MODE_NORMAL:
                Long categoryId = sctId != 0L ? sctId : catId;
                adManager.pullAds(ads.size(), sortingMethod, title, prvId, categoryId, priceMin, priceMax, this);
                break;
            case AppConstants.MODE_ACTIVE:
                adManager.pullUserAds(recyclerView.getItemCount(), true, this);
                break;
            case AppConstants.MODE_INACTIVE:
                adManager.pullUserAds(recyclerView.getItemCount(), false, this);
                break;
            case AppConstants.MODE_FAVOURITE:
                adManager.pullFavourites(recyclerView.getItemCount(), this);
                break;
        }
    }

    @Override
    public void adsReceived(List<AdMinimalData> ads) {
        if (!ads.isEmpty()) {
            LinkedHashSet<AdMinimalData> newAds = new LinkedHashSet<>(this.ads);
            newAds.addAll(ads);
            this.ads.clear();
            this.ads.addAll(newAds);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        setEmptyListTextView();
        showProgress(false);
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        showProgress(false);
        if (ads.size() == 0) {
            startActivity(new Intent(getActivity(), NetErrorActivity.class));
        } else {
            MRKUtil.connectionProblem(getActivity());
        }
    }

    @Override
    public void viewAd(Long id, int position) {
        Intent viewIntent = new Intent(getActivity(), ViewActivity.class);
        viewIntent.putExtra(AppConstants.AD_ID_KEY, id);
        viewIntent.putExtra(AppConstants.AD_POS_KEY, position);
        viewIntent.putExtra(AppConstants.MODE_KEY, listMode == AppConstants.MODE_FAVOURITE ? AppConstants.MODE_NORMAL : listMode);
        startActivityForResult(viewIntent, AppConstants.REMOVABLE_CODE);
    }

    @Override
    public void editAd(final Long id, int position) {
        MRKUtil.startEditActivity(getActivity(), id, position);
    }

    @Override
    public void refreshAd(final Long id, final AdManager manager, final ErrorListener errorListener) {
        if (!inProgress) {
            adButtonManager = manager;
            showProgress(true);
            manager.refreshAd(id, errorListener);
        }
    }

    @Override
    public void changeAdStatus(final Long id, final AdManager manager, final ErrorListener errorListener) {
        if (!inProgress) {
            adButtonManager = manager;
            showProgress(true);
            manager.changeAdStatus(id, errorListener);
        }
    }

    @Override
    public void removeFavourite(final Long id, final AdManager manager, final ErrorListener errorListener) {
        if (!inProgress) {
            adButtonManager = manager;
            showProgress(true);
            manager.removeFavourite(id, errorListener);
        }
    }

    @Override
    public void removeAd(final int position) {
        ads.remove(position);
        recyclerView.getAdapter().notifyDataSetChanged();
        setEmptyListTextView();
        showProgress(false);
    }

    @Override
    public void adRefreshed() {
        showProgress(false);
    }

}
