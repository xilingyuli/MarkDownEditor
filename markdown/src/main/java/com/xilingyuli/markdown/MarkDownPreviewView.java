package com.xilingyuli.markdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by xilingyuli on 2017/2/28.
 */

public class MarkDownPreviewView extends WebView {

    private String markdownString = "";

    public MarkDownPreviewView(Context context) {
        super(context);
        init();
    }

    public MarkDownPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkDownPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MarkDownPreviewView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressWarnings("deprecation")
    public MarkDownPreviewView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init()
    {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setUseWideViewPort(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadUrl("javascript:parseMarkdown(\"" + markdownString.replace("\n", "\\n").replace("\"", "\\\"").replace("'", "\\'") + "\")");
            }
        });
        preview("");
    }

    public void preview(String markdownString)
    {
        this.markdownString = markdownString;
        loadUrl("file:///android_asset/markdown.html");
    }
}
