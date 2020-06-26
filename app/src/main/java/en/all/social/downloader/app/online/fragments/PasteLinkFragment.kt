package en.all.social.downloader.app.online.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.utils.Constants
import kotlinx.android.synthetic.main.fragment_paste_link.view.*


open class PasteLinkFragment(private val website: String) : BaseFragment() {
    open fun newInstance(position: Int): BaseFragment {
        val f = PasteLinkFragment(website)
        val b = Bundle()
        b.putInt(Constants.ARG_POSITION, position)
        f.arguments = b
        return f
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_paste_link, container, false)
        controlView()
        return root
    }

    private fun controlView() {
        when (website) {
            getString(R.string.facebook_website) -> {
                changeTextAndButtonColor(
                    getString(R.string.fb_download_text)
                )
                root!!.fbBtn.visibility = View.VISIBLE
            }
            getString(R.string.twitter_website) -> {
                changeTextAndButtonColor(
                    getString(R.string.twitter_download_text)
                )
                root!!.twitterBtn.visibility = View.VISIBLE

            }
            getString(R.string.linkedin_website) -> {
                changeTextAndButtonColor(
                    getString(R.string.linkedin_download_text)
                )
                root!!.linkedinBtn.visibility = View.VISIBLE

            }
            getString(R.string.instagram_website) -> {
                changeTextAndButtonColor(
                    getString(R.string.insta_download_text)
                )
                root!!.instagramBtn.visibility = View.VISIBLE

            }
            getString(R.string.tiktok_website) -> {

                changeTextAndButtonColor(
                    getString(R.string.tiktok_download_text)
                )
                root!!.tiktokBtn.visibility = View.VISIBLE

            }
        }
    }

    private fun changeTextAndButtonColor(s: String) {
        root!!.textDownload.text = s

    }

}