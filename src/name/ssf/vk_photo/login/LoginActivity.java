package name.ssf.vk_photo.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import name.ssf.vk_photo.album.AlbumsActivity;
import name.ssf.vk_photo.R;


//To Do
//On Cancel
//Connection problems

public class LoginActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public final static String EXTRA_TOKEN = "TOKEN";
    public final static String EXTRA_USER_ID = "USER_ID";

    public final static String CLIENT_ID = "3130114";
    private final static String SCOPE = "photos";
    private final static String REDIRECT_URI = "http://oauth.vk.com/blank.html";
    private final static String DISPLAY = "touch";
    private final static String RESPONSE_TYPE = "token";

    private final static String loginUrl = "http://api.vkontakte.ru/oauth/authorize?" +
            "client_id=" + CLIENT_ID +
            "&scope=" + SCOPE +
            "&redirect_uri=" + REDIRECT_URI +
            "&display=" + DISPLAY +
            "&response_type=" + RESPONSE_TYPE;

    private String userId;
    private String authenticationToken;
    private Integer tokenExpiresInSeconds;
    private Long tokenReceivedTime;
    private WebView webView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
    }

    private void init() {
        webView = (WebView) findViewById(R.id.login_web_view);
        webView.getSettings().
                setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith(REDIRECT_URI)) {
                    if(!url.contains("?error")) {
                        url = url.replace(REDIRECT_URI, "");
                        String params[] = url.split("[&#]\\w+=");

                        authenticationToken = params[1];
                        tokenExpiresInSeconds = Integer.parseInt(params[2]);
                        tokenReceivedTime = System.currentTimeMillis();
                        userId = params[3];

                        Intent albumsIntent = new Intent(LoginActivity.this, AlbumsActivity.class);
                        albumsIntent.putExtra(EXTRA_USER_ID, userId);
                        albumsIntent.putExtra(EXTRA_TOKEN, authenticationToken);
                        startActivity(albumsIntent);
                    } else {
                        url = url.replace(REDIRECT_URI, "");
                        String params[] = url.split("[&#]\\w+=");

                        String error = params[0];
                        String errorDescription = params[1];

                        Log.i("Vk auth error", error);
                        Toast.makeText(LoginActivity.this, errorDescription, Toast.LENGTH_LONG).show();
                        webView.loadUrl(loginUrl);
                    }
                }
            }
        });
        webView.loadUrl(loginUrl);
    }



}
