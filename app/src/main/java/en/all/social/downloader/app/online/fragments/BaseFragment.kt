@file:Suppress("DEPRECATION")

package en.all.social.downloader.app.online.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.htetznaing.lowcostvideo.LowCostVideo
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.activities.MainActivity
import en.all.social.downloader.app.online.utils.Constants.DOWNLOAD_PATH
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

open class BaseFragment : Fragment() {
    var root: View? = null
    var webview: WebView? = null
    var rnds: String? = null
    private val downloadedList = ArrayList<String>()
    private var dialog: AlertDialog? = null
    var xGetter: LowCostVideo? = null

    var mainContext: MainActivity? = null

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


    private fun vIdeoList(): ArrayList<String> {
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
        if (dialog?.isShowing!!) {
            dialog?.dismiss()
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


}
