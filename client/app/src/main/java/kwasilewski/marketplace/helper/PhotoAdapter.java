package kwasilewski.marketplace.helper;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends FragmentPagerAdapter {

    private List<byte[]> photos = new ArrayList<>();

    public PhotoAdapter(FragmentManager fm, List<byte[]> photos) {
        super(fm);
        this.photos = photos;
    }

    public static LinearLayout.LayoutParams getPagerLayout(final AppCompatActivity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new LinearLayout.LayoutParams(size.x, size.x);
    }

    @Override
    public Fragment getItem(int position) {
        if (photos.size() == 0) return null;
        return PhotoAdapterItem.newInstance(photos.get(position));
    }

    @Override
    public int getCount() {
        return photos.size();
    }

}