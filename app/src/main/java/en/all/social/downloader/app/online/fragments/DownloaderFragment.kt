package en.all.social.downloader.app.online.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_downloader.view.*


class DownloaderFragment : BaseFragment() {
    private var tabLayout: TabLayout? = null
    private var website: String? = null

    @SuppressLint("ResourceAsColor")
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
        viewPager.adapter = ViewPagerAdapter(childFragmentManager, website.toString())
        setupViewPager(viewPager)
        tabLayout = root!!.tabs
        tabLayout!!.setupWithViewPager(viewPager)
        when {
            website.equals(getString(R.string.facebook_website)) -> {
                changeToolbarColor(R.color.fbColor)
            }
            website.equals(getString(R.string.twitter_website)) -> {
                changeToolbarColor(R.color.twitterColor)
            }
            website.equals(getString(R.string.linkedin_website)) -> {
                changeToolbarColor(R.color.linkedinColor)
            }
            website.equals(getString(R.string.instagram_website)) -> {
                changeToolbarColor(R.color.centerColorInsta)
            }
            website.equals(getString(R.string.tiktok_website)) -> {
                changeToolbarColor(R.color.tiktokColor)
            }
        }
        return root

    }

    private fun changeToolbarColor(colorID: Int) {
        root!!.appBarLayout.setBackgroundResource(colorID)
        tabLayout!!.setBackgroundResource(colorID)
        mainContext!!.changeToolbarColor(colorID)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(childFragmentManager, website.toString())
        adapter.addFragment(BrowserFragment(website.toString()), getString(R.string.browse))
        adapter.addFragment(PasteLinkFragment(website.toString()), getString(R.string.paste_link))
        viewPager.adapter = adapter
    }


}