package en.all.social.downloader.app.online.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import en.all.social.downloader.app.online.fragments.PhotosFragment
import en.all.social.downloader.app.online.fragments.VideosFragment

class StatusViewPagerAdapter(fm: FragmentManager, private val website: String) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList: ArrayList<Fragment> =
        ArrayList()
    private val mFragmentTitleList: ArrayList<String> =
        ArrayList()


    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return PhotosFragment(website).newInstance(position)
            }
            1 -> {
                return VideosFragment(website).newInstance(position)
            }

        }
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(
        fragment: Fragment?,
        title: String?
    ) {
        mFragmentList.add(fragment!!)
        mFragmentTitleList.add(title!!)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
}