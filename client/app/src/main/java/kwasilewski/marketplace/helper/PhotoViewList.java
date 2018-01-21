package kwasilewski.marketplace.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kwasilewski.marketplace.R;

public class PhotoViewList {

    private final List<PhotoView> photos = new ArrayList<>();
    private int photoContained = 0;

    public PhotoViewList() {
    }

    public List<PhotoView> getPhotos() {
        return photos;
    }

    public void add(PhotoView photo) {
        photo.setPosition(photos.size());
        photos.add(photo);
    }

    public void remove(Context context, int position) {
        photoContained--;
        for(int i = position; i < photoContained; i++) {
            if (photos.get(i).getUri().equals(photos.get(i+1).getUri())) continue;
            photos.get(i).setUri(photos.get(i+1).getUri());
            Picasso.with(context).load(photos.get(i).getUri()).transform(new PhotoTransform(i == 0)).memoryPolicy(MemoryPolicy.NO_CACHE).into(photos.get(i));
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
        Picasso.with(context).load(photos.get(0).getUri()).transform(new PhotoTransform(true)).memoryPolicy(MemoryPolicy.NO_CACHE).into(photos.get(0));
        Picasso.with(context).load(photos.get(position).getUri()).transform(new PhotoTransform()).memoryPolicy(MemoryPolicy.NO_CACHE).into(photos.get(position));
    }

    public void addPhoto(Context context, Uri uri) {
        photos.get(photoContained).setContainsPhoto(true);
        photos.get(photoContained).setUri(uri);
        Picasso.with(context).load(uri).transform(new PhotoTransform(photoContained == 0)).memoryPolicy(MemoryPolicy.NO_CACHE).into(photos.get(photoContained++));
    }

    public String getEncodedThumbnail(Context context) {
        if (photoContained == 0) return null;
        byte[] encodedPhoto = getByteFromUri(context, photos.get(0).getUri());
        if(encodedPhoto != null) {
            return Base64.encodeToString(encodedPhoto, Base64.DEFAULT);
        }
        return null;
    }

    public List<String> getEncodedPhotos(Context context) {
        if (photoContained < 2) return null;
        List<String> encodedPhotos = new ArrayList<>();
        for (int i = 1; i < photoContained; i++) {
            byte[] encodedPhoto = getByteFromUri(context, photos.get(i).getUri());
            if(encodedPhoto != null) {
                encodedPhotos.add(Base64.encodeToString(encodedPhoto, Base64.DEFAULT));
            }
        }
        return encodedPhotos;
    }

    private byte[] getByteFromUri(Context context, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            return bos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

}
