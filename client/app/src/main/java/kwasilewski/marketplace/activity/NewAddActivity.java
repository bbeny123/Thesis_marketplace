package kwasilewski.marketplace.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.util.MRKDialogItem;
import kwasilewski.marketplace.util.MRKImageView;
import kwasilewski.marketplace.util.MRKImageViewList;
import kwasilewski.marketplace.util.MRKUtil;

public class NewAddActivity extends AppCompatActivity {

    private final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private final int PERMISSION_CODE = 1;
    private final int MATISSE_CODE = 2;
    private final int MAX_PHOTOS = 10;
    private AlertDialog.Builder builder;

    MRKImageViewList photos = new MRKImageViewList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add);

        Toolbar toolbar = findViewById(R.id.new_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        photos.add((MRKImageView)findViewById(R.id.new_image1));
        photos.add((MRKImageView)findViewById(R.id.new_image2));
        photos.add((MRKImageView)findViewById(R.id.new_image3));
        photos.add((MRKImageView)findViewById(R.id.new_image4));
        photos.add((MRKImageView)findViewById(R.id.new_image5));
        photos.add((MRKImageView)findViewById(R.id.new_image6));
        photos.add((MRKImageView)findViewById(R.id.new_image7));
        photos.add((MRKImageView)findViewById(R.id.new_image8));
        photos.add((MRKImageView)findViewById(R.id.new_image9));
        photos.add((MRKImageView)findViewById(R.id.new_image10));

        for (MRKImageView photo : photos.getPhotos()) {
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPhoto(((MRKImageView)v).getPosition());
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults.length > 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    MRKUtil.toast(this, getString(R.string.toast_permissions));
                    return;
                }
            }
            callMatisse();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MATISSE_CODE && resultCode == RESULT_OK) {
            for(Uri uri : Matisse.obtainResult(data)) {
                photos.addPhoto(this, uri);
            }
        }
    }

    private void clickPhoto(int position) {
        if (photos.containsPhoto(position)) {
            photoDialog(position);
        } else {
            if (!hasPermissions()) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
            } else {
                callMatisse();
            }
        }
    }

    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void photoDialog(final int position) {
        if (builder == null) {
            MRKDialogItem[] items = {
                    new MRKDialogItem(getString(R.string.action_set_thumbnail), android.R.drawable.ic_menu_gallery),
                    new MRKDialogItem(getString(R.string.action_remove_photo), android.R.drawable.ic_menu_delete)
            };
            builder = new AlertDialog.Builder(this);
            builder.setAdapter(MRKUtil.getDialogAdapter(this, items), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        setMiniature(position);
                    } else if (which == 1) {
                        removePhoto(position);
                    }
                }
            });
            builder.setPositiveButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        builder.show();
    }

    private void setMiniature(int position) {
        photos.setThumbnail(this, position);
    }

    private void removePhoto(int position) {
        photos.remove(this, position);
    }

    private void callMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "kwasilewski.marketplace.fileprovider"))
                .countable(true)
                .maxSelectable(MAX_PHOTOS - photos.getPhotoContained())
                .imageEngine(new PicassoEngine())
                .theme(R.style.Matisse_Dracula)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .forResult(MATISSE_CODE);
    }
}
