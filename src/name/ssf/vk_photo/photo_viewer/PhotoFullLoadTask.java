package name.ssf.vk_photo.photo_viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoFullLoadTask extends AsyncTaskLoader<Bitmap> {

    private Bitmap bitmap;
    private String url;

    private MCallbacks mCallbacks;
    public interface MCallbacks {
        void loading();
        void connectionError();
    }

    public PhotoFullLoadTask(Context context, String url) {
        super(context);
        this.url = url;
        this.mCallbacks = (MCallbacks) context;
    }


    @Override
    public Bitmap loadInBackground() {
        mCallbacks.loading();
        Bitmap result = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            result = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            mCallbacks.connectionError();
        }

        return result;
    }

    @Override
    public void deliverResult(Bitmap data) {
        bitmap = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (bitmap != null) {
            deliverResult(bitmap);
        } else {
            forceLoad();
        }
    }

}
