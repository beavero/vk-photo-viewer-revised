package name.ssf.vk_photo.photo_viewer;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import name.ssf.vk_photo.R;
import name.ssf.vk_photo.photo.PhotosActivity;


public class PhotoViewerActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Bitmap>, PhotoFullLoadTask.MCallbacks {

    private final static int PHOTO_LOADER_ID = 1245;

    private String photoUrl;
    private Bitmap photoBitmap;
    private ImageView imageView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private boolean isProgressDialogDismissCalled;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photo_viewer);

        Bundle extras = getIntent().getExtras();
        photoUrl = extras.getString(PhotosActivity.EXTRA_PHOTO_URL);

        imageView = (ImageView) findViewById(R.id.photo_viewer_image);
        progressBar = (ProgressBar) findViewById(R.id.photo_viewer_progress_bar);

        getSupportLoaderManager().initLoader(PHOTO_LOADER_ID, null, this);
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        return new PhotoFullLoadTask(this, photoUrl);
    }

    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
        photoBitmap = data;
        imageView.setImageBitmap(photoBitmap);
        progressBar.setVisibility(ProgressBar.GONE);
        imageView.setVisibility(ImageView.VISIBLE);

        if (progressDialog != null) {
            progressDialog.dismiss();
            isProgressDialogDismissCalled = true;
            progressDialog = null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {

    }

    @Override
    public void loading() {
        isProgressDialogDismissCalled = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null && isProgressDialogDismissCalled == false) {
                    progressDialog = ProgressDialog.show(PhotoViewerActivity.this, "",
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
                Toast.makeText(PhotoViewerActivity.this,
                        getResources().getText(R.string.connection_error),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}