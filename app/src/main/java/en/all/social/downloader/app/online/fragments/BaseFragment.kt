package en.all.social.downloader.app.online.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import en.all.social.downloader.app.online.activities.MainActivity
import en.all.social.downloader.app.online.utils.Constants.ARG_POSITION

open class BaseFragment : Fragment() {
    var root: View? = null
    var webview: WebView? = null

    var mainContext: MainActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainContext = (requireActivity() as MainActivity)
    }

    //TODO:  navigate to new fragment
    fun navigateFragment(id: Int, websites: String) {
        val bundle = bundleOf("webAddress" to websites)

        findNavController().navigate(id, bundle)

    }


    fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}