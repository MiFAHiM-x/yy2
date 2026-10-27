package com.resultbrowser.app;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private TextView statusText;
    private String targetUrl;
    private String board;
    private String roll;
    private String reg;
    private boolean scriptInjected = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        targetUrl = getIntent().getStringExtra(MainActivity.EXTRA_URL);
        String serverName = getIntent().getStringExtra(MainActivity.EXTRA_SERVER_NAME);
        board = getIntent().getStringExtra(MainActivity.EXTRA_BOARD);
        roll = getIntent().getStringExtra(MainActivity.EXTRA_ROLL);
        reg = getIntent().getStringExtra(MainActivity.EXTRA_REG);

        if (board == null) board = "dinajpur";
        if (roll == null) roll = "";
        if (reg == null) reg = "";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(serverName != null ? serverName : "HSC26xFAHIM");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);
        webView = findViewById(R.id.webView);

        setupWebView();
        loadPage();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        settings.setUserAgentString(
                "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        );

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                statusText.setVisibility(View.VISIBLE);
                statusText.setText("লোড হচ্ছে...");
                scriptInjected = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                statusText.setText("অটোফিল চালু হচ্ছে...");

                if (!scriptInjected) {
                    injectAutofillScript();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadPage() {
        if (targetUrl != null && !targetUrl.isEmpty()) {
            webView.loadUrl(targetUrl);
        }
    }

    private void injectAutofillScript() {
        // Dynamic script with user values
        final String script =
                "(function(){" +
                "  function setVal(id, val){" +
                "    var el = document.getElementById(id);" +
                "    if(el){" +
                "      el.value = val;" +
                "      el.dispatchEvent(new Event('change', {bubbles:true}));" +
                "      el.dispatchEvent(new Event('input', {bubbles:true}));" +
                "    }" +
                "  }" +
                "  setVal('board', '" + board + "');" +
                "  setVal('exam', 'hsc');" +
                "  setVal('year', '2026');" +
                "  setVal('result_type', '1');" +
                "  setTimeout(function(){" +
                "    setVal('roll', '" + roll + "');" +
                "    setVal('reg', '" + reg + "');" +
                "    window.scrollTo({" +
                "      top: document.documentElement.scrollHeight," +
                "      behavior: 'smooth'" +
                "    });" +
                "  }, 600);" +
                "})();";

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(script, new android.webkit.ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        scriptInjected = true;
                        statusText.setText("অটোফিল সম্পন্ন ✓");
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                statusText.setVisibility(View.GONE);
                            }
                        }, 2500);
                    }
                });
            }
        }, 800);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
