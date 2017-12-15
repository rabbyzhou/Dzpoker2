package com.yijian.dzpoker.activity.news;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.view.data.MainPageNews;

public class DetailNewsActivity extends BaseBackActivity {

    private final String TAG = "DetailNewsActivity";
    private WebView mWebView;
    private ProgressDialog mProgressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_news;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("请稍后....");
        mProgressDialog.setMessage("加载中....");

        Intent intent = getIntent();
        MainPageNews news = (MainPageNews)intent.getSerializableExtra("news");

//        TextView backText = (TextView) findViewById(R.id.tv_back);
//        backText.setText("首页");
//        backText.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

//        TextView titleText = (TextView) findViewById(R.id.tv_title);
//        titleText.setText(news.title);
        setToolbarTitle(news.title);
        mWebView = (WebView) findViewById(R.id.webView);
        Log.d(TAG, "link :" + news.link);
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setAppCacheEnabled(true);
        final String cachePath = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        ws.setAppCachePath(cachePath);

        ws.setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressDialog.hide();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.loadUrl(news.link);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            if (mWebView.canGoBack()){
                mWebView.goBack();
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    public void onClick(View v) {

    }
}
