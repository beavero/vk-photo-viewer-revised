package name.ssf.vk_photo.photo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.*;
import name.ssf.vk_photo.R;
import name.ssf.vk_photo.album.AlbumsActivity;
import name.ssf.vk_photo.photo_viewer.PhotoViewerActivity;
import name.ssf.vk_photo.utils.ThumbLoader;

import java.util.ArrayList;
import java.util.List;

public class PhotosActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<Photo>>, PhotosLoadTask.MCallbacks, ThumbLoader.Notifiable {
    public final static String EXTRA_PHOTO_URL = "EXTRA_PHOTO_URL";

    private static final int PHOTO_LOADER_ID = 1;

    private String userId;
    private String token;
    private String albumId;

    private List<Photo> photos = new ArrayList<Photo>();
    private ArrayAdapter<Photo> photosArrayAdapter;
    private Thread photoThumbLoaderThread;
    private ThumbLoader<Photo> photoThumbLoader;
    private ProgressDialog progressDialog;
    private boolean isProgressDialogDismissCalled;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos);


        Bundle extras = getIntent().getExtras();
        userId = extras.getString(AlbumsActivity.EXTRA_USER_ID);
        token = extras.getString(AlbumsActivity.EXTRA_TOKEN);
        albumId = extras.getString(AlbumsActivity.EXTRA_ALBUM_ID);

        photosArrayAdapter = new PhotosArrayAdapter(this, R.layout.photos_item, photos);
        final GridView photosGridView = (GridView) findViewById(R.id.photos_grid_view);
        photosGridView.setAdapter(photosArrayAdapter);
        photosGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Photo photo = (Photo) photosGridView.getItemAtPosition(position);
                Intent intent = new Intent(PhotosActivity.this, PhotoViewerActivity.class);
                intent.putExtra(EXTRA_PHOTO_URL, photo.getFullPhotoUrl());
                startActivity(intent);

            }
        });

        getSupportLoaderManager().initLoader(PHOTO_LOADER_ID, null, this).startLoading();

    }


    @Override
    public Loader<List<Photo>> onCreateLoader(int id, Bundle args) {
        return new PhotosLoadTask(this, userId, token, albumId);
    }

    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> data) {
        photosArrayAdapter.clear();
        for (Photo photo : data) {
            photosArrayAdapter.add(photo);
        }

        photoThumbLoader = new ThumbLoader<Photo>(photos, this);
        photoThumbLoaderThread = new Thread(photoThumbLoader);
        photoThumbLoaderThread.start();

        if (progressDialog != null) {
            progressDialog.dismiss();
            isProgressDialogDismissCalled = true;
            progressDialog = null;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Photo>> loader) {
        photosArrayAdapter.clear();
        if (photoThumbLoaderThread.isAlive()) {
            photoThumbLoader.stopLoadThumbs();
        }
    }

    @Override
    public void thumbLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                photosArrayAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void loading() {
        isProgressDialogDismissCalled = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null && isProgressDialogDismissCalled == false) {
                    progressDialog = ProgressDialog.show(PhotosActivity.this, "",
                            getResources().getText(R.string.progress_dialog_loading_message), true);
                }
            }
        });

    }



    @Override
    public void connectionError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(PhotosActivity.this,
                        getResources().getText(R.string.connection_error),
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}