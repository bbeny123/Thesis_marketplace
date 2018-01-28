package kwasilewski.marketplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.helper.AdListViewAdapter;
import kwasilewski.marketplace.helper.MRKSearchView;
import kwasilewski.marketplace.retrofit.listener.AdListener;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.manager.AdManager;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;

import static android.app.Activity.RESULT_OK;

public class AdFragment extends Fragment implements AdListViewAdapter.OnButtonsClickListener, AdListener, ErrorListener, MRKSearchView.TitleListener {

    private static final int FILTER_ACTIVITY_CODE = 1;
    private static final int REMOVABLE_CODE = 2;
    private static final int REFRESH_ACTION = 1;
    private static final int STATUS_ACTION = 2;
    private static final int FAVOURITE_ACTION = 3;
    private final List<AdMinimalData> ads = new ArrayList<>();

    private AdManager adManager;
    private int listMode = ListModes.NORMAL_MODE;
    //filter params
    private int sortingMethod = SortingMethod.NEWEST;
    private String title = "";
    private Long prvId = 0L;
    private Long catId = 0L;
    private Long sctId = 0L;
    private String priceMin;
    private String priceMax;
    private String token;

    private boolean inProgress = false;

    private View progressBar;
    private View filterButton;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
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
        args.putInt(AppConstants.VIEW_MODE, listMode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listMode = getArguments().getInt(AppConstants.VIEW_MODE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_ad, container, false);

        progressBar = view.findViewById(R.id.ad_list_progress);
        emptyListTextView = view.findViewById(R.id.ad_list_empty);
        recyclerView = view.findViewById(R.id.ad_list_recycler);
        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        adapter = new AdListViewAdapter(ads, getContext(), this, listMode);
        setRecyclerAdapter();

        adManager = new AdManager(getActivity(), this);
        if (listMode == ListModes.NORMAL_MODE) {
            setHasOptionsMenu(true);
            View filterView = view.findViewById(R.id.ad_list_filters_bar);
            filterView.setVisibility(View.VISIBLE);
            filterLabel = view.findViewById(R.id.ad_list_filters_label);
            filterActiveLabel = view.findViewById(R.id.ad_list_filters_active);
            sortLabel = view.findViewById(R.id.ad_list_sort_label);
            filterButton = view.findViewById(R.id.ad_list_filters);
            filterButton.setOnClickListener(v -> {
                v.setEnabled(false);
                Intent filterIntent = MRKUtil.getFilterIntent(getContext(), title, priceMin, priceMax, prvId, catId, sctId);
                startActivityForResult(filterIntent, FILTER_ACTIVITY_CODE);
            });
            View sortByButton = view.findViewById(R.id.ad_list_sort);
            sortByButton.setOnClickListener(v -> {
                if (getContext() == null) {
                    return;
                }
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.menu_sort);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.sort_newest) {
                        sortingMenuItemClicked(R.string.label_sort_default, SortingMethod.NEWEST);
                    } else if (item.getItemId() == R.id.sort_cheapest) {
                        sortingMenuItemClicked(R.string.label_sort_cheapest, SortingMethod.CHEAPEST);
                    } else if (item.getItemId() == R.id.sort_most_expensive) {
                        sortingMenuItemClicked(R.string.label_sort_most_expensive, SortingMethod.MOSTEXPENSIVE);
                    }
                    return true;
                });
                popupMenu.show();
            });
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
        inflater.inflate(R.menu.menu_search, menu);
        searchBar = menu.findItem(R.id.action_search);
        searchView = (MRKSearchView) searchBar.getActionView();
        searchView.prepareSearchView(searchBar, this);
        searchView.setQueryHint(getString(R.string.label_search));
        searchView.findViewById(R.id.search_close_btn).setOnClickListener(v -> searchBar.collapseActionView());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_ACTIVITY_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                setFilterParams(extras);
            }
        } else if (requestCode == REMOVABLE_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                //       removeAdFromAdapter(extras.getInt(AppConstants.AD_POSITION));
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
        adManager.cancelCalls();
        super.onPause();
    }

    private void setFilterParams(Bundle extras) {
        title = extras.getString(AppConstants.TITLE_KEY);
        priceMin = extras.getString(AppConstants.PRICE_FROM_KEY);
        priceMax = extras.getString(AppConstants.PRICE_TO_KEY);
        catId = extras.getLong(AppConstants.CATEGORY_KEY);
        sctId = extras.getLong(AppConstants.SUBCATEGORY_KEY);
        prvId = extras.getLong(AppConstants.PROVINCE_KEY);
        resetAdapter();
        setSearchBar();
        setFilterLabel();
    }

    private void resetAdapter() {
        ads.clear();
        pullAds();
    }

    private void setSearchBar() {
        if (!TextUtils.isEmpty(title)) {
            searchBar.expandActionView();
            searchView.setQuery(title, false);
            searchView.clearFocus();
        }
    }

    private void setFilterLabel() {
        long activeFilters = Stream.of(TextUtils.isEmpty(title), prvId == 0, catId == 0, TextUtils.isEmpty(priceMin), TextUtils.isEmpty(priceMax)).filter(bool -> !bool).count();
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int threshold = 3;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findLastVisibleItemPosition() >= adapter.getItemCount() - threshold) {
                    pullAds();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && layoutManager.findLastVisibleItemPosition() >= adapter.getItemCount() - threshold) {
                    pullAds();
                }
            }
        });
    }

    @Override
    public void adsReceived(List<AdMinimalData> ads) {
        if (!ads.isEmpty()) {
            LinkedHashSet<AdMinimalData> noDuplicates = new LinkedHashSet<>(this.ads);
            noDuplicates.addAll(ads);
            this.ads.clear();
            this.ads.addAll(ads);
            adapter.notifyDataSetChanged();
        }
        setEmptyListTextView();
        showProgress(false);
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        if (ads.size() == 0) {
            startActivity(new Intent(getActivity(), NetErrorActivity.class));
        } else {
            showProgress(false);
            MRKUtil.connectionProblem(getActivity());
        }
    }

    private void setEmptyListTextView() {
        if (!ads.isEmpty()) {
            emptyListTextView.setVisibility(View.GONE);
        } else {
            emptyListTextView.setText(getResources().getStringArray(R.array.ad_list_empty)[listMode - 1]);
            emptyListTextView.setVisibility(View.VISIBLE);
        }
    }
    private void pullAds() {
        if (inProgress) {
            return;
        }
        inProgress = true;
        showProgress(true);
        switch (listMode) {
            case ListModes.NORMAL_MODE:
                Long categoryId = sctId != 0L ? sctId : catId;
                adManager.pullAds(ads.size(), sortingMethod, title, prvId, categoryId, priceMin, priceMax, this);
                break;
            case ListModes.ACTIVE_MODE:
                adManager.pullUserAds(adapter.getItemCount(), true, this);
                break;
            case ListModes.INACTIVE_MODE:
                adManager.pullUserAds(adapter.getItemCount(), false, this);
                break;
            case ListModes.FAVOURITE_MODE:
                adManager.pullFavourites(adapter.getItemCount(), this);
                break;
        }
    }

    private void showProgress(final boolean show) {
        inProgress = show;
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    private void sortingMenuItemClicked(int labelId, int sortingMethod) {
        if (this.sortingMethod != sortingMethod) {
            sortLabel.setText(getString(labelId));
            this.sortingMethod = sortingMethod;
            resetAdapter();
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
    public void viewAd(Long id, int position) {
        Intent viewIntent = new Intent(getActivity(), ViewActivity.class);
        viewIntent.putExtra(AppConstants.AD_ID_KEY, id);
        viewIntent.putExtra(AppConstants.AD_POSITION, position);
        viewIntent.putExtra(AppConstants.VIEW_MODE, listMode == ListModes.FAVOURITE_MODE ? ListModes.NORMAL_MODE : listMode);
        startActivityForResult(viewIntent, REMOVABLE_CODE);
    }

    @Override
    public void editAd(final Long id, int position) {
        Intent editIntent = new Intent(getActivity(), EditActivity.class);
        editIntent.putExtra(AppConstants.AD_ID_KEY, id);
        editIntent.putExtra(AppConstants.AD_POSITION, position);
        startActivityForResult(editIntent, REMOVABLE_CODE);
    }

    @Override
    public void refreshAd(final Long id, final Button button) {
        //   changeAd(id, REFRESH_ACTION, 0, button);
    }


//
//
//
//

    @Override
    public void changeAdStatus(final Long id, final int position) {
        //  changeAd(id, STATUS_ACTION, position, null);
    }

    @Override
    public void removeFavourite(final Long id, final int position) {
        //     changeAd(id, FAVOURITE_ACTION, position, null);
    }

    @Override
    public void adStatusChanged(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled adStatusChanged");
    }

    public interface SortingMethod {
        int NEWEST = 1;
        int CHEAPEST = 2;
        int MOSTEXPENSIVE = 3;
    }

    public interface ListModes {
        int NORMAL_MODE = 1;
        int ACTIVE_MODE = 2;
        int INACTIVE_MODE = 3;
        int FAVOURITE_MODE = 4;
    }

//
//    private void changeAd(Long id, final int action, final int position, final Button button) {
//        if (inProgress) {
//            return;
//        }
//        inProgress = true;
//        showProgress(true);
//        if (action == REFRESH_ACTION) {
//            adManager.refreshAd(id, this);
//        } else if (action == STATUS_ACTION) {
//            adManager.changeAdStatus(id, this);
//        } else if (action == FAVOURITE_ACTION) {
//            adManager.removeFavourite(id, this);
//        }
//        callAd.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    changeSuccess(action, position, button);
//                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                    connectionProblem();
//                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
//                    adNotFound(position);
//                } else if (response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
//                    favouriteNotExists(position);
//                } else {
//                    connectionProblem();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                if (!call.isCanceled()) connectionProblem();
//            }
//        });
//    }
//
//    private void removeAdFromAdapter(int position) {
//        ads.remove(position);
//        adapter.notifyDataSetChanged();
//        setEmptyListTextView();
//        endOfCall();
//    }
//
//    private void changeSuccess(int action, int position, Button button) {
//        if (action == REFRESH_ACTION) {
//            MRKUtil.toast(getActivity(), getString(R.string.toast_ad_refreshed));
//            button.setEnabled(false);
//            endOfCall();
//        } else {
//            if (action == STATUS_ACTION) {
//                if (listMode == ListModes.ACTIVE_MODE) {
//                    MRKUtil.toast(getActivity(), getString(R.string.toast_ad_deactivated));
//                } else {
//                    MRKUtil.toast(getActivity(), getString(R.string.toast_ad_activated));
//                }
//            } else if (action == FAVOURITE_ACTION) {
//                MRKUtil.toast(getActivity(), getString(R.string.toast_removed_favourite));
//            }
//            removeAdFromAdapter(position);
//        }
//    }
//
//    private void endOfCall() {
//
//        inProgress = false;
//    }
//
//    private void adNotFound(int position) {
//        removeAdFromAdapter(position);
//        MRKUtil.toast(getActivity(), getString(R.string.toast_ad_not_exist));
//    }
//
//    private void favouriteNotExists(int position) {
//        removeAdFromAdapter(position);
//        MRKUtil.toast(getActivity(), getString(R.string.toast_not_favourite));
//    }
//

//
//    private void connectionProblem() {
//        showProgress(false);
//        MRKUtil.connectionProblem(getActivity());
//    }

}
