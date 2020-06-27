package en.all.social.downloader.app.online.fragments

import android.annotation.SuppressLint
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
import en.all.social.downloader.app.online.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_downloader.view.*


@Suppress("DEPRECATION")
class DownloaderFragment : BaseFragment() {
    var tabLayout: TabLayout? = null
    var website: String? = null

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
                changeToolbarColor1(R.color.fbColor)
            }
            website.equals(getString(R.string.twitter_website)) -> {
                changeToolbarColor1(R.color.twitterColor)
            }
            website.equals(getString(R.string.linkedin_website)) -> {
                changeToolbarColor1(R.color.linkedinColor)
            }
            website.equals(getString(R.string.instagram_website)) -> {
                changeToolbarColor1(R.color.centerColorInsta)
            }
            website.equals(getString(R.string.tiktok_website)) -> {
                changeToolbarColor1(R.color.tiktokColor)
            }
        }
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
        val adapter = ViewPagerAdapter(childFragmentManager, website.toString())
        if (website.equals(getString(R.string.linkedin_website))) {
            adapter.addFragment(BrowserFragment(website.toString()), getString(R.string.browse))
        } else {
            adapter.addFragment(BrowserFragment(website.toString()), getString(R.string.browse))
            adapter.addFragment(
                PasteLinkFragment(website.toString()),
                getString(R.string.paste_link)
            )
        }
        viewPager.adapter = adapter
    }


}