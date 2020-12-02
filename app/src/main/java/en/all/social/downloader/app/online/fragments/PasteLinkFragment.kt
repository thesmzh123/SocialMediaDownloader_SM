@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package en.all.social.downloader.app.online.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.fragment.findNavController
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ashudevs.facebookurlextractor.FacebookExtractor
import com.ashudevs.facebookurlextractor.FacebookFile
import en.all.social.downloader.app.online.utils.InternetConnection
import en.all.social.downloader.app.online.utils.SharedPrefUtils
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.google.android.gms.ads.AdListener
import com.google.android.material.textfield.TextInputEditText
import com.htetznaing.lowcostvideo.LowCostVideo
import com.htetznaing.lowcostvideo.Model.XModel
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.utils.Constants
import en.all.social.downloader.app.online.utils.Constants.FB_FOLDER
import en.all.social.downloader.app.online.utils.Constants.INSTAGRAM_FOLDER
import en.all.social.downloader.app.online.utils.Constants.INSTA_LINK
import en.all.social.downloader.app.online.utils.Constants.QUERY
import en.all.social.downloader.app.online.utils.Constants.TAGI
import en.all.social.downloader.app.online.utils.Constants.TIKTOK_FOLDER
import en.all.social.downloader.app.online.utils.Constants.TWITTER_FOLDER
import en.all.social.downloader.app.online.utils.TikTokDownloader
import en.all.social.downloader.app.online.utils.TikTokLinkListener
import kotlinx.android.synthetic.main.banner.view.*
import kotlinx.android.synthetic.main.fragment_paste_link.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


open class PasteLinkFragment(private val website: String) : BaseFragment() {
    open fun newInstance(position: Int): BaseFragment {
        val f = PasteLinkFragment(website)
        val b = Bundle()
        b.putInt(Constants.ARG_POSITION, position)
        f.arguments = b
        return f
    }

