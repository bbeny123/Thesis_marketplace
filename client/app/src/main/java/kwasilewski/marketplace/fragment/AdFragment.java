package kwasilewski.marketplace.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.helper.AdListViewAdapter;
import kwasilewski.marketplace.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String LIST_MODE = "mode";
    private int listMode;
    private RecyclerView recyclerView;
    private Menu menu;
private TextView sortByLabel;
    private List<AdMinimalData> ads = new ArrayList<>();
    private AdListViewAdapter adapter;
    private SearchView searchView;
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
        recyclerView = view.findViewById(R.id.list);
        sortByLabel = view.findViewById(R.id.button_sort_by);
        sortByLabel.setOnClickListener(sortbyListener);
        getAds();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        this.menu = menu;
        inflater.inflate(R.menu.menu_with_searchbar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("search");
    }

    private void getAds() {
        Call<List<AdMinimalData>> callAd = RetrofitService.getInstance().getAdService().getAds(new HashMap<String, String>());
        callAd.enqueue(new Callback<List<AdMinimalData>>() {
            @Override
            public void onResponse(Call<List<AdMinimalData>> call, Response<List<AdMinimalData>> response) {
                if (response.isSuccessful()) {
                    getAdsSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AdMinimalData>> call, Throwable t) {
                //         if (!call.isCanceled()) connectionProblemAtStart();
            }
        });
    }

    private void getAdsSuccess(List<AdMinimalData> newAds) {
        if(!newAds.isEmpty()) {
            ads.addAll(newAds);
            if(adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                setAdapter();
            }
        }

    }

    private void setAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdListViewAdapter(ads, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && adapter.getLastItemPosition() >= adapter.getItemCount() - 3) {
                    getAds();
                }
            }
        });
    }

//    public HashMap<String, String>
    public interface ListModes {
        int NORMAL_MODE = 1;
        int ACITVE_MODE = 2;
        int INACTIVE_MODE = 3;
        int FAVOURITE_MODE = 4;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Long id);
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        searchView.clearFocus();


        if (menu != null) {
            (menu.findItem(R.id.action_search)).collapseActionView();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        return false;
    }

    private View.OnClickListener sortbyListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            if (getContext() == null) {
                return;
            }
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.inflate(R.menu.ads_sort_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.sort_newest:
                            sortByLabel.setText(getString(R.string.label_sort_default));
                            break;
                        case R.id.sort_cheapest:
                            sortByLabel.setText(getString(R.string.label_sort_cheapest));
                            break;
                        case R.id.sort_most_expensive:
                            sortByLabel.setText(getString(R.string.label_sort_most_expensive));
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    };
}
