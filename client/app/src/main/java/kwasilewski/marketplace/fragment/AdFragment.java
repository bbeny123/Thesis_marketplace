package kwasilewski.marketplace.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.helper.AdListViewAdapter;
import kwasilewski.marketplace.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdFragment extends Fragment {

    private static final String LIST_MODE = "mode";
    private int listMode;
    private RecyclerView rv;

    private List<AdMinimalData> ads;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ad_list, container, false);

        rv = view.findViewById(R.id.list);
        getAds();

        return view;
    }

    private void getAds() {
        Call<List<AdMinimalData>> callAd = RetrofitService.getInstance().getAdService().getAds(new HashMap<String, String>());
        callAd.enqueue(new Callback<List<AdMinimalData>>() {
            @Override
            public void onResponse(Call<List<AdMinimalData>> call, Response<List<AdMinimalData>> response) {
                if (response.isSuccessful()) {
                    ads = response.body();
                    setAdapter();
                }
            }

            @Override
            public void onFailure(Call<List<AdMinimalData>> call, Throwable t) {
                //         if (!call.isCanceled()) connectionProblemAtStart();
            }
        });
    }

    private void setAdapter() {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new AdListViewAdapter(ads, getActivity()));
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
