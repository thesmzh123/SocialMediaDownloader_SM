package en.all.social.downloader.app.online.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.models.DownloadFile
import kotlinx.android.synthetic.main.slider_vew_pager_layout.view.*
import java.util.*

class SliderViewPagerAdapter(
    val context: Context,
    private val downloadFileList: ArrayList<DownloadFile>
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        val view: View =
            layoutInflater!!.inflate(R.layout.slider_vew_pager_layout, container, false)

        val image = downloadFileList[position]
        if (image.filePath.contains("mp4")) {
            view.RelativeLayoutVido.visibility = View.VISIBLE
            Glide.with(context).load(image.filePath)
                .thumbnail(0.5f)
                .into(view.image_preview_video)
        } else {
            view.RelativeLayout1.visibility = View.VISIBLE

            Glide.with(context).load(image.filePath)
                .thumbnail(0.5f)
                .into(view.image_preview)
        }
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return downloadFileList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}