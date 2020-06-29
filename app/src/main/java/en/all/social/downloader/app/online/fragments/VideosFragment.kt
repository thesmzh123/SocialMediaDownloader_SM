package en.all.social.downloader.app.online.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.google.android.gms.ads.AdListener
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.adapters.StatusViewAdapter
import en.all.social.downloader.app.online.models.DownloadFile
import en.all.social.downloader.app.online.utils.ClickListener
import en.all.social.downloader.app.online.utils.Constants
import en.all.social.downloader.app.online.utils.FileCheckerHelper.isVideoFile
import en.all.social.downloader.app.online.utils.RecyclerTouchListener
import kotlinx.android.synthetic.main.fragment_photo_videos.view.*
import java.io.File

class VideosFragment(private val website: String) : BaseFragment() {

    fun newInstance(position: Int): VideosFragment {
        val f = VideosFragment(website)
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
            directory = File(Constants.STATUS_PATH)
            init(Constants.STATUS_PATH)
        } else if (website == "buisnessstatues") {
            directory = File(Constants.BUISNESS_STATUS_PATH)
            init(Constants.BUISNESS_STATUS_PATH)
        }
        checkEmptyState()
        root!!.recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                requireActivity(),
                root!!.recyclerView,
                object : ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                            if (position >= 2) {
                                if (position % 2 == 0) {
                                    if (interstitial.isLoaded) {
                                        if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
                                                Lifecycle.State.STARTED
                                            )
                                        ) {
                                            interstitial.show()
                                        } else {
                                            Log.d(
                                                Constants.TAGI,
                                                "App Is In Background Ad Is Not Going To Show"
                                            )

                                        }
                                    } else {
                                        openVideos(position)
                                    }
                                    interstitial.adListener = object : AdListener() {
                                        override fun onAdClosed() {
                                            requestNewInterstitial()
                                            openVideos(position)

                                        }
                                    }
                                } else {
                                    openVideos(position)

                                }
                            } else {
                                openVideos(position)

                            }

                        } else {
                            openVideos(position)
                        }
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        Log.d(Constants.TAGI, "onLongClick")

                    }
                })
        )
        return root
    }

    private fun openVideos(position: Int) {
        val bundle = Bundle()
        bundle.putSerializable("images", downloadFileList)
        bundle.putInt("position", position)
        bundle.putString("status", website)

        val ft =
            requireActivity().supportFragmentManager.beginTransaction()
        val newFragment = StatusSliderFragment().newInstance()
        newFragment.arguments = bundle
        newFragment.show(ft, "slideshow")
    }

    private fun checkEmptyState() {
        if (downloadFileList!!.isEmpty()) {
            root!!.recyclerView.visibility = View.GONE
            root!!.emptyView.visibility = View.VISIBLE
            root!!.noImage.setImageResource(R.drawable.no_file)
            root!!.noText.text = getString(R.string.no_video_status)
        } else {
            root!!.recyclerView.visibility = View.VISIBLE
            root!!.emptyView.visibility = View.GONE
        }

    }

    private fun init(statusPath: String) {
        try {
            downloadFileList!!.clear()
            val files = directory!!.listFiles()!!

            for (file in files) {
                val fileName = file.name
                val recordingUri = "$statusPath/$fileName"
                Log.d(Constants.TAGI, recordingUri)
                if (isVideoFile(recordingUri)) {
                    downloadFileList!!.add(DownloadFile(recordingUri, fileName))
                }
            }
            val fileModelAdapter = StatusViewAdapter(requireActivity(), downloadFileList!!)
            root!!.recyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
            root!!.recyclerView.itemAnimator = DefaultItemAnimator()
            root!!.recyclerView.adapter = fileModelAdapter
            fileModelAdapter.notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}