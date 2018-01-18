package kwasilewski.marketplace.util;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kwasilewski.marketplace.R;

public class MRKImageViewList {

    private List<MRKImageView> photos = new ArrayList<>();
    private int photoContained = 0;

    public MRKImageViewList() {
    }

    public List<MRKImageView> getPhotos() {
        return photos;
    }

    public MRKImageView getEmpty(Uri uri) {
        photos.get(photoContained).setContainsPhoto(true);
        photos.get(photoContained).setUri(uri);
        return photos.get(photoContained++);
    }

    public void add(MRKImageView photo) {
        photo.setPosition(photos.size());
        photos.add(photo);
    }

    public void remove(Context context, int position) {
        photoContained--;
        for(int i = position; i < photoContained; i++) {
            if (photos.get(i).getUri().equals(photos.get(i+1).getUri())) continue;
            photos.get(i).setUri(photos.get(i+1).getUri());
            Picasso.with(context).load(photos.get(i).getUri()).transform(new RoundedCornersTransform(i == 0)).memoryPolicy(MemoryPolicy.NO_CACHE).into(photos.get(i));
        }
        photos.get(photoContained).setContainsPhoto(false);
        photos.get(photoContained).setUri(null);
        photos.get(photoContained).setImageResource(R.drawable.ic_add_photo);
    }

    public boolean containsPhoto(int position) {
        return photos.get(position).isContainsPhoto();
    }

    public int getPhotoContained() {
        return photoContained;
    }

    public void setThumbnail(Context context, int position) {
        if (position == 0 || photos.get(0).getUri().equals(photos.get(position).getUri())) return;
        Uri oldThumbnail = photos.get(0).getUri();
        photos.get(0).setUri(photos.get(position).getUri());
        photos.get(position).setUri(oldThumbnail);
        Picasso.with(context).load(photos.get(0).getUri()).transform(new RoundedCornersTransform(true)).memoryPolicy(MemoryPolicy.NO_CACHE).into(photos.get(0));
        Picasso.with(context).load(photos.get(position).getUri()).transform(new RoundedCornersTransform()).memoryPolicy(MemoryPolicy.NO_CACHE).into(photos.get(position));
    }

    public void addPhoto(Context context, Uri uri) {
        photos.get(photoContained).setContainsPhoto(true);
        photos.get(photoContained).setUri(uri);
        Picasso.with(context).load(uri).transform(new RoundedCornersTransform(photoContained == 0)).memoryPolicy(MemoryPolicy.NO_CACHE).into(photos.get(photoContained++));
    }

}
