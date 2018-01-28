package kwasilewski.marketplace.helper;

import android.app.Activity;
import android.graphics.BitmapFactory;
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
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.retrofit.listener.AdListener;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.manager.AdManager;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import okhttp3.ResponseBody;

public class AdListViewAdapter extends RecyclerView.Adapter<AdListViewAdapter.ViewHolder> {

    private final List<AdMinimalData> ads;
    private final Activity activity;
    private final OnButtonsClickListener listener;
    private final int listMode;

    public AdListViewAdapter(List<AdMinimalData> ads, Activity activity, OnButtonsClickListener listener, int listMode) {
        this.ads = ads;
        this.activity = activity;
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
        holder.enableRefreshButton(ad.isRefreshable());
        holder.setButtonListeners(ad.getId());
        holder.view.setOnClickListener(v -> listener.viewAd(ad.getId(), position));

    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public interface OnButtonsClickListener {

        void viewAd(final Long id, int position);

        void editAd(final Long id, int position);

        void refreshAd(final Long id, final AdManager manager, final ErrorListener errorListener);

        void changeAdStatus(final Long id, final AdManager manager, final ErrorListener errorListener);

        void removeFavourite(final Long id, final AdManager manager, final ErrorListener errorListener);

        void removeAd(final int position);

        void adRefreshed();

        void unhandledError(Activity activity, String error);

    }

    class ViewHolder extends RecyclerView.ViewHolder implements AdListener, ErrorListener {
        private final View view;
        private final TextView title;
        private final TextView price;
        private final TextView views;
        private final ImageView thumbnail;
        private final Button editButton;
        private final Button refreshButton;
        private final Button statusButton;
        private final Button favouriteButton;
        private AdManager adManager;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.ad_list_title);
            price = view.findViewById(R.id.ad_list_price);
            views = view.findViewById(R.id.ad_list_views);
            thumbnail = view.findViewById(R.id.ad_list_thumbnail);

            switch (listMode) {
                case AppConstants.MODE_ACTIVE: {
                    View buttonsContainer = view.findViewById(R.id.ad_list_active_buttons);
                    buttonsContainer.setVisibility(View.VISIBLE);
                    editButton = view.findViewById(R.id.ad_list_active_edit);
                    refreshButton = view.findViewById(R.id.ad_list_active_refresh);
                    statusButton = view.findViewById(R.id.ad_list_active_finish);
                    favouriteButton = null;
                    break;
                }
                case AppConstants.MODE_INACTIVE: {
                    View buttonsContainer = view.findViewById(R.id.ad_list_inactive_buttons);
                    buttonsContainer.setVisibility(View.VISIBLE);
                    editButton = view.findViewById(R.id.ad_list_inactive_edit);
                    refreshButton = null;
                    statusButton = view.findViewById(R.id.ad_list_inactive_activate);
                    favouriteButton = null;
                    break;
                }
                case AppConstants.MODE_FAVOURITE: {
                    View buttonsContainer = view.findViewById(R.id.ad_list_favourite_buttons);
                    buttonsContainer.setVisibility(View.VISIBLE);
                    editButton = null;
                    refreshButton = null;
                    statusButton = null;
                    favouriteButton = view.findViewById(R.id.ad_list_favourite_remove);
                    break;
                }
                default:
                    editButton = null;
                    refreshButton = null;
                    statusButton = null;
                    favouriteButton = null;
                    break;
            }
        }

        private void setTitle(String title) {
            this.title.setText(title);
        }

        private void setPrice(String price) {
            this.price.setText(String.format(activity.getString(R.string.ad_price_text), price));
        }

        private void setViews(String views) {
            this.views.setText(String.format(Locale.getDefault(), "%s", views));
        }

        private void setThumbnail(byte[] decodedPhoto) {
            this.thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(decodedPhoto, 0, decodedPhoto.length));
        }

        private void enableRefreshButton(boolean enable) {
            if (refreshButton != null) {
                refreshButton.setEnabled(enable);
            }
        }

        private AdManager getAdManager() {
            if (adManager == null) {
                adManager = new AdManager(activity, this);
            }
            return adManager;
        }

        private void setButtonListeners(Long id) {
            if (editButton != null) {
                editButton.setOnClickListener(v -> listener.editAd(id, getAdapterPosition()));
            }
            if (refreshButton != null) {
                refreshButton.setOnClickListener(v -> listener.refreshAd(id, getAdManager(), this));
            }
            if (statusButton != null) {
                statusButton.setOnClickListener(v -> listener.changeAdStatus(id, getAdManager(), this));
            }
            if (favouriteButton != null) {
                favouriteButton.setOnClickListener(v -> listener.removeFavourite(id, getAdManager(), this));
            }
        }

        @Override
        public void adStatusChanged(ResponseBody responseBody) {
            MRKUtil.toast(activity, listMode == AppConstants.MODE_ACTIVE ? activity.getString(R.string.toast_ad_deactivated) : activity.getString(R.string.toast_ad_activated));
            listener.removeAd(getAdapterPosition());
        }

        @Override
        public void adRefreshed(ResponseBody responseBody) {
            MRKUtil.toast(activity, activity.getString(R.string.toast_ad_refreshed));
            refreshButton.setEnabled(false);
            listener.adRefreshed();
        }

        @Override
        public void favouriteRemoved(ResponseBody responseBody) {
            MRKUtil.toast(activity, activity.getString(R.string.toast_removed_favourite));
            listener.removeAd(getAdapterPosition());
        }

        @Override
        public void notFound(Activity activity) {
            MRKUtil.toast(activity, activity.getString(R.string.toast_ad_not_exist));
            listener.removeAd(getAdapterPosition());
        }

        @Override
        public void notAcceptable(Activity activity) {
            MRKUtil.toast(activity, activity.getString(R.string.toast_not_favourite));
            listener.removeAd(getAdapterPosition());
        }

        @Override
        public void unhandledError(Activity activity, String error) {
            MRKUtil.toast(activity, activity.getString(R.string.toast_not_favourite));
            listener.unhandledError(activity, error);
        }
    }

}
