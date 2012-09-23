package name.ssf.vk_photo.photo;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

public class PhotosLoadTask extends AsyncTaskLoader<List<Photo>> {

    private String userId;
    private String token;
    private String albumId;

    private List<Photo> loadedPhotos;
    private MCallbacks mCallbacks;
    public interface MCallbacks {
        void loading();
        void connectionError();
    }

    public PhotosLoadTask(Context context, String userId, String token, String albumId) {
        super(context);
        this.mCallbacks = (MCallbacks) context;
        this.userId = userId;
        this.token = token;
        this.albumId = albumId;

    }

    @Override
    public List<Photo> loadInBackground() {
        mCallbacks.loading();
        return loadPhotos();
    }

    @Override
    protected void onStartLoading() {
        if (loadedPhotos != null) {
            deliverResult(loadedPhotos);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Photo> data) {
        loadedPhotos = data;
        super.deliverResult(data);
    }

    public List<Photo> loadPhotos() {
        List<Photo> result = new ArrayList<Photo>();
        HttpClient httpClient = new DefaultHttpClient();

        String requestUrl = "https://api.vk.com/method/" + "photos.get.xml" +
                "?uid=" +userId +
                "&access_token=" + token +
                "&aid=" + albumId;

        HttpPost request = new HttpPost(requestUrl);

        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent());
            try {
                List<Photo> parsed = parseXML(inputStreamReader);
                result.addAll(parsed);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        } catch (ClientProtocolException e) {
//            e.printStackTrace();
            mCallbacks.connectionError();
        } catch (IOException e) {
//            e.printStackTrace();
            mCallbacks.connectionError();
        }
        return result;
    }

    private List<Photo> parseXML(InputStreamReader inputStreamReader)
            throws XmlPullParserException, IOException {
        List<Photo> result = new ArrayList<Photo>();
        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();

        XmlPullParser xmlPullParser = parserFactory.newPullParser();
        xmlPullParser.setInput(inputStreamReader);

        String currentTag = "";
        Photo photo = new Photo();
        for (int eventType = xmlPullParser.getEventType();
             eventType != XmlPullParser.END_DOCUMENT;
             eventType = xmlPullParser.next()) {

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    currentTag = xmlPullParser.getName();
                    if (currentTag.equals("photo")) {
                        photo = new Photo();
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (xmlPullParser.getName().equals("photo")) {
                        result.add(photo);
                    }
                    currentTag = "";
                    break;

                case XmlPullParser.TEXT:
                    if (currentTag.equals("src")) {
                        photo.setThumbPhotoUrl(xmlPullParser.getText());
                    } else if (currentTag.equals("src_small") ||
                            currentTag.equals("src_big") ||
                            currentTag.equals("src_xbig") ||
                            currentTag.equals("src_xxbig")) {
                        photo.setFullPhotoUrl(xmlPullParser.getText());
                    } else if(currentTag.equals("error")) {
                        mCallbacks.connectionError();
                    }
            }
        }
        return result;
    }




}
