@file:Suppress("DEPRECATION", "UNUSED_ANONYMOUS_PARAMETER")

package en.all.social.downloader.app.online.fragments

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.*
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.webkit.*
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.find.lost.app.phone.utils.InternetConnection
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.htetznaing.lowcostvideo.LowCostVideo
import com.htetznaing.lowcostvideo.Model.XModel
import com.video.downloading.app.downloader.online.app.utils.PermissionsUtils
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.activities.FbVideoWatchActivity
import en.all.social.downloader.app.online.models.VideoDownload
import en.all.social.downloader.app.online.utils.*
import en.all.social.downloader.app.online.utils.Constants.FB_FOLDER
import en.all.social.downloader.app.online.utils.Constants.INSTAGRAM_FOLDER
import en.all.social.downloader.app.online.utils.Constants.LINKEDIN_FOLDER
import en.all.social.downloader.app.online.utils.Constants.TAGI
import en.all.social.downloader.app.online.utils.Constants.TIKTOK_FOLDER
import en.all.social.downloader.app.online.utils.Constants.TWITTER_FOLDER
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

    private var anim: Animation? = null
    private var videoDownloadList: ArrayList<VideoDownload>? = null

    private var twitterLink: String? = null
    private var twitterLink1: String? = null
    private var tiktokLink: String? = null
    private var tiktokLink1: String? = null
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
        videoDownloadList = ArrayList()

        createWebView()
        CookieSyncManager.createInstance(requireActivity())
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        CookieSyncManager.getInstance().startSync()

        when (website) {
            getString(R.string.facebook_website) -> {
                loadUrl(getString(R.string.facebook_website))
                root!!.fab.visibility = View.GONE

            }
            getString(R.string.twitter_website) -> {
                loadUrl(getString(R.string.twitter_website))
                root!!.fab.visibility = View.GONE

            }
            getString(R.string.linkedin_website) -> {
                loadUrl(getString(R.string.linkedin_website))
                root!!.fab.visibility = View.VISIBLE

            }
            getString(R.string.instagram_website) -> {
                loadUrl(getString(R.string.instagram_website))
                root!!.fab.visibility = View.VISIBLE

            }
            getString(R.string.tiktok_website) -> {
                loadUrl(getString(R.string.tiktok_website))
                root!!.fab.visibility = View.GONE

            }
        }
        anim = AlphaAnimation(0.0f, 1.0f)
        (anim as AlphaAnimation).duration =
            1000 //You can manage the blinking time with this parameter

        (anim as AlphaAnimation).startOffset = 20
        (anim as AlphaAnimation).repeatMode = Animation.REVERSE
        (anim as AlphaAnimation).repeatCount = Animation.INFINITE

        root!!.fab.setOnClickListener {
            if (InternetConnection().checkConnection(requireActivity())) {

                when {
                    videoDownloadList.isNullOrEmpty() -> {
                        root!!.fab.clearAnimation()
                        noResourceDialog()
                    }
                    webview!!.url.contains(getString(R.string.twitter_website)) -> {
                        val split: Array<String> =
                            twitterLink!!.split("\\?".toRegex()).toTypedArray()
                        showDialog(getString(R.string.generate_download_link))
                        xGetter!!.find(split[0])
                    }
                    webview!!.url.contains(getString(R.string.instagram_website)) -> {
                        downloadVideo()
                    }
                    webview!!.url.contains(getString(R.string.linkedin_website)) -> {
                        downloadVideo()
                    }
                    webview!!.url.contains(getString(R.string.tiktok_website)) -> {

                        val tikTokDownloader =
                            TikTokDownloader(tiktokLink.toString(), requireActivity())
                        tikTokDownloader.execute()
                        tikTokDownloader.setOnTikTokListener(object : TikTokLinkListener {
                            override fun onResponseReceive(data: String?) {
                                tiktokLink1 = data
                                downloadVideo()

                            }
                        })
                    }
                }
            } else {
                showToast(getString(R.string.no_internet))
            }
        }

        xGetter = LowCostVideo(requireActivity())
        xGetter!!.onFinish(object : LowCostVideo.OnTaskCompleted {
            override fun onTaskCompleted(
                vidURL: ArrayList<XModel>,
                multiple_quality: Boolean
            ) {
                hideDialog()
                if (multiple_quality) {
                    twitterLink1 = vidURL[0].url
                    downloadVideo()
                }
            }

            override fun onError() {
                //Error
                hideDialog()
            }
        })
        return root
    }

    private fun noResourceDialog() {
        val builder =
            MaterialStyledDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.no_video))
            .setDescription(getString(R.string.video_resource))
            .setStyle(Style.HEADER_WITH_ICON)
            .setIcon(R.drawable.ic_baseline_videocam_off_24)
            .setCancelable(false)
            .withDialogAnimation(true)
            .setPositiveText(getString(R.string.yes))
            .onPositive { dialog, which -> dialog.dismiss() }

        val dialog = builder.build()
        dialog.show()
    }

    private fun downloadVideo() {
        val builder =
            MaterialStyledDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.congrats))
            .setDescription(getString(R.string.download_this_video))
            .setStyle(Style.HEADER_WITH_ICON)
            .setIcon(R.drawable.ic_baseline_arrow_downward_24)
            .withDialogAnimation(true)
            .setPositiveText(getString(R.string.yes))
            .onPositive { dialog, which -> createDownloadStream() }
            .setNegativeText(getString(R.string.no))
            .onNegative { dialog, which -> dialog.dismiss() }
        val dialog = builder.build()
        dialog.show()
    }

    //TODO: createDownloadStream
    private fun createDownloadStream() {
        var linked = ""
        for (video in videoDownloadList!!) {
            linked = video.link
        }
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionsUtils =
                PermissionsUtils().getInstance(requireActivity())
            if (permissionsUtils != null) {
                if (permissionsUtils.isAllPermissionAvailable()) {
                    when {
                        webview!!.url.contains(getString(R.string.twitter_website)) -> {
                            startDownload(twitterLink1, "Twitter_$rnds", TWITTER_FOLDER)
                        }
                        webview!!.url.contains(getString(R.string.instagram_website)) -> {
                            startDownload(linked, "Instagram_$rnds", INSTAGRAM_FOLDER)
                        }
                        webview!!.url.contains(getString(R.string.linkedin_website)) -> {
                            startDownload(linked, "Linkedin_$rnds", LINKEDIN_FOLDER)
                        }
                        webview!!.url.contains(getString(R.string.tiktok_website)) -> {
                            startDownload(tiktokLink1, "TIKTOK_$rnds", TIKTOK_FOLDER)
                        }
                    }
                    Log.d(TAGI, "permission accepted")
                } else {
                    permissionsUtils.setActivity(requireActivity())
                    permissionsUtils.requestPermissionsIfDenied()
                }
            }
        } else {
            when {
                webview!!.url.contains(getString(R.string.twitter_website)) -> {
                    startDownload(twitterLink1, "Twitter_$rnds", TWITTER_FOLDER)
                }
                webview!!.url.contains(getString(R.string.instagram_website)) -> {
                    startDownload(linked, "Instagram_$rnds", INSTAGRAM_FOLDER)
                }
                webview!!.url.contains(getString(R.string.linkedin_website)) -> {
                    startDownload(linked, "Linkedin_$rnds", LINKEDIN_FOLDER)
                }
                webview!!.url.contains(getString(R.string.tiktok_website)) -> {
                    startDownload(tiktokLink1, "TikTok_$rnds", TIKTOK_FOLDER)
                }
            }
        }

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

                try {
                    if (view.url.contains(getString(R.string.twitter_website))) {
                        if (isAdded) {
                            if (!SharedPrefUtils.getBooleanData(requireActivity(), "isTwitter")) {
                                guideDialog(isTwitter = true, isFacebook = false)
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
                                guideDialog(isTwitter = false, isFacebook = true)
                            }
                        }
                    }
                    if (view.url.contains(getString(R.string.tiktok_website))) {
                        if (isAdded) {
                            if (!SharedPrefUtils.getBooleanData(requireActivity(), "isTikTok")) {
                                guideDialog(isTwitter = false, isFacebook = false)
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
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
                    } else {
                        object : VideoContentSearch(requireActivity(), url, page, title) {
                            override fun onStartInspectingURL() {
                                Handler(Looper.getMainLooper()).post {
                                    Log.d(TAGI, "onStartInspectingURL")
                                }
                            }

                            override fun onFinishedInspectingURL(finishedAll: Boolean) {
                                HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSF)
                                if (finishedAll) {
                                    Handler(Looper.getMainLooper()).post {
                                        Log.d(TAGI, "onFinishedInspectingURL")
                                    }
                                }
                            }

                            override fun onVideoFound(
                                size: String?,
                                type: String?,
                                link: String?,
                                name: String?,
                                page: String?,
                                chunked: Boolean,
                                website: String?
                            ) {
                                try {
                                    requireActivity()
                                        .runOnUiThread {

                                            if (page != null) {
                                                when {
                                                    page.contains(getString(R.string.twitter_website)) -> {

                                                        if (isAdded) {
                                                            clipTweet()
                                                        }
                                                    }
                                                    page.contains(getString(R.string.tiktok_website)) -> {

                                                        if (isAdded) {
                                                            clipTweet()
                                                        }
                                                    }
                                                    else -> {
                                                        root!!.fab.clearAnimation()
                                                        root!!.fab.startAnimation(anim)
                                                    }
                                                }
                                            }
                                        }
                                    videoDownloadList!!.clear()
                                    Log.d(
                                        TAGI,
                                        "onVideoFound: $size,$type,$link,$name,$page,$chunked,$website"
                                    )
                                    videoDownloadList!!.add(
                                        VideoDownload(
                                            size.toString(),
                                            type.toString(),
                                            link.toString(),
                                            name.toString(),
                                            page.toString(),
                                            chunked,
                                            website.toString()
                                        )
                                    )


                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                        }.start()
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

    //TODO: clip tweet
    private fun clipTweet() {
        val clipBoard =
            requireActivity().getSystemService(
                Context.CLIPBOARD_SERVICE
            ) as ClipboardManager
        clipBoard.addPrimaryClipChangedListener {
            val clipData = clipBoard.primaryClip
            val item = clipData!!.getItemAt(0)
            val text = item.text.toString()
            Log.d(TAGI, "twitter: $text")
            if (text.contains("https://twitter.com/")) {
                twitterLink = text
                fabAnim()
            } else if (text.contains("tiktok.com/")) {
                tiktokLink = text
                fabAnim()
            }
        }
    }

    private fun fabAnim() {
        root!!.fab.visibility =
            View.VISIBLE
        root!!.fab.clearAnimation()
        root!!.fab.startAnimation(anim)
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
        var finalurl: String = pathvideo
        finalurl = finalurl.replace("%3A".toRegex(), ":")
        finalurl = finalurl.replace("%2F".toRegex(), "/")
        finalurl = finalurl.replace("%3F".toRegex(), "?")
        finalurl = finalurl.replace("%3D".toRegex(), "=")
        finalurl = finalurl.replace("%26".toRegex(), "&")
        val finalUrl = finalurl

        val builder =
            MaterialStyledDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.congrats))
            .setDescription(getString(R.string.download_this_video))
            .setStyle(Style.HEADER_WITH_ICON)
            .setIcon(R.drawable.ic_baseline_arrow_downward_24)
            .withDialogAnimation(true)
            .setPositiveText(getString(R.string.yes))
            .onPositive { dialog, which ->
                webview!!.post {
                    if (InternetConnection().checkConnection(requireActivity())) {
                        startDownload(finalUrl, "Facebook_$rnds", FB_FOLDER)
                    } else {
                        showToast(getString(R.string.no_internet))
                    }
                }
            }
            .setNeutralText(getString(R.string.copy))
            .onNeutral { dialog, which ->
                requireActivity().runOnUiThread {
                    copyUrlToClip(finalUrl)
                }
            }
            .setNegativeText(getString(R.string.watch))
            .onNegative { dialog, which ->
                if (InternetConnection().checkConnection(requireActivity())) {
                    val intent = Intent(activity, FbVideoWatchActivity::class.java)
                    intent.putExtra("videoUrl", finalurl)
                    startActivity(intent)
                } else {
                    showToast(getString(R.string.no_internet))
                }
            }
        val dialog = builder.build()
        dialog.show()
    }

    private fun copyUrlToClip(url: String) {
        val clipboard = requireActivity()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip =
            ClipData.newPlainText(webview!!.title, url)
        clipboard.setPrimaryClip(clip)
        showToast(getString(R.string.link_copied))
    }
}
