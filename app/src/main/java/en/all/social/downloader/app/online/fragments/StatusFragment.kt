package en.all.social.downloader.app.online.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.adapters.StatusViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_downloader.view.*


@Suppress("SameParameterValue", "DEPRECATION")
class StatusFragment : BaseFragment() {
    private var tabLayout: TabLayout? = null
    var website: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_downloader, container, false)
        website = requireArguments().getString("webAddress").toString()
        val viewPager: ViewPager = root!!.viewpager
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = StatusViewPagerAdapter(childFragmentManager, website.toString())
        setupViewPager(viewPager)
        tabLayout = root!!.tabs
        tabLayout!!.setupWithViewPager(viewPager)
        changeToolbarColor1(R.color.whtsappColor)

        return root
    }

    private fun changeToolbarColor1(colorID: Int) {
        try {
            root!!.appBarLayout.setBackgroundResource(colorID)
            tabLayout!!.setBackgroundResource(colorID)
            (activity as AppCompatActivity?)!!.supportActionBar
                ?.setBackgroundDrawable(
                    ColorDrawable(
                        resources.getColor(
                            colorID
                        )
                    )
                )

            (activity as AppCompatActivity?)!!.supportActionBar!!.elevation = 0F
            //status bar color
            val window = requireActivity().window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = ContextCompat.getColor(requireActivity(), colorID)
//            mainContext!!.changeToolbarColor(colorID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = StatusViewPagerAdapter(childFragmentManager, website.toString())

        adapter.addFragment(PhotosFragment(website.toString()), getString(R.string.photos))
        adapter.addFragment(VideosFragment(website.toString()), getString(R.string.videos))
        viewPager.adapter = adapter
    }

}