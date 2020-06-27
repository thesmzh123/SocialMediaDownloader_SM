package en.all.social.downloader.app.online.utils

import android.view.View

interface ClickListener {
    fun onClick(view: View?, position: Int)

    fun onLongClick(view: View?, position: Int)
}