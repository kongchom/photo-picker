package g3.viewchoosephoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_folder_photo_viewer.*

class PhotoViewerFragment: Fragment() {

    private var mLocalImages: ArrayList<LocalImage> = ArrayList()
    private var onItemClick : PhotoPickerActivity.ItemClickFromPagerFragment? = null

    fun setListener(listener: PhotoPickerActivity.ItemClickFromPagerFragment) {
        onItemClick = listener
    }

    private val onClickItemPhotoListener = PhotoAdapter.OnClickItemPhotoListener { position ->
        onItemClick?.onItemClickInFragment(position)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folder_photo_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initDataRecyclerView()
    }

    private fun initDataRecyclerView() {
        val args = arguments
        mLocalImages = args?.getSerializable(KEY_ALBUM_LIST) as ArrayList<LocalImage>
        val photoAdapter = PhotoAdapter(context,mLocalImages)
        photoAdapter.setOnClickItemPhotoListener(onClickItemPhotoListener)
        folder_photo_viewer_recycler_view.adapter = photoAdapter
        folder_photo_viewer_recycler_view.layoutManager = GridLayoutManager(context,3, GridLayoutManager.VERTICAL,false)
    }

    companion object {
        fun newInstance(albumImages: ArrayList<AlbumImage>, position: Int): PhotoViewerFragment {
            val args = Bundle()
            val localImages: ArrayList<LocalImage> = ArrayList()
            localImages.addAll(albumImages[position].localImages)
            args.putSerializable(KEY_ALBUM_LIST, localImages)
            val fragment = PhotoViewerFragment()
            fragment.arguments = args
            return fragment
        }
        const val KEY_ALBUM_LIST = "KEY_ALBUM_LIST"
    }
}
