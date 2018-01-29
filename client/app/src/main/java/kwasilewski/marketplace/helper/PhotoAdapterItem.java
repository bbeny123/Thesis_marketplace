package kwasilewski.marketplace.helper;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kwasilewski.marketplace.R;

public class PhotoAdapterItem extends Fragment {

    private static final String KEY_CONTENT = "encodedPhoto";
    private byte[] decodedPhoto;

    public PhotoAdapterItem() {
    }

    public static PhotoAdapterItem newInstance(byte[] photo) {
        PhotoAdapterItem fragment = new PhotoAdapterItem();
        Bundle args = new Bundle();
        args.putByteArray(KEY_CONTENT, photo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            decodedPhoto = getArguments().getByteArray(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ad_pager_item, container, false);
        ImageView imageView = view.findViewById(R.id.pager_photo);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedPhoto, 0, decodedPhoto.length));
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray(KEY_CONTENT, decodedPhoto);
    }

}
