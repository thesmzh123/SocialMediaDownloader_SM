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
    const val FB_FOLDER = "facebook"
    const val TWITTER_FOLDER = "twitter"
    const val INSTAGRAM_FOLDER = "instagram"
    const val LINKEDIN_FOLDER = "linkedin"
    const val TIKTOK_FOLDER = "tiktok"
    const val DOWNLOAD_DIRECTORY = "SocialMediaDownloader"
    val STATUS_PATH =
        Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses"
    val BUISNESS_STATUS_PATH =
        Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses"
}