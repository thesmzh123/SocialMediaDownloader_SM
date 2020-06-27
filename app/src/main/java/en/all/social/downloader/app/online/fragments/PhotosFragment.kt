package en.all.social.downloader.app.online.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.adapters.StatusViewAdapter
import en.all.social.downloader.app.online.models.DownloadFile
import en.all.social.downloader.app.online.utils.Constants
import en.all.social.downloader.app.online.utils.Constants.BUISNESS_STATUS_PATH
import en.all.social.downloader.app.online.utils.Constants.STATUS_PATH
import en.all.social.downloader.app.online.utils.Constants.TAGI
import en.all.social.downloader.app.online.utils.FileCheckerHelper.isImageFile
import kotlinx.android.synthetic.main.fragment_photo_videos.view.*
import java.io.File

class PhotosFragment(private val website: String) : BaseFragment() {

    fun newInstance(position: Int): PhotosFragment {
        val f = PhotosFragment(website)
        val b = Bundle()
        b.putInt(Constants.ARG_POSITION, position)
        f.arguments = b
        return f
    }

    private var directory: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_photo_videos, container, false)
        downloadFileList = ArrayList()
        if (website == "statuses") {
            directory = File(STATUS_PATH)
            init(STATUS_PATH)
        } else if (website == "buisnessstatues") {
            directory = File(BUISNESS_STATUS_PATH)
            init(BUISNESS_STATUS_PATH)
        }

        checkEmptyState()
        return root
    }

    private fun init(statusPath: String) {
        try {
            downloadFileList!!.clear()
            val files = directory!!.listFiles()!!

            for (file in files) {
                val fileName = file.name
                val recordingUri = "$statusPath/$fileName"
                Log.d(TAGI, recordingUri)
                if (isImageFile(recordingUri)) {
                    downloadFileList!!.add(DownloadFile(recordingUri, fileName))
                }
            }
            val fileModelAdapter = StatusViewAdapter(requireActivity(),downloadFileList!!)
            root!!.recyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
            root!!.recyclerView.itemAnimator = DefaultItemAnimator()
            root!!.recyclerView.adapter = fileModelAdapter
            fileModelAdapter.notifyDataSetChanged()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkEmptyState() {
        if (downloadFileList!!.isEmpty()) {
            root!!.recyclerView.visibility = View.GONE
            root!!.emptyView.visibility = View.VISIBLE
            root!!.noImage.setImageResource(R.drawable.nocam)
            root!!.noText.text = getString(R.string.no_photo_status)
        } else {
            root!!.recyclerView.visibility = View.VISIBLE
            root!!.emptyView.visibility = View.GONE
        }
    }

}