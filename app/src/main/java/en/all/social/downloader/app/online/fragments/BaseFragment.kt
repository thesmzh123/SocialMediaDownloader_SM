@file:Suppress("DEPRECATION")

package en.all.social.downloader.app.online.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.fragment.findNavController
import en.all.social.downloader.app.online.utils.SharedPrefUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.htetznaing.lowcostvideo.LowCostVideo
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.activities.MainActivity
import en.all.social.downloader.app.online.models.DownloadFile
import en.all.social.downloader.app.online.utils.Constants.DOWNLOAD_PATH
import en.all.social.downloader.app.online.utils.Constants.TAGI
import kotlinx.android.synthetic.main.ad_unified.view.*
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*
import kotlinx.android.synthetic.main.twitter_guide_layout.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
open class BaseFragment : Fragment() {
    var root: View? = null
    var webview: WebView? = null
    var rnds: String? = null
    private val downloadedList = ArrayList<String>()
    private var dialog: AlertDialog? = null
    var xGetter: LowCostVideo? = null
    var downloadFileList: ArrayList<DownloadFile>? = null
    lateinit var interstitial: InterstitialAd

    private var mainContext: MainActivity? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            mainContext = (requireActivity() as MainActivity)
            val sdf = SimpleDateFormat("dd_M_yyyy hh_mm_ss")
            val currentDate = sdf.format(Date())
            rnds = "Dated_$currentDate"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO:  navigate to new fragment
    fun navigateFragment(id: Int, websites: String) {
        val bundle = bundleOf("webAddress" to websites)

        findNavController().navigate(id, bundle)

    }

