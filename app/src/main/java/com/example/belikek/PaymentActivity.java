package com.example.belikek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.belikek.Constants.*;

public class PaymentActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        String paymentUrl = getIntent().getStringExtra(PAYMENT_URL_FIELD);
        setupWebView();
        webView.loadUrl(paymentUrl);

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Prepare the data to send back
                Intent resultIntent = new Intent();
                resultIntent.putExtra("status", "done");

                // Set the result
                setResult(RESULT_OK, resultIntent);

                // Finish the activity
                finish();
            }
        });
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);

                // Check for ToyyibPay completion URLs
                if (url.contains("toyyibpay.com") &&
                        (url.contains("status") || url.contains("receipt") ||
                                url.contains("transaction") || url.contains("result"))) {

                    // Wait a moment for page to fully load, then check status
                    new Handler().postDelayed(() -> {
//                        checkPaymentStatusAndClose();
                    }, 2000);
                }

                // Also check page title for completion indicators
                String title = view.getTitle();
                if (title != null && (title.contains("Success") || title.contains("Complete") ||
                        title.contains("Receipt") || title.contains("Transaction"))) {
                    new Handler().postDelayed(() -> {
//                        checkPaymentStatusAndClose();
                    }, 1000);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("PAYMENT_URL", "Loading: " + url);
                return false; // Let WebView handle all URLs
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("WEBVIEW_ERROR", "Error: " + error.getDescription());
            }
        });

        // Suppress console errors from bank websites
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                // Don't log bank website console errors
                return true;
            }
        });
    }
}