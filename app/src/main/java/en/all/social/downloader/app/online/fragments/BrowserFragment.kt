@file:Suppress("DEPRECATION")

package en.all.social.downloader.app.online.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.utils.Constants
import en.all.social.downloader.app.online.utils.Constants.TAGI
import en.all.social.downloader.app.online.utils.JavascriptNotation
import kotlinx.android.synthetic.main.fragment_browser.view.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory


class BrowserFragment(private val website: String) : BaseFragment() {
    fun newInstance(position: Int): BaseFragment {
        val f = BrowserFragment(website)
        val b = Bundle()
        b.putInt(Constants.ARG_POSITION, position)
        f.arguments = b
        return f
    }

    private var defaultSSLSF: SSLSocketFactory? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        // This callback will only be called when MyFragment is at least Started.

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    if (webview?.canGoBack()!!) {
                        webview?.goBack()
                    } else {
                        findNavController().navigate(R.id.nav_home)

                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_browser, container, false)
        defaultSSLSF = HttpsURLConnection.getDefaultSSLSocketFactory()
        webview = root!!.webView
        createWebView()
        CookieSyncManager.createInstance(requireActivity())
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        CookieSyncManager.getInstance().startSync()

        when {
            website.equals(getString(R.string.facebook_website)) -> {
                loadUrl(getString(R.string.facebook_website))
            }
            website.equals(getString(R.string.twitter_website)) -> {
                loadUrl(getString(R.string.twitter_website))
                root!!.fab.visibility = View.VISIBLE

            }
            website.equals(getString(R.string.linkedin_website)) -> {
                loadUrl(getString(R.string.linkedin_website))
                root!!.fab.visibility = View.VISIBLE

            }
            website.equals(getString(R.string.instagram_website)) -> {
                loadUrl(getString(R.string.instagram_website))
                root!!.fab.visibility = View.VISIBLE

            }
            website.equals(getString(R.string.tiktok_website)) -> {
                loadUrl(getString(R.string.tiktok_website))
                root!!.fab.visibility = View.VISIBLE

            }
        }
        return root
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun createWebView() {
        val progressBar: ProgressBar = root!!.progressBar
        val settings = webview!!.settings
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.allowUniversalAccessFromFileURLs = true

        settings.javaScriptEnabled = true
        settings.pluginState = WebSettings.PluginState.ON
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCacheEnabled(true)
        settings.domStorageEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        webview!!.addJavascriptInterface(this, "mJava")
        webview!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.progress = newProgress
                }
            }

        }
        webview!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?
            ) {
                progressBar.progress = 0
                progressBar.visibility = View.VISIBLE
                view.requestFocus()

            }

            override fun onPageFinished(view: WebView, url: String) {
                // Don't use the argument url here since navigation to that URL might have been
                // cancelled due to SSL error

                if (view.url.contains(getString(R.string.twitter_website))) {
                    if (isAdded) {
                        if (!SharedPrefUtils.getBooleanData(requireActivity(), "isTwitter")) {
//                        guideDialog(true)
                        }
                    }
                }
                if (view.url.contains(getString(R.string.facebook_website))) {
                    root!!.fab.visibility = View.GONE
                    val handler = Handler()
                    handler.postDelayed({
                        webview!!.loadUrl(JavascriptNotation.value)
                    }, 3000)
                    if (isAdded) {
                        if (!SharedPrefUtils.getBooleanData(requireActivity(), "isFacebook")) {
//                        guideDialog(false)
                        }
                    }
                }
            }


            override fun onLoadResource(view: WebView, url: String) {
                try {
                    val page = view.url
                    Log.d(TAGI, "url1: $page")
                    val title = view.title
                    if (page.contains(getString(R.string.facebook_website))) {
                        root!!.fab.visibility = View.GONE
                        val handler = Handler()
                        handler.postDelayed({
                            webview!!.loadUrl(JavascriptNotation.valueResource)
                            webview!!.loadUrl(JavascriptNotation.getValue)
                        }, 3000)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val sslErrors = arrayOf(
                "Not yet valid",
                "Expired",
                "Hostname mismatch",
                "Untrusted CA",
                "Invalid date",
                "Unknown error"
            )

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                val primaryError = error.primaryError
                val errorStr =
                    if (primaryError >= 0 && primaryError < sslErrors.size) sslErrors[primaryError] else "Unknown error $primaryError"
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Insecure connection")
                    .setMessage(
                        String.format(
                            "Error: %s\nURL: %s\n\nCertificate:\n%s",
                            errorStr,
                            error.url,
                            certificateToStr(error.certificate)
                        )
                    )
                    .setPositiveButton(
                        getString(R.string.proceed)
                    ) { dialog: DialogInterface?, which: Int -> handler.proceed() }
                    .setNegativeButton(
                        getString(R.string.cancel)
                    ) { dialog: DialogInterface?, which: Int -> handler.cancel() }
                    .show()
            }
        }

    }

    @SuppressLint("DefaultLocale")
    private fun certificateToStr(certificate: SslCertificate?): String? {
        if (certificate == null) {
            return null
        }
        var s = ""
        val issuedTo = certificate.issuedTo
        if (issuedTo != null) {
            s += """
                Issued to: ${issuedTo.dName}
                
                """.trimIndent()
        }
        val issuedBy = certificate.issuedBy
        if (issuedBy != null) {
            s += """
                Issued by: ${issuedBy.dName}
                
                """.trimIndent()
        }
        val issueDate = certificate.validNotBeforeDate
        if (issueDate != null) {
            s += String.format("Issued on: %tF %tT %tz\n", issueDate, issueDate, issueDate)
        }
        val expiryDate = certificate.validNotAfterDate
        if (expiryDate != null) {
            s += String.format(
                "Expires on: %tF %tT %tz\n",
                expiryDate,
                expiryDate,
                expiryDate
            )
        }
        return s
    }

    private fun loadUrl(url1: String) {
        var url = url1
        url = url.trim { it <= ' ' }
        if (url.isEmpty()) {
            url = "about:blank"
        }
        url =
            if (url.startsWith("about:") || url.startsWith("javascript:") || url.startsWith("file:") || url.startsWith(
                    "data:"
                ) ||
                url.indexOf(' ') == -1 && Patterns.WEB_URL.matcher(url).matches()
            ) {
                val indexOfHash = url.indexOf('#')
                val guess = URLUtil.guessUrl(url)
                if (indexOfHash != -1 && guess.indexOf('#') == -1) {
                    // Hash exists in original URL but no hash in guessed URL
                    guess + url.substring(indexOfHash)
                } else {
                    guess
                }
            } else {
                URLUtil.composeSearchUrl(url, website, "%s")
            }
        webview?.loadUrl(url)
    }


    @JavascriptInterface
    fun getData(pathvideo: String) {
        Log.d(TAGI, pathvideo)
    }
}