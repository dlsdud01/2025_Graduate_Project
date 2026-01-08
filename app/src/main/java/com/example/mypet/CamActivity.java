package com.example.mypet;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class CamActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        // ✅ WebView 설정
        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);               // JS 허용
        webSettings.setAllowContentAccess(true);              // 로컬 콘텐츠 접근 허용
        webSettings.setLoadWithOverviewMode(true);            // 전체 화면 맞춤
        webSettings.setUseWideViewPort(true);                 // 뷰포트 설정
        webSettings.setDomStorageEnabled(true);               // DOM 스토리지 사용

        // ✅ MJPEG 스트림 로드
        webView.loadUrl("http://192.168.137.6:8080");
    }
}
