package name.ssf.vk_photo.utils;

import android.graphics.Bitmap;

public interface ContainsImage {
    Bitmap getImage();
    void setImage(Bitmap image);
    String getThumbUrl();
}
