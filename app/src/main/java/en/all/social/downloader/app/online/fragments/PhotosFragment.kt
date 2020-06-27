package en.all.social.downloader.app.online.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.adapters.StatusViewPagerAdapter
import en.all.social.downloader.app.online.utils.Constants
import kotlinx.android.synthetic.main.fragment_downloader.view.*

class PhotosFragment(private val website: String) : BaseFragment() {

    fun newInstance(position: Int): PhotosFragment {
        val f = PhotosFragment(website)
        val b = Bundle()
        b.putInt(Constants.ARG_POSITION, position)
        f.arguments = b
        return f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_photo_videos, container, false)
        showToast(website)

        return root
    }

}