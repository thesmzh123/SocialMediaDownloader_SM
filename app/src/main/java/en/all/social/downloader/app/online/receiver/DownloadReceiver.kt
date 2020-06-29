package en.all.social.downloader.app.online.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import en.all.social.downloader.app.online.fragments.BaseFragment
import en.all.social.downloader.app.online.utils.Constants.TAGI

class DownloadReceiver : BroadcastReceiver() {
    private var dmo: DownloadManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        when (val action = intent.action) {
            "android.intent.action.DOWNLOAD_COMPLETE" -> {
                val extras = intent.extras
                dmo =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val q = DownloadManager.Query()
                extras?.getLong(DownloadManager.EXTRA_DOWNLOAD_ID)?.let {
                    q.setFilterById(
                        it
                    )
                }
                val c = dmo!!.query(q)
                val xcoords = BaseFragment().vIdeoList()
                if (c.moveToFirst()) {
                    val status =
                        c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        xcoords.remove(c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI)))
                        Toast.makeText(context, "Download Completed", Toast.LENGTH_LONG).show()
                    } else {
                        xcoords.remove(c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI)))
                    }
                }
                c.close()
            }
            "android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED" -> {
                Log.d(TAGI, "Notification clicked")
            }
            else -> {
                Log.d(TAGI, action!!)
            }
        }
    }
}