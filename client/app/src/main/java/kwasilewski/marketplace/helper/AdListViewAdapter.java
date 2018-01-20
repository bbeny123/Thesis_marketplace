package kwasilewski.marketplace.helper;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.fragment.AdFragment;
import kwasilewski.marketplace.dto.ad.AdMinimalData;

public class AdListViewAdapter extends RecyclerView.Adapter<AdListViewAdapter.ViewHolder> {

    private final List<AdMinimalData> ads;
    private final FragmentActivity activity;

    public AdListViewAdapter(List<AdMinimalData> ads, FragmentActivity activity) {
        this.ads = ads;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ad, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setAd(ads.get(holder.getAdapterPosition()));
        holder.setTitle();
        holder.setPrice();
        holder.setViews();
        holder.setThumbnail();

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, AdFragment.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private AdMinimalData ad;
        private final View view;
        private final TextView title;
        private final TextView price;
        private final TextView views;
        private final ImageView thumbnail;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.ad_list_title);
            price = view.findViewById(R.id.ad_list_price);
            views = view.findViewById(R.id.ad_list_views);
            thumbnail = view.findViewById(R.id.ad_list_thumbnail);
        }

        private Long getAdId() {
            return ad.getId();
        }

        private void setAd(AdMinimalData ad) {
            this.ad = ad;
        }

        private void setTitle() {
            this.title.setText(ad.getTitle());
        }

        private void setPrice() {
            this.price.setText(String.format(activity.getString(R.string.ad_price_text), ad.getPrice()));
        }

        private void setViews() {
            this.views.setText(String.format(Locale.getDefault(), "%d", ad.getViews()));
        }

        private void setThumbnail() {
            if(ad.getMiniature() != null) {
                this.thumbnail.setImageBitmap(ad.getDecodedMiniature());
            }
        }
    }

}
