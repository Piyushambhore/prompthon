package com.jansaarthi.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout

    companion object {
        private const val PERMISSION_REQUEST_RECORD_AUDIO = 101
        private const val LOCAL_ASSET_URL = "file:///android_asset/index.html"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        swipeRefresh = findViewById(R.id.swipeRefreshLayout)

        requestMicrophonePermission()
        setupWebView()
        setupBackNavigation()
        setupPullToRefresh()

        webView.loadUrl(LOCAL_ASSET_URL)
    }

    private fun requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_RECORD_AUDIO
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(false)
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                android.util.Log.d("JanSaarthiWebView", "Page loaded: $url")
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                android.util.Log.e("JanSaarthiWebView", "Error loading ${request?.url}: ${error?.description}")
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false

                // If clicking an external government portal, open in external Android browser
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    if (!url.contains("localhost:3000") && !url.contains("127.0.0.1")) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                            return true
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "Could not open external link", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                val msg = consoleMessage?.message() ?: ""
                val src = consoleMessage?.sourceId() ?: ""
                val line = consoleMessage?.lineNumber() ?: 0
                android.util.Log.d("JanSaarthiJS", "[$src:$line] $msg")
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.let {
                    val resources = it.resources
                    for (r in resources) {
                        if (r == PermissionRequest.RESOURCE_AUDIO_CAPTURE) {
                            it.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                            return
                        }
                    }
                    it.grant(it.resources)
                }
            }
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupPullToRefresh() {
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.primary)
        )
        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
