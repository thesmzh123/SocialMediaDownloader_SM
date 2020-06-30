package en.all.social.downloader.app.online.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import en.all.social.downloader.app.online.R
import kotlinx.android.synthetic.main.fragment_home.view.*


@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class HomeFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false)

        changeToolbarColor(R.color.colorPrimary)
        root!!.fb.setOnClickListener {
            navigateFragmentByAds(R.id.nav_facebook, getString(R.string.facebook_website))
        }
        root!!.whtsapp.setOnClickListener {
            val pictureDialog: AlertDialog.Builder = MaterialAlertDialogBuilder(requireActivity())
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf(
                getString(R.string.whtsapp_status),
                getString(R.string.whtsapp_buisness_status)
            )
            pictureDialog.setItems(
                pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    0 -> navigateFragmentByAds(R.id.nav_whtsapp, "statuses")
                    1 -> navigateFragment(R.id.nav_whtsapp_buisness, "buisnessstatues")
                }
            }
            pictureDialog.show()
        }
        root!!.twitter.setOnClickListener {
            navigateFragmentByAds(R.id.nav_twittetr, getString(R.string.twitter_website))
        }
        root!!.linkedin.setOnClickListener {
            navigateFragmentByAds(R.id.nav_linkedin, getString(R.string.linkedin_website))
        }
        root!!.tiktok.setOnClickListener {
            navigateFragment(R.id.nav_tiktok, getString(R.string.tiktok_website))
        }
        root!!.instagram.setOnClickListener {
            navigateFragmentByAds(R.id.nav_instagram, getString(R.string.instagram_website))
        }
        loadInterstial()
        refreshAd(root!!.nativeAd, R.layout.ad_unified)
        return root
    }

    private var callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
//            requireActivity().finish()
            exit()
        }
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun exit() {
        val builder =
            MaterialStyledDialog.Builder(requireActivity())
        builder
            .setDescription(getString(R.string.do_you_want_exit))
            .setStyle(Style.HEADER_WITH_ICON)
            .setIcon(R.drawable.ic_baseline_rate_review_24)
            .withDialogAnimation(true)
            .setPositiveText(getString(R.string.exit))
            .onPositive { dialog, which -> requireActivity().finishAffinity() }
            .setNegativeText(getString(R.string.rate_us))
            .onNegative { dialog, which ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().packageName)
                    )
                )
            }.setNeutralText(getString(R.string.cancel))
            .onNeutral { dialog, which -> dialog.dismiss() }

        val dialog = builder.build()
        dialog.show()
      }
}