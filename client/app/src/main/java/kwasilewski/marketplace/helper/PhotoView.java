package kwasilewski.marketplace.helper;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

public class PhotoView extends android.support.v7.widget.AppCompatImageView {

    private boolean miniature = false;
    private boolean containsPhoto = false;
    private int position = 0;
    private Uri uri;

    public PhotoView(Context context) {
        super(context);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isMiniature() {
        return miniature;
    }

    public void setMiniature(boolean miniature) {
        this.miniature = miniature;
    }

    public boolean isContainsPhoto() {
        return containsPhoto;
    }

    public void setContainsPhoto(boolean containsPhoto) {
        this.containsPhoto = containsPhoto;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

}
