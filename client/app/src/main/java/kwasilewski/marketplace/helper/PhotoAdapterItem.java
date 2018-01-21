package kwasilewski.marketplace.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kwasilewski.marketplace.R;

public class PhotoAdapterItem extends Fragment {

    private static final String KEY_CONTENT = "encodedPhoto";
    private Bitmap photo;
    private String encodedPhoto;

    public PhotoAdapterItem() {
    }

    public static PhotoAdapterItem newInstance(String photo) {
        PhotoAdapterItem fragment = new PhotoAdapterItem();
        Bundle args = new Bundle();
        args.putString("encodedPhoto", photo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            encodedPhoto = getArguments().getString(KEY_CONTENT);
            byte[] decodedPhoto = Base64.decode(encodedPhoto, Base64.DEFAULT);
            photo = BitmapFactory.decodeByteArray(decodedPhoto, 0, decodedPhoto.length);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ad_pager_item, container, false);
        ImageView imageView = view.findViewById(R.id.pager_imageView);
        imageView.setImageBitmap(photo);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, encodedPhoto);
    }

}