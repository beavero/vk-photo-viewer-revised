package name.ssf.vk_photo.album;

import android.graphics.Bitmap;
import name.ssf.vk_photo.utils.ContainsImage;

public class Album implements ContainsImage {
    private String aid;
    private String title;
    private String thumbScreenUrl;
    private Bitmap thumb;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbScreenUrl() {
        return thumbScreenUrl;
    }

    public void setThumbScreenUrl(String thumbScreenUrl) {
        this.thumbScreenUrl = thumbScreenUrl;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    @Override
    public Bitmap getImage() {
        return getThumb();
    }

    @Override
    public void setImage(Bitmap image) {
        setThumb(image);
    }

    @Override
    public String getThumbUrl() {
        return getThumbScreenUrl();
    }
}
