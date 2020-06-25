@file:Suppress("DEPRECATION")

package en.all.social.downloader.app.online.utils

import android.os.Environment
import java.io.File

object Constants {
    const val TAGI = "Test"
        val DOWNLOAD_PATH = Environment.getExternalStorageDirectory()
            .toString() + File.separator + "SocialMediaDownloader"
    const val INSTA_LINK = "https://www.instagram.com/p/"
    const val QUERY = "?__a=1"
    const val ARG_POSITION = "position"
    const val FB_FOLDER="facebook"
    const val TWITTER_FOLDER="twitter"

}