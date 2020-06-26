package en.all.social.downloader.app.online.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.utils.Constants.TAGI
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class TikTokDownloader(private val url1: String, val context: Context) :
    AsyncTask<Void, Void, String>() {
    private var url: URL? = null
    private var dialog: AlertDialog? = null
    private var tikTokLinkListener: TikTokLinkListener? = null

    fun setOnTikTokListener(tikTokLinkListener: TikTokLinkListener) {
        this.tikTokLinkListener = tikTokLinkListener
    }

    override fun doInBackground(vararg params: Void?): String {
        url = URL(url1)
        //Create a URL connection
        val conn = url!!.openConnection()

        //Set the user agent so TikTok will think we're a person using a browser instead of a program
        conn.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
        )

        //Set up the bufferedReader
        val `in` =
            BufferedReader(InputStreamReader(conn.getInputStream()))


        /*
         * Read every line until we get
         * to a string with the text 'videoObject'
         * which is where misc. information about
         * the user is stored and where the video
         * URL is stored too
         */
        var data: String
        while (`in`.readLine().also { data = it } != null) {
            if (data.contains("videoObject")) {
                // Read up until we reach a string in the HTML file valled 'videoObject'
                break
            }
        }

        //Close the bufferedReader as we don't need it anymore
        `in`.close()

        /*
         * Because we are viewing the raw source code from
         * the website, there's a lot of trash including but not
         * limited to HTML tags, javascript, random text, and so
         * on. We don't want that. That's why it will be cropped
         * out down below
         */

        //Crop out the useless tags and code behind the VideoObject string
        data = data.substring(data.indexOf("VideoObject"))


        //Grab the thumb nail URL
        var thumbnailURL = data.substring(data.indexOf("thumbnailUrl") + 16)
        thumbnailURL = thumbnailURL.substring(0, thumbnailURL.indexOf("\""))
        Log.d(TAGI, "ThumbnailURL: $thumbnailURL")

        //Grab content URL (video file)
        var contentURL = data.substring(data.indexOf("contentUrl") + 13)
        contentURL = contentURL.substring(0, contentURL.indexOf("?"))
        return contentURL
    }

    override fun onPreExecute() {
        super.onPreExecute()
        showDialog(context.getString(R.string.generate_download_link))

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Log.d(TAGI, "onPostExecute: $result")
        tikTokLinkListener!!.onResponseReceive(result)
        hideDialog()

    }

    //TODO: show dialog
    private fun showDialog(message: String) {
        dialog = setProgressDialog(message)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    //TODO: hide dialog
    private fun hideDialog() {
        if (dialog?.isShowing!!) {
            dialog?.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    private fun setProgressDialog(message: String): AlertDialog {

        val builder = MaterialAlertDialogBuilder(
            context
        )
        builder.setCancelable(false)
        val inflater = (context as AppCompatActivity).layoutInflater
        val view = inflater.inflate(R.layout.layout_loading_dialog, null)
        builder.setView(view)

        view.dialogText.text = message
        return builder.create()
    }


}