package name.ssf.vk_photo.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import name.ssf.vk_photo.R;

import java.util.List;

public class PhotosArrayAdapter extends ArrayAdapter<Photo> {

    int resourceId;

    public PhotosArrayAdapter(Context context, int resourceId, List<Photo> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;

        if(convertView == null) {
            itemView = new LinearLayout(getContext());
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(resourceId, itemView, true);
        } else  {
            itemView = (LinearLayout) convertView;
        }

        Photo photoItem = getItem(position);

        if (photoItem.getThumbImage() != null) {
            ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.photos_item_progress_bar);
            progressBar.setVisibility(ProgressBar.GONE);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.photos_item_image);
            imageView.setImageBitmap(photoItem.getThumbImage());
            imageView.setVisibility(ImageView.VISIBLE);
        }

        return itemView;
    }
}
