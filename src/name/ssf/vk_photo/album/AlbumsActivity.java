package name.ssf.vk_photo.album;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;
import name.ssf.vk_photo.R;
import name.ssf.vk_photo.login.LoginActivity;
import name.ssf.vk_photo.photo.PhotosActivity;
import name.ssf.vk_photo.utils.ThumbLoader;


import java.util.ArrayList;
import java.util.List;

public class AlbumsActivity extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<List<Album>>,
        AlbumsLoadTask.MCallbacks,
        ThumbLoader.Notifiable {

    public final static String EXTRA_USER_ID = "EXTRA_USER_ID";
    public final static String EXTRA_TOKEN = "EXTRA_TOKEN";
    public final static String EXTRA_ALBUM_ID = "EXTRA_ALBUM_ID";

    private final static int ALBUMS_LOADER_ID = 1;

    private List<Album> albums = new ArrayList<Album>();
    private ArrayAdapter<Album> albumsArrayAdapter;
    private Thread albumThumbLoaderThread;
    private ThumbLoader<Album> albumThumbLoader;
    private String userId;
    private String token;
    private ProgressDialog progressDialog;
    private boolean isProgressDialogDismissCalled;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums);

        userId = (String) getIntent().getExtras().get(LoginActivity.EXTRA_USER_ID);
        token = (String) getIntent().getExtras().get(LoginActivity.EXTRA_TOKEN);


        albumsArrayAdapter = new AlbumsAdapter(this, R.layout.albums_item, albums);
        final GridView albumsGridView = (GridView) findViewById(R.id.albums_grid_view);
        albumsGridView.setAdapter(albumsArrayAdapter);
        albumsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Album album = (Album) albumsGridView.getItemAtPosition(position);
                Intent intent = new Intent(AlbumsActivity.this, PhotosActivity.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                intent.putExtra(EXTRA_TOKEN, token);
                intent.putExtra(EXTRA_ALBUM_ID, album.getAid());
                startActivity(intent);
            }
        });


        getSupportLoaderManager().initLoader(ALBUMS_LOADER_ID, null, this).startLoading();//forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.albums_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.albums_menu_logout:
                CookieManager.getInstance().removeAllCookie();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //don't need back to login activity
    }


    @Override
    public Loader<List<Album>> onCreateLoader(int id, Bundle args) {
        return new AlbumsLoadTask(this, userId, token);
    }

    @Override
    public void onLoadFinished(Loader<List<Album>> loader, List<Album> data) {
        albumsArrayAdapter.clear();
        for (Album album : data) {
            albumsArrayAdapter.add(album);
        }

        albumThumbLoader = new ThumbLoader<Album>(albums, this);
        albumThumbLoaderThread = new Thread(albumThumbLoader);
        albumThumbLoaderThread.start();


        if (progressDialog != null) {
            progressDialog.dismiss();
            isProgressDialogDismissCalled = true;
            progressDialog = null;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Album>> loader) {
        albumsArrayAdapter.clear();
        if (albumThumbLoaderThread.isAlive()) {
            albumThumbLoader.stopLoadThumbs();
        }
    }

    @Override
    public void thumbLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                albumsArrayAdapter.notifyDataSetChanged();
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
                    progressDialog = ProgressDialog.show(AlbumsActivity.this, "",
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
                Toast.makeText(AlbumsActivity.this,
                        getResources().getText(R.string.connection_error),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}