package com.example.belikek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        String paymentUrl = getIntent().getStringExtra("payment_url");
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
                Log.d("PAYMENT_URL", "Page finished: " + url);

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
                    Log.d("PAYMENT_PAGE", "Completion page detected by title: " + title);
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


    private void handlePaymentResult(String url) {
        // Parse the return URL to get payment status
        Uri uri = Uri.parse(url);

        // ToyyibPay typically returns these parameters:
        String billCode = uri.getQueryParameter("billcode");
        String orderNumber = uri.getQueryParameter("order_id");
        String status = uri.getQueryParameter("status_id");
        String transactionId = uri.getQueryParameter("transaction_id");
        String amount = uri.getQueryParameter("amount");

        Log.d("PAYMENT_RESULT", "URL: " + url);
        Log.d("PAYMENT_RESULT", "Bill Code: " + billCode);
        Log.d("PAYMENT_RESULT", "Status: " + status);
        Log.d("PAYMENT_RESULT", "Transaction ID: " + transactionId);

        // Status meanings (usually):
        // 1 = Success
        // 2 = Pending
        // 3 = Failed

        if ("1".equals(status)) {
            // Payment successful
            showPaymentSuccess(transactionId, amount);
        } else if ("2".equals(status)) {
            // Payment pending
            showPaymentPending();
        } else {
            // Payment failed
            showPaymentFailed();
        }
    }

    private void showPaymentSuccess(String transactionId, String amount) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_status", "success");
        resultIntent.putExtra("transaction_id", transactionId);
        resultIntent.putExtra("amount", amount);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showPaymentPending() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_status", "pending");
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Payment Pending", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showPaymentFailed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_status", "failed");
        setResult(RESULT_CANCELED, resultIntent);

        Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show();
        finish();
    }
}