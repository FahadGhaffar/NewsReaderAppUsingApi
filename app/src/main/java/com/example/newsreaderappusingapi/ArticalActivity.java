package com.example.newsreaderappusingapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artical);

        WebView webView=findViewById(R.id.webview);

        Intent intent=getIntent();
     webView.loadUrl(intent.getStringExtra("content"));
     Log.i("contentindex",intent.getStringExtra("content"));
    }
}
