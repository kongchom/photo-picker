package g3.viewchoosephoto

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_page.view.*

class FolderPhotoAdapter(
    private val albumImages: ArrayList<AlbumImage>,
    private val context: Context
) : RecyclerView.Adapter<PagerVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))

    override fun getItemCount(): Int = albumImages.size

    override fun onBindViewHolder(holder: PagerVH, position: Int): Unit = holder.itemView.run {
        val photoAdapter = PhotoAdapter(context, albumImages[position].localImages)
        item_page_recycler_view.adapter = photoAdapter
        item_page_recycler_view.layoutManager = GridLayoutManager(context,3, GridLayoutManager.VERTICAL,false)
//        Log.d("congnm", albumImages[position].localImages.size.toString())
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)

