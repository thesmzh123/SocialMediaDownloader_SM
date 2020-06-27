package en.all.social.downloader.app.online.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.models.DownloadFile
import kotlinx.android.synthetic.main.file_model_layout.view.*

class StatusViewAdapter(
    val context: Context,
    private val downloadFileList: ArrayList<DownloadFile>
) : RecyclerView.Adapter<StatusViewAdapter.MyHolder>() {
    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_model_layout, parent, false)
        return MyHolder(v)
    }

    override fun getItemCount(): Int {
        return downloadFileList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val status = downloadFileList[position]
        if (status.filePath.contains("mp4")) {
            holder.itemView.videoType.visibility = View.VISIBLE
            Glide.with(context).load(status.filePath).into(holder.itemView.video)

        }else {
            holder.itemView.image.visibility=View.VISIBLE
            Glide.with(context).load(status.filePath).into(holder.itemView.image)

        }
    }
}