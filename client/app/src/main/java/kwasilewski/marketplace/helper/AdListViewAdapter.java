package kwasilewski.marketplace.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.activity.AdFragment;
import kwasilewski.marketplace.dto.ad.AdMinimalData;

public class AdListViewAdapter extends RecyclerView.Adapter<AdListViewAdapter.ViewHolder> {

    private final List<AdMinimalData> ads;
    private final Context context;
    private final OnButtonsClickListener listener;
    private final int listMode;

    public AdListViewAdapter(List<AdMinimalData> ads, Context context, OnButtonsClickListener listener, int listMode) {
        this.ads = ads;
        this.context = context;
        this.listener = listener;
        this.listMode = listMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ad, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AdMinimalData ad = ads.get(position);
        holder.setTitle(ad.getTitle());
        holder.setPrice(ad.getPrice());
        holder.setViews(ad.getViews());
        holder.setThumbnail(ad.getDecodedMiniature());
        setListeners(holder, ad.getId(), ad.isRefreshable(), holder.getAdapterPosition());
    }

    private void setListeners(final ViewHolder holder, final Long id, final boolean refreshable, int position) {
        holder.view.setOnClickListener(getItemClickListener(id, position));

        if(holder.editButton != null) {
            holder.editButton.setOnClickListener(getEditClickListener(id, position));
        }
        if(holder.refreshButton != null) {
            if (refreshable) {
                holder.refreshButton.setOnClickListener(getRefreshClickListener(id, holder.refreshButton));
            } else {
                holder.refreshButton.setEnabled(false);
            }
        }

        if(holder.statusButton != null) {
            holder.statusButton.setOnClickListener(getStatusClickListener(id, position));
        }

        if(holder.favouriteButton != null) {
            holder.favouriteButton.setOnClickListener(getFavouriteClickListener(id, position));
        }

    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView title;
        private final TextView price;
        private final TextView views;
        private final ImageView thumbnail;
        private final Button editButton;
        private final Button refreshButton;
        private final Button statusButton;
        private final Button favouriteButton;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.ad_list_title);
            price = view.findViewById(R.id.ad_list_price);
            views = view.findViewById(R.id.ad_list_views);
            thumbnail = view.findViewById(R.id.ad_list_thumbnail);

            if (listMode == AdFragment.ListModes.ACTIVE_MODE) {
                View buttonsContainer = view.findViewById(R.id.ad_list_active_buttons);
                buttonsContainer.setVisibility(View.VISIBLE);
                editButton = view.findViewById(R.id.ad_list_active_edit);
                refreshButton = view.findViewById(R.id.ad_list_active_refresh);
                statusButton = view.findViewById(R.id.ad_list_active_finish);
                favouriteButton = null;
            } else if (listMode == AdFragment.ListModes.INACTIVE_MODE) {
                View buttonsContainer = view.findViewById(R.id.ad_list_inactive_buttons);
                buttonsContainer.setVisibility(View.VISIBLE);
                editButton = view.findViewById(R.id.ad_list_inactive_edit);
                refreshButton = null;
                statusButton = view.findViewById(R.id.ad_list_inactive_activate);
                favouriteButton = null;
            } else if (listMode == AdFragment.ListModes.FAVOURITE_MODE) {
                View buttonsContainer = view.findViewById(R.id.ad_list_favourite_buttons);
                buttonsContainer.setVisibility(View.VISIBLE);
                editButton = null;
                refreshButton = null;
                statusButton = null;
                favouriteButton = view.findViewById(R.id.ad_list_favourite_remove);
            } else {
                editButton = null;
                refreshButton = null;
                statusButton = null;
                favouriteButton = null;
            }
        }

        private void setTitle(String title) {
            this.title.setText(title);
        }

        private void setPrice(String price) {
            this.price.setText(String.format(context.getString(R.string.ad_price_text), price));
        }

        private void setViews(String views) {
            this.views.setText(String.format(Locale.getDefault(), "%s", views));
        }

        private void setThumbnail(Bitmap bitmap) {
            if(bitmap != null) {
                this.thumbnail.setImageBitmap(bitmap);
            }
        }
    }

    private View.OnClickListener getItemClickListener(final Long id, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.viewAd(id, position);
            }
        };
    }

    private View.OnClickListener getEditClickListener(final Long id, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editAd(id, position);
            }
        };
    }

    private View.OnClickListener getRefreshClickListener(final Long id, final Button button) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.refreshAd(id, button);
            }
        };
    }

    private View.OnClickListener getStatusClickListener(final Long id, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.changeAdStatus(id, position);
            }
        };
    }

    private View.OnClickListener getFavouriteClickListener(final Long id, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.removeFavourite(id, position);
            }
        };
    }

    public interface OnButtonsClickListener {

        void viewAd(final Long id, final int position);

        void editAd(final Long id, final int position);

        void refreshAd(final Long id, final Button button);

        void changeAdStatus(final Long id, final int position);

        void removeFavourite(final Long id, final int position);

    }

}
