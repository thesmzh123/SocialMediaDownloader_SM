package en.all.social.downloader.app.online.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import en.all.social.downloader.app.online.R


class DownloadFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_download, container, false)
        mainContext!!.changeToolbarColor(R.color.colorPrimary)
        return root
    }


}