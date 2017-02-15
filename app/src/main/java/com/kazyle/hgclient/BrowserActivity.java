package com.kazyle.hgclient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by Kazyle on 2017/2/14.
 */
public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private EditText url;
    private Button visitUrlBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_layout);
        webView = (WebView) findViewById(R.id.webview);
        visitUrlBtn = (Button) findViewById(R.id.visitUrlBtn);
        visitUrlBtn.setOnClickListener(this);
        webView.setWebViewClient(new WebViewClient() {
            // Load opened URL in the application instead of standard browser
            // application
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            // Set progress bar during loading
            public void onProgressChanged(WebView view, int progress) {
                System.err.println("Progress: " + progress);
                BrowserActivity.this.setProgress(progress * 100);
            }
        });

        // Enable some feature like Javascript and pinch zoom
        WebSettings websettings = webView.getSettings();
        websettings.setJavaScriptEnabled(true);						// Warning! You can have XSS vulnerabilities!
        websettings.setBuiltInZoomControls(true);
        websettings.setJavaScriptCanOpenWindowsAutomatically(true);
        websettings.setSupportZoom(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setUseWideViewPort(true);
        websettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        websettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        websettings.setLoadWithOverviewMode(true);
        websettings.setAppCacheEnabled(true);
        websettings.setDomStorageEnabled(true);
        System.err.println("UserAgent: " + websettings.getUserAgentString());
//        websettings.setUserAgent(websettings.getUserAgentString() + " MicroMessenger/6.5.4.1000 NetType/WIFI Language/zh_CN");

        url = (EditText) findViewById(R.id.url);
        url.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            webView.loadUrl(url.getText().toString());
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(
                                    url.getWindowToken(), 0);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.visitUrlBtn:
                String target = url.getText().toString();
                if (target.trim().length() > 0) {
                    webView.loadUrl(url.getText().toString());
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            url.getWindowToken(), 0);
                }
                break;
        }
    }
}
