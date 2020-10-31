package com.canada.cardelar.application;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class UrlLoader extends AppCompatActivity {
WebView webView;
ImageView imageViewBackArrow;
String Url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_loader);
        Intent intent=getIntent();
        Url=intent.getStringExtra("url");
        imageViewBackArrow=findViewById(R.id.backArrow);
        initWebLoader();
        imageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initWebLoader() {
        webView =findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        final ProgressDialog progDailog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        progDailog.setCancelable(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
                showToast("if the page is still blank please wait Device Writing Data On UI");
            }
        });
        webView.loadUrl(Url);
    }
    private void showToast(String s) {
        Toast.makeText(getApplicationContext(),
                s,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            finish();
        }
    }
}