    //TODO:  navigate to new fragment by Ads
    fun navigateFragmentByAds(id: Int, websites: String) {
        if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {

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
                val bundle = bundleOf("webAddress" to websites)

                findNavController().navigate(id, bundle)

            }
            interstitial.adListener = object : AdListener() {
                override fun onAdClosed() {
                    requestNewInterstitial()
                    val bundle = bundleOf("webAddress" to websites)

                    findNavController().navigate(id, bundle)

                }
            }
        } else {
            val bundle = bundleOf("webAddress" to websites)

            findNavController().navigate(id, bundle)
        }


    }

    fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    fun startDownload(link: String?, name: String?, folderName: String) {
        if (Build.VERSION.SDK_INT >= 29) {
            downloadVideoInQ(link, name, folderName)
        } else {
            downloadVideoInOther(link, name, folderName)
        }
    }

    private fun downloadVideoInOther(
        link: String?,
        name: String?,
        folderName: String
    ) {
        try {
            val request =
                DownloadManager.Request(Uri.parse(link))
            request.allowScanningByMediaScanner()
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val file = File(DOWNLOAD_PATH + File.separator + folderName)
            if (!file.exists()) {
                file.mkdirs()
            }
            val path =
                Uri.withAppendedPath(Uri.fromFile(file), "$name.mp4")

            request.setDestinationUri(path)
            requireActivity()
            val dm = requireActivity()
                .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val urldownloadFragmentList: ArrayList<String> = vIdeoList()
            if (urldownloadFragmentList.contains(link)) {
                showToast(getString(R.string.video_is_already_downloading))
            } else {
                urldownloadFragmentList.add(link.toString())
                dm.enqueue(request)
                showToast(
                    getString(R.string.download_video_in_background)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadVideoInQ(
        link: String?,
        name: String?,
        folderName: String
    ) {
        try {
            val direct = File(
                requireActivity().getExternalFilesDir(null),
                "/SocialMediaDownloader/$folderName"
            )

            if (!direct.exists()) {
                direct.mkdirs()
            }

            val mgr =
                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val downloadUri = Uri.parse(link)
            val request = DownloadManager.Request(
                downloadUri
            )

            request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or
                        DownloadManager.Request.NETWORK_MOBILE
            )
                .setAllowedOverRoaming(false).setTitle(name) //Download Manager Title
                .setDescription("Downloading...")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/SocialMediaDownloader/$folderName/$name.mp4" //Your User define(Non Standard Directory name)/File Name
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
            showToast(
                getString(R.string.download_video_in_background)
            )
            mgr.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun vIdeoList(): ArrayList<String> {
        return downloadedList
    }

    //TODO: show dialog
    fun showDialog(message: String) {
        dialog = setProgressDialog(message)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    //TODO: hide dialog
    fun hideDialog() {
        try {
            if (dialog?.isShowing!!) {
                dialog?.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("InflateParams")
    private fun setProgressDialog(message: String): AlertDialog {

        val builder = MaterialAlertDialogBuilder(
            requireActivity()
        )
        builder.setCancelable(false)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.layout_loading_dialog, null)
        builder.setView(view)

        view.dialogText.text = message
        return builder.create()
    }

    fun changeToolbarColor(resId: Int) {
        try {//toolbar color
            (activity as AppCompatActivity?)!!.supportActionBar!!.setBackgroundDrawable(
                ColorDrawable(
                    resources.getColor(
                        resId
                    )
                )
            )
            (activity as AppCompatActivity?)!!.supportActionBar!!.elevation = 0F
            //status bar color
            val window = requireActivity().window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = ContextCompat.getColor(requireActivity(), resId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: banner
    fun adView(adView: AdView) {
//        adView.visibility = View.GONE
        try {
            if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                adView.adListener = object : AdListener() {

                    override fun onAdLoaded() {
                        adView.visibility = View.VISIBLE
                    }

                    override fun onAdFailedToLoad(error: Int) {
                        adView.visibility = View.GONE
                    }

                }
            } else {
                adView.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: load interstial
    fun loadInterstial() {
        try {

            Log.d(TAGI, "load ads")
            if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                interstitial = InterstitialAd(requireActivity())
                interstitial.adUnitId = getString(R.string.interstitial)
                try {
                    if (!interstitial.isLoading && !interstitial.isLoaded) {
                        val adRequest = AdRequest.Builder().build()
                        interstitial.loadAd(adRequest)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.d(TAGI, "error: " + ex.message)
                }

                requestNewInterstitial()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: requestNewInterstitial
    fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder().build()
        interstitial.loadAd(adRequest)
    }
    //TODO: native adview

    @SuppressLint("InflateParams")
    fun refreshAd(frameLayout: FrameLayout, layout: Int) {

        try {
            if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {

                val builder = AdLoader.Builder(requireActivity(), getString(R.string.nativead))

                builder.forUnifiedNativeAd { unifiedNativeAd ->
                    // OnUnifiedNativeAdLoadedListener implementation.
                    try {

                        val adView = layoutInflater
                            .inflate(layout, null) as UnifiedNativeAdView
                        populateUnifiedNativeAdView(unifiedNativeAd, adView)

                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


                val adLoader = builder.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        frameLayout.visibility = View.GONE
                    }
                }).build()

                adLoader.loadAd(AdRequest.Builder().build())


            } else {
                frameLayout.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //TODO: populateUnifiedNativeAdView
    private fun populateUnifiedNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.videoController

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
        }

        val mediaView = adView.ad_media
        val mainImageView = adView.ad_image

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.mediaView = mediaView
            mainImageView.visibility = View.GONE

        } else {
            adView.imageView = mainImageView
            mediaView.visibility = View.GONE

            // At least one image is guaranteed.
            val images = nativeAd.images
            mainImageView.setImageDrawable(images[0].drawable)

        }

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    fun guideDialog(isTwitter: Boolean, isFacebook: Boolean) {
        val factory = LayoutInflater.from(requireActivity())
        @SuppressLint("InflateParams") val deleteDialogView: View =
            factory.inflate(R.layout.twitter_guide_layout, null)
        val deleteDialog: AlertDialog = MaterialAlertDialogBuilder(requireActivity()).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)
        when {
            isTwitter -> {
                deleteDialogView.gif1.visibility = View.VISIBLE
            }
            isFacebook -> {
                deleteDialogView.gif2.visibility = View.VISIBLE
            }
            else -> {
                deleteDialogView.gif3.visibility = View.VISIBLE

            }
        }
        deleteDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.ok)
        ) { dialog, which -> // here you can add functions
            when {
                isTwitter -> {
                    SharedPrefUtils.saveData(requireActivity(), "isTwitter", true)
                }
                isFacebook -> {
                    SharedPrefUtils.saveData(requireActivity(), "isFacebook", true)
                }
                else -> {
                    SharedPrefUtils.saveData(requireActivity(), "isTikTok", true)
                }
            }
        }

        deleteDialog.show()
    }
}
