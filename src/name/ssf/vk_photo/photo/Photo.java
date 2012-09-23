package name.ssf.vk_photo.photo;

import android.graphics.Bitmap;
import name.ssf.vk_photo.utils.ContainsImage;

public class Photo implements ContainsImage {
    private String thumbPhotoUrl;
    private String fullPhotoUrl;
    private Bitmap thumbImage;
    private Bitmap fullImage;

    public String getThumbPhotoUrl() {
        return thumbPhotoUrl;
    }

    public void setThumbPhotoUrl(String thumbPhotoUrl) {
        this.thumbPhotoUrl = thumbPhotoUrl;
    }

    public String getFullPhotoUrl() {
        return fullPhotoUrl;
    }

    public void setFullPhotoUrl(String fullPhotoUrl) {
        this.fullPhotoUrl = fullPhotoUrl;
    }

    public Bitmap getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(Bitmap thumbImage) {
        this.thumbImage = thumbImage;
    }

    public Bitmap getFullImage() {
        return fullImage;
    }

    public void setFullImage(Bitmap fullImage) {
        this.fullImage = fullImage;
    }

    @Override
    public Bitmap getImage() {
        return getThumbImage();
    }

    @Override
    public void setImage(Bitmap image) {
        setThumbImage(image);
    }

    @Override
    public String getThumbUrl() {
        return getThumbPhotoUrl();
    }
}
