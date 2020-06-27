@file:Suppress("UNCHECKED_CAST")

package en.all.social.downloader.app.online.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.adapters.SliderViewPagerAdapter
import en.all.social.downloader.app.online.models.DownloadFile
import kotlinx.android.synthetic.main.fragment_slider_status.view.*
import java.util.*

class StatusSliderFragment : DialogFragment() {
    fun newInstance(): StatusSliderFragment {
        return StatusSliderFragment()
    }

    var downloadFileList: ArrayList<DownloadFile>? = null

    var root: View? = null
    private var imageUrl = ""

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
        val myViewPagerAdapter = SliderViewPagerAdapter(requireActivity(), downloadFileList!!)
        root!!.viewpager.adapter = myViewPagerAdapter
        root!!.viewpager.addOnPageChangeListener(viewPagerPageChangeListener)
        setCurrentItem(selectedPosition)

        return root
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

}