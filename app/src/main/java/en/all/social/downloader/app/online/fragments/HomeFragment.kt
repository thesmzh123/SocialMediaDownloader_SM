package en.all.social.downloader.app.online.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import en.all.social.downloader.app.online.R
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false)

        mainContext!!.changeToolbarColor(R.color.colorPrimary)
        root!!.fb.setOnClickListener {
            navigateFragment(R.id.nav_facebook, getString(R.string.facebook_website))
        }
        root!!.whtsapp.setOnClickListener {
            val pictureDialog: AlertDialog.Builder = MaterialAlertDialogBuilder(requireActivity())
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf(
                getString(R.string.whtsapp_status),
                getString(R.string.whtsapp_buisness_status)
            )
            pictureDialog.setItems(pictureDialogItems,
                { dialog, which ->
                    when (which) {
                        0 -> navigateFragment(R.id.nav_whtsapp, "")
                        1 -> navigateFragment(R.id.nav_whtsapp_buisness, "")
                    }
                })
            pictureDialog.show()
        }
        root!!.twitter.setOnClickListener {
            navigateFragment(R.id.nav_twittetr, getString(R.string.twitter_website))
        }
        root!!.linkedin.setOnClickListener {
            navigateFragment(R.id.nav_linkedin, getString(R.string.linkedin_website))
        }
        root!!.tiktok.setOnClickListener {
            navigateFragment(R.id.nav_tiktok, getString(R.string.tiktok_website))
        }
        root!!.instagram.setOnClickListener {
            navigateFragment(R.id.nav_instagram, getString(R.string.instagram_website))
        }
        return root
    }

    private var callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

}