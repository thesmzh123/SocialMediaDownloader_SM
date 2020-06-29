@file:Suppress("UNCHECKED_CAST", "UNUSED_ANONYMOUS_PARAMETER")

package en.all.social.downloader.app.online.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.adapters.SliderViewPagerAdapter
import en.all.social.downloader.app.online.models.DownloadFile
import en.all.social.downloader.app.online.utils.Constants
import en.all.social.downloader.app.online.utils.Constants.DOWNLOAD_PATH
import en.all.social.downloader.app.online.utils.Constants.WHTSAPP_FOLDER
import kotlinx.android.synthetic.main.fragment_slider_status.view.*
import java.io.*
import java.util.*

class StatusSliderFragment : DialogFragment() {
    fun newInstance(): StatusSliderFragment {
        return StatusSliderFragment()
    }

    private lateinit var interstitial: InterstitialAd

    private var downloadFileList: ArrayList<DownloadFile>? = null

    var root: View? = null
    private var imageUrl = ""
    private var status = ""

    //	page change listener
    private val viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            displayMetaInfo(position)
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }
    private var selectedPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_slider_status, container, false)
        downloadFileList = requireArguments().getSerializable("images") as ArrayList<DownloadFile>
        selectedPosition = requireArguments().getInt("position")
        status = requireArguments().getString("status").toString()
        val myViewPagerAdapter = SliderViewPagerAdapter(requireActivity(), downloadFileList!!)
        root!!.viewpager.adapter = myViewPagerAdapter
        root!!.viewpager.addOnPageChangeListener(viewPagerPageChangeListener)
        setCurrentItem(selectedPosition)


        root!!.download.setOnClickListener {

            if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {

                if (interstitial.isLoaded) {
                    if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
                            Lifecycle.State.STARTED
                        )
                    ) {
                        interstitial.show()
                    } else {
                        Log.d(Constants.TAGI, "App Is In Background Ad Is Not Going To Show")
                    }
                } else {
                    downloadStatus()

                }
                interstitial.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        requestNewInterstitial()
                        downloadStatus()

                    }
                }
            } else {
                downloadStatus()
            }
        }
        root!!.share.setOnClickListener {
            shareStatus()
        }
        root!!.repost.setOnClickListener {
            rePostStatus()
        }
        loadInterstial()
        adView(root!!.adView)
        return root
    }

    private fun rePostStatus() {
        val uri: Uri
        val intent = Intent("android.intent.action.SEND")
        val fileWithinMyDir = File(imageUrl)
        if (fileWithinMyDir.exists()) {
            uri = if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    requireActivity(), requireActivity().packageName +
                            ".provider", fileWithinMyDir
                )
            } else {
                Uri.parse("file://$imageUrl")
            }
            try {
                intent.type = "*/*"
                if (status == "statuses") {
                    intent.setPackage("com.whatsapp")
                } else if (status == "buisnessstatues") {
                    intent.setPackage("com.whatsapp.w4b")
                }
                intent.putExtra("android.intent.extra.STREAM", uri)
                startActivity(intent)

            } catch (unused: ActivityNotFoundException) {
                showToast("WhatsApp Not Found on this Phone :(")
            }
        }
    }

    private fun shareStatus() {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val fileWithinMyDir = File(imageUrl)
        val uri: Uri
        if (fileWithinMyDir.exists()) {
            uri = if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    requireActivity(), requireActivity().packageName +
                            ".provider", fileWithinMyDir
                )
            } else {
                Uri.parse("file://$imageUrl")
            }
            intentShareFile.type = "*/*"
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intentShareFile, "Share Via"))
        }
    }

    private fun downloadStatus() {
        val builder =
            MaterialStyledDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.congrats))
            .setDescription(getString(R.string.download_this_status))
            .setStyle(Style.HEADER_WITH_ICON)
            .setIcon(R.drawable.ic_baseline_arrow_downward_24)
            .withDialogAnimation(true)
            .setPositiveText(getString(R.string.yes))
            .onPositive { dialog, which ->
                val inputFile = File(imageUrl)

                val `in`: InputStream
                val out: OutputStream
                try {

                    //create output directory if it doesn't exist
                    val dir = File(DOWNLOAD_PATH + File.separator + WHTSAPP_FOLDER + File.separator)
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    `in` = FileInputStream(inputFile.path)
                    out =
                        FileOutputStream(DOWNLOAD_PATH + File.separator + WHTSAPP_FOLDER + File.separator + inputFile.name)
                    val buffer = ByteArray(1024)
                    var read: Int
                    while (`in`.read(buffer).also { read = it } != -1) {
                        out.write(buffer, 0, read)
                    }
                    `in`.close()

                    // write the output file
                    out.flush()
                    out.close()
                    showToast(" Downloaded at: " + DOWNLOAD_PATH + File.separator + WHTSAPP_FOLDER + File.separator + inputFile.name)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .setNegativeText(getString(R.string.no))
            .onNegative { dialog, which -> dialog.dismiss() }
        val dialog = builder.build()
        dialog.show()
    }


    private fun setCurrentItem(position: Int) {
        root!!.viewpager.setCurrentItem(position, false)
        displayMetaInfo(selectedPosition)
    }

    @SuppressLint("SetTextI18n")
    private fun displayMetaInfo(position: Int) {
        root!!.lbl_count.text = "<< " + (position + 1) + " of " + downloadFileList!!.size + " >>"
        val image = downloadFileList!![position]
        root!!.title.text = image.fileName
        imageUrl = image.filePath
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    //TODO: banner
    private fun adView(adView: AdView) {
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
    private fun loadInterstial() {
        try {

            Log.d(Constants.TAGI, "load ads")
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
                    Log.d(Constants.TAGI, "error: " + ex.message)
                }

                requestNewInterstitial()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: requestNewInterstitial
    private fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder().build()
        interstitial.loadAd(adRequest)
    }
}