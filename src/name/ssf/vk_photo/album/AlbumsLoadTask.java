package name.ssf.vk_photo.album;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AlbumsLoadTask extends AsyncTaskLoader<List<Album>> {

    private final String userId;
    private final String token;

    private List<Album> loadedAlbums;

    private MCallbacks mCallbacks;
    public interface MCallbacks {
        void loading();
        void connectionError();
    }

    public AlbumsLoadTask(Context context, String userId, String token) {
        super(context);

        this.userId = userId;
        this.token = token;
        this.mCallbacks = (MCallbacks) context;
    }

    @Override
    public List<Album> loadInBackground() {
        mCallbacks.loading();
        return loadAlbums();
    }

    @Override
    public void deliverResult(List<Album> albums) {
        loadedAlbums = albums;
        super.deliverResult(albums);
    }

    @Override
    protected void onStartLoading() {
        if (loadedAlbums != null) {
            deliverResult(loadedAlbums);
        } else {
            forceLoad();
        }
    }

    private List<Album> loadAlbums() {
        HttpClient httpClient = new DefaultHttpClient();
        List<Album> parsedAlbums = null;

        String requestUrl = "https://api.vk.com/method/" + "photos.getAlbums.xml" +
                "?uid=" + userId +
                "&access_token=" + token +
                "&need_covers=" + 1;

        HttpPost request = new HttpPost(requestUrl);

        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();

            InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent());

            try {
                parsedAlbums = parseXML(inputStreamReader);
            } catch (XmlPullParserException e) {
                Log.v("XML parsing error", e.getMessage());
                assert false;
            } catch (VkParsingErrorException e) {
                mCallbacks.connectionError();
            }
        } catch (IOException ioException) {
//            ioException.printStackTrace();
            mCallbacks.connectionError();
        }

        return parsedAlbums;
    }

    private List<Album> parseXML(InputStreamReader inputStreamReader) throws XmlPullParserException, IOException, VkParsingErrorException {
        List<Album> result = new ArrayList<Album>();
        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();

        XmlPullParser xmlPullParser = parserFactory.newPullParser();
        xmlPullParser.setInput(inputStreamReader);

        String currentTag = "";

        Album tmpAlbum = new Album();
        for (int eventType = xmlPullParser.getEventType();
             eventType != XmlPullParser.END_DOCUMENT;
             eventType = xmlPullParser.next()) {

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    currentTag = xmlPullParser.getName();
                    if (currentTag.equals("album")) {
                        tmpAlbum = new Album();
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (xmlPullParser.getName().equals("album")) {
                        result.add(tmpAlbum);
                    }
                    currentTag = "";
                    break;

                case XmlPullParser.TEXT:
                    if (currentTag.equals("aid")) {
                        tmpAlbum.setAid(xmlPullParser.getText());
                    } else if (currentTag.equals("title")) {
                        tmpAlbum.setTitle(xmlPullParser.getText());
                    } else if (currentTag.equals("thumb_src")) {
                        tmpAlbum.setThumbScreenUrl(xmlPullParser.getText());
                    } else if (currentTag.equals("error_code")) {
                        throw new VkParsingErrorException(xmlPullParser.getText());
                    }
            }
        }
        return result;
    }
}


class VkParsingErrorException extends Exception {
    private String error;

    public String getError() {
        return error;
    }

    public VkParsingErrorException(String error) {
        super(error);
        this.error = error;
    }
}