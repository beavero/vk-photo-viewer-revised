package name.ssf.vk_photo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ThumbLoader<T extends ContainsImage> implements Runnable {

    private List<T> list;
    private Notifiable notifiable;
    private volatile boolean isStopped;

    public synchronized void stopLoadThumbs() {
        isStopped = true;
    }

    @Override
    public void run() {
        loadThumbs();
    }

    public interface Notifiable {
        void thumbLoaded();
    }

    public ThumbLoader(List<T> list, Notifiable notifiable) {
        this.list = list;
        this.notifiable = notifiable;
    }

    private void loadThumbs() {
        stopped:
        for (T item : list) {

            synchronized (this) {
                if (isStopped) {
                    break stopped;
                }
            }

            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } */

            if (item.getThumbUrl() != null) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(item.getThumbUrl()).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap thumbBitmap = BitmapFactory.decodeStream(input);
                    item.setImage(thumbBitmap);
                    notifiable.thumbLoaded();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
