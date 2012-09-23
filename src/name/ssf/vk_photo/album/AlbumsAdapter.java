package name.ssf.vk_photo.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import name.ssf.vk_photo.R;

import java.util.List;

public class AlbumsAdapter extends ArrayAdapter<Album> {

    int textViewResourceId;

    public AlbumsAdapter(Context context, int textViewResourceId, List<Album> objects) {
        super(context, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout albumsItemView;

        if (convertView == null) {
            albumsItemView = new LinearLayout(getContext());
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater.inflate(textViewResourceId, albumsItemView, true);
        } else {
            albumsItemView = (LinearLayout) convertView;
        }

        Album albumsItem = getItem(position);

        TextView titleView = (TextView) albumsItemView.findViewById(R.id.albums_item_title);
        titleView.setText(albumsItem.getTitle());

        if (albumsItem.getThumb() != null) {
            ProgressBar progressBar = (ProgressBar) albumsItemView.findViewById(R.id.albums_item_progress_bar);
            progressBar.setVisibility(ProgressBar.GONE);

            ImageView imageView = (ImageView) albumsItemView.findViewById(R.id.albums_item_image);
            imageView.setImageBitmap(albumsItem.getThumb());
            imageView.setVisibility(ImageView.VISIBLE);
        }

        return albumsItemView;
    }
}
