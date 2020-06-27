@file:Suppress("DEPRECATION")

package en.all.social.downloader.app.online.fragments

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.adapters.DownloadFileAdapter
import en.all.social.downloader.app.online.models.DownloadFile
import en.all.social.downloader.app.online.utils.CategoryCustomButton
import en.all.social.downloader.app.online.utils.Constants.DOWNLOAD_DIRECTORY
import en.all.social.downloader.app.online.utils.Constants.FB_FOLDER
import en.all.social.downloader.app.online.utils.Constants.INSTAGRAM_FOLDER
import en.all.social.downloader.app.online.utils.Constants.LINKEDIN_FOLDER
import en.all.social.downloader.app.online.utils.Constants.TAGI
import en.all.social.downloader.app.online.utils.Constants.TIKTOK_FOLDER
import en.all.social.downloader.app.online.utils.Constants.TWITTER_FOLDER
import kotlinx.android.synthetic.main.fragment_download.view.*
import java.io.File


class DownloadFragment : BaseFragment() {
    private var root1: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_download, container, false)
        changeToolbarColor(R.color.colorPrimary)
        downloadFileList = ArrayList()
        root1 = if (Build.VERSION.SDK_INT >= 29) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        } else {

            Environment.getExternalStorageDirectory()

        }

        root!!.fbCard.setOnClickListener {
            init(FB_FOLDER)
            changeCardColor(
                root!!.fbCard,
                root!!.instagramCard,
                root!!.twitterCard,
                root!!.tiktokCard,
                root!!.linkdeinCard
            )
        }
        root!!.instagramCard.setOnClickListener {
            init(INSTAGRAM_FOLDER)
            changeCardColor(
                root!!.instagramCard,
                root!!.fbCard,
                root!!.twitterCard,
                root!!.tiktokCard,
                root!!.linkdeinCard
            )
        }

        root!!.twitterCard.setOnClickListener {
            init(TWITTER_FOLDER)
            changeCardColor(
                root!!.twitterCard,
                root!!.fbCard,
                root!!.instagramCard,
                root!!.tiktokCard,
                root!!.linkdeinCard
            )
        }
        root!!.tiktokCard.setOnClickListener {
            init(TIKTOK_FOLDER)
            changeCardColor(
                root!!.tiktokCard,
                root!!.fbCard,
                root!!.instagramCard,
                root!!.twitterCard,
                root!!.linkdeinCard
            )
        }
        root!!.linkdeinCard.setOnClickListener {
            init(LINKEDIN_FOLDER)
            changeCardColor(
                root!!.linkdeinCard,
                root!!.fbCard,
                root!!.instagramCard,
                root!!.twitterCard,
                root!!.tiktokCard
            )
        }

        init(FB_FOLDER)
        changeCardColor(
            root!!.fbCard,
            root!!.instagramCard,
            root!!.twitterCard,
            root!!.tiktokCard,
            root!!.linkdeinCard
        )
        return root
    }

    private fun init(folderName: String) {
        try {
            downloadFileList!!.clear()
            val path = root1!!.absolutePath + "/" + DOWNLOAD_DIRECTORY + "/" + folderName + "/"
            Log.d(TAGI, "Path: $path")
            val directory = File(path)
            val files = directory.listFiles()!!
            Log.d(TAGI, "Size: " + files.size)

            for (file in files) {
                Log.d(TAGI, "FileName:" + file.name)
                val fileName = file.name
                val recordingUri =
                    root1!!.absolutePath + "/" + DOWNLOAD_DIRECTORY + "/" + folderName + "/" + fileName
                downloadFileList!!.add(DownloadFile(recordingUri, fileName))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //creating our adapter
        val adapter =
            DownloadFileAdapter(requireActivity(), downloadFileList!!, this@DownloadFragment)

        //now adding the adapter to recyclerview
        root!!.recyclerView.adapter = adapter
        checkEmptyState()
    }

    fun checkEmptyState() {
        if (downloadFileList!!.isEmpty()) {
            root!!.recyclerView.visibility = View.GONE
            root!!.emptyView.visibility = View.VISIBLE
        } else {
            root!!.recyclerView.visibility = View.VISIBLE
            root!!.emptyView.visibility = View.GONE
        }
    }

    private fun changeCardColor(
        categoryCustomButton: CategoryCustomButton, categoryCustomButton2: CategoryCustomButton,
        categoryCustomButton3: CategoryCustomButton, categoryCustomButton4: CategoryCustomButton,
        categoryCustomButton5: CategoryCustomButton
    ) {
        categoryCustomButton.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))
        categoryCustomButton2.setCardBackgroundColor(resources.getColor(R.color.colorBtn))
        categoryCustomButton3.setCardBackgroundColor(resources.getColor(R.color.colorBtn))
        categoryCustomButton4.setCardBackgroundColor(resources.getColor(R.color.colorBtn))
        categoryCustomButton5.setCardBackgroundColor(resources.getColor(R.color.colorBtn))
    }
}