    private var urlText: TextInputEditText? = null


    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_paste_link, container, false)
        urlText = root!!.videoUrl
        controlView()

        //fb video link
        root!!.fbBtn.setOnClickListener {
            fbVideoLink()
        }
        //twitter video link
        root!!.twitterBtn.setOnClickListener {
            twitterVideoLink()
        }

        //instagram video link
        root!!.instagramBtn.setOnClickListener {
            if (checkEditText()) {
                showToast(getString(R.string.fill_the_field))
            } else {
                if (InternetConnection().checkConnection(requireActivity())) {
                    when {
                        checkUrl() -> {
                            showToast(getString(R.string.valid_url))
                        }
                        urlText!!.text.toString().contains("https://www.instagram.com") -> {
                            instagramLink()
                        }
                        else -> {
                            showToast(getString(R.string.enter_instagram_url))
                        }
                    }
                } else {
                    showToast(getString(R.string.no_internet))

                }
            }
        }

        //tiktok video link
        root!!.tiktokBtn.setOnClickListener {
            tiktokVideoLink()
        }
        xGetter = LowCostVideo(requireActivity())
        xGetter!!.onFinish(object : LowCostVideo.OnTaskCompleted {
            override fun onTaskCompleted(
                vidURL: ArrayList<XModel>,
                multiple_quality: Boolean
            ) {
                hideDialog()
                if (multiple_quality) {
                    if (urlText!!.text.toString().contains("https://twitter.com")) {
                        downloadVideo("Twitter_" + rnds.toString(), vidURL[0].url, TWITTER_FOLDER)
                    }
                }
            }

            override fun onError() {
                //Error
                hideDialog()
            }
        })
        loadInterstial()
        adView(root!!.adView)
        return root
    }

    private fun tiktokVideoLink() {
        if (checkEditText()) {
            showToast(getString(R.string.fill_the_field))
        } else {
            if (InternetConnection().checkConnection(requireActivity())) {
                when {
                    checkUrl() -> {
                        showToast(getString(R.string.valid_url))
                    }
                    urlText!!.text.toString()
                        .contains(getString(R.string.tiktok_website)) || urlText!!.text.toString()
                        .contains("https://vm.tiktok.com/") -> {
                        val tikTokDownloader =
                            TikTokDownloader(urlText!!.text.toString(), requireActivity())
                        tikTokDownloader.execute()
                        tikTokDownloader.setOnTikTokListener(object : TikTokLinkListener {
                            override fun onResponseReceive(data: String?) {
                                downloadVideo("TikTok_$rnds", data.toString(), TIKTOK_FOLDER)

                            }
                        })
                    }
                    else -> {
                        showToast(getString(R.string.enter_tiktok_url))
                    }

                }
            } else {
                showToast(getString(R.string.no_internet))

            }
        }
    }

    private fun twitterVideoLink() {
        if (checkEditText()) {
            showToast(getString(R.string.fill_the_field))
        } else {
            if (InternetConnection().checkConnection(requireActivity())) {
                when {
                    checkUrl() -> {
                        showToast(getString(R.string.valid_url))
                    }
                    urlText!!.text.toString().contains("https://twitter.com") -> {
                        val split: Array<String> =
                            urlText!!.text.toString().split("\\?".toRegex()).toTypedArray()
                        showDialog(getString(R.string.generate_download_link))
                        xGetter!!.find(split[0])
                    }
                    else -> {
                        showToast(getString(R.string.enter_twitter_url))
                    }
                }
            } else {
                showToast(getString(R.string.no_internet))

            }
        }
    }

    private fun fbVideoLink() {
        if (checkEditText()) {
            showToast(getString(R.string.fill_the_field))
        } else {
            if (InternetConnection().checkConnection(requireActivity())) {
                when {
                    checkUrl() -> {
                        showToast(getString(R.string.valid_url))
                    }
                    urlText!!.text.toString().contains("https://video.f") -> {
                        val handler = Handler()
                        showDialog(getString(R.string.generate_download_link))
                        handler.postDelayed({
                            hideDialog()
                            downloadVideo(
                                "Facebook_$rnds",
                                urlText!!.text.toString(),
                                FB_FOLDER
                            )
                        }, 3000)
                    }
                    urlText!!.text.toString().contains("https://www.facebook.com/") -> {
                        extractFbDownloadLink()
                    }
                    else -> {
                        showToast(getString(R.string.enter_fb_url))
                    }
                }
            } else {
                showToast(getString(R.string.no_internet))

            }
        }
    }

    private fun controlView() {
        when (website) {
            getString(R.string.facebook_website) -> {
                changeTextAndButtonColor(
                    getString(R.string.fb_download_text)
                )
                root!!.fbBtn.visibility = View.VISIBLE
            }
            getString(R.string.twitter_website) -> {
                changeTextAndButtonColor(
                    getString(R.string.twitter_download_text)
                )
                root!!.twitterBtn.visibility = View.VISIBLE

            }
            getString(R.string.linkedin_website) -> {
                changeTextAndButtonColor(
                    getString(R.string.linkedin_download_text)
                )
                root!!.linkedinBtn.visibility = View.VISIBLE

            }
            getString(R.string.instagram_website) -> {
                changeTextAndButtonColor(
                    getString(R.string.insta_download_text)
                )
                root!!.instagramBtn.visibility = View.VISIBLE

            }
            getString(R.string.tiktok_website) -> {

                changeTextAndButtonColor(
                    getString(R.string.tiktok_download_text)
                )
                root!!.tiktokBtn.visibility = View.VISIBLE

            }
        }
    }

    private fun changeTextAndButtonColor(s: String) {
        root!!.textDownload.text = s

    }

    private fun checkEditText(): Boolean {
        if (urlText!!.text!!.isEmpty()) {
            showToast(getString(R.string.fill_the_field))
            return true
        }
        return false
    }

    private fun checkUrl(): Boolean {
        if (!URLUtil.isValidUrl(urlText!!.text.toString())) {
            return true
        }
        return false
    }

    private fun downloadVideo(name: String, url: String, folderName: String) {
        val builder =
            MaterialStyledDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.congrats))
            .setDescription(getString(R.string.download_this_video))
            .setStyle(Style.HEADER_WITH_ICON)
            .setIcon(R.drawable.ic_baseline_arrow_downward_24)
            .withDialogAnimation(true)
            .setCancelable(false)
            .setPositiveText(getString(R.string.yes))
            .onPositive { dialog, which ->
                if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                    hideDialog()
                    if (interstitial.isLoaded) {
                        if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
                                Lifecycle.State.STARTED
                            )
                        ) {
                            interstitial.show()
                        } else {
                            Log.d(TAGI, "App Is In Background Ad Is Not Going To Show")

                        }
                    } else {
                        startDownload(url, name, folderName)
                        urlText!!.text?.clear()
                    }
                    interstitial.adListener = object : AdListener() {
                        override fun onAdClosed() {
                            requestNewInterstitial()
                            startDownload(url, name, folderName)
                            urlText!!.text?.clear()
                        }
                    }
                } else {
                    startDownload(url, name, folderName)
                    urlText!!.text?.clear()

                }


            }
            .setNegativeText(getString(R.string.no))
            .onNegative { dialog, which ->
                dialog.dismiss()
                urlText!!.text?.clear()
            }
        val dialog = builder.build()
        dialog.show()
    }

    @SuppressLint("StaticFieldLeak")
    private fun extractFbDownloadLink() {
        showDialog(getString(R.string.generate_download_link))
        object : FacebookExtractor() {
            override fun onExtractionComplete(FbFile: FacebookFile) {
                hideDialog()
                downloadVideo("Facebook-$rnds", FbFile.url, FB_FOLDER)
            }

            override fun onExtractionFail(Error: String) {
                //Fail
                hideDialog()
                Log.d(TAGI, "fb error: $Error")
            }
        }.Extractor(activity, urlText!!.text.toString())
    }

    private fun instagramLink() {
        showDialog(getString(R.string.generate_download_link))
        val splitter: Array<String> =
            urlText!!.text.toString().split("/".toRegex()).toTypedArray()
        val pathvideo = INSTA_LINK + splitter[4] + "/" + QUERY
        val requestQueue =
            Volley.newRequestQueue(requireActivity())
        val stringRequest =
            StringRequest(
                Request.Method.GET, pathvideo,
                Response.Listener { response: String? ->
                    Log.d(TAGI, response!!)
                    try {
                        val jsonObject2 = JSONObject(response)
                        Log.d(
                            TAGI,
                            "onResponse: " + jsonObject2.getString("graphql")
                        )
                        val respone1 = jsonObject2.getString("graphql")
                        val `object` = JSONObject(respone1)
                        parseJson(`object`)
                        hideDialog()
                    } catch (e: Exception) {
                        hideDialog()
                        e.printStackTrace()
                        Log.d(TAGI, "onResponse: " + e.message)
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideDialog()
                    error.printStackTrace()
                    Log.d(TAGI, "onErrorResponse: " + error.message)
                }
            )


        val socketTimeout = 10000
        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = policy
        requestQueue.add(stringRequest)
    }

    @Throws(JSONException::class)
    private fun parseJson(data: JSONObject?) {
        try {
            if (data != null) {
                val it = data.keys()
                while (it.hasNext()) {
                    val key = it.next()
                    try {
                        when {
                            data[key] is JSONArray -> {
                                val arry = data.getJSONArray(key)
                                val size = arry.length()
                                for (i in 0 until size) {
                                    parseJson(arry.getJSONObject(i))
                                }
                            }
                            data[key] is JSONObject -> {
                                parseJson(data.getJSONObject(key))
                            }
                            else -> {
                                println("" + key + " : " + data.optString(key))


                            }
                        }
                    } catch (e: Throwable) {
                        println("" + key + " : " + data.optString(key))
                        e.printStackTrace()
                    }
                }
                Log.d(TAGI, "parseJson: " + data.getString("video_url"))
                downloadVideo("Instagram_$rnds", data.getString("video_url"), INSTAGRAM_FOLDER)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}