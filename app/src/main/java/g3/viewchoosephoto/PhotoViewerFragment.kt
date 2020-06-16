package g3.viewchoosephoto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_folder_photo_viewer.*

class PhotoViewerFragment: Fragment() {

    private var mLocalImages: ArrayList<LocalImage> = ArrayList()
    private var mPhotoChoose: ArrayList<LocalImage> = ArrayList()
    private var mAdapterPhotoChoose: PhotoChooseAdapter? = null
    private var mLayoutManagerPhotoChoose: LinearLayoutManager? = null
    private lateinit var rvChosenImg: View
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var photoChooseRecyclerView: RecyclerView

    private val onClickItemPhotoListener = PhotoAdapter.OnClickItemPhotoListener { position ->
        Log.d("congnm",mLocalImages[position].path)
        rvChosenImg.visibility = View.VISIBLE
        floatingActionButton.visibility = View.VISIBLE
        if (mPhotoChoose.size < 60) {
            mPhotoChoose.add(mLocalImages[position])
            Log.d("congnm",mPhotoChoose.size.toString())
            mLayoutManagerPhotoChoose?.scrollToPosition(mPhotoChoose.size - 1)
            mAdapterPhotoChoose?.notifyDataSetChanged()
//            if (mPhotoChoose.isEmpty()) {
//                mTvPlease.setVisibility(View.VISIBLE)
//                mRlContainerPlease.setVisibility(View.GONE)
//            } else {
//                mTvPlease.setVisibility(View.GONE)
//                mRlContainerPlease.setVisibility(View.VISIBLE)
//                mTvNumber.setText(getString(R.string.text_number, mPhotoChoose.size))
//            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folder_photo_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("congnm","onViewCreated")
        initDataRecyclerView()
        rvChosenImg = activity?.findViewById(R.id.container_rv_chosen_image)!!
        floatingActionButton = activity?.findViewById(R.id.photo_picker_fab_next)!!
        photoChooseRecyclerView = activity?.findViewById(R.id.photo_picker_rv_chosen_image)!!
        mAdapterPhotoChoose = PhotoChooseAdapter(context,mPhotoChoose)
        photoChooseRecyclerView.adapter = mAdapterPhotoChoose
        mLayoutManagerPhotoChoose = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        photoChooseRecyclerView.layoutManager = mLayoutManagerPhotoChoose
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

    override fun onDestroyView() {
        Log.d("congnm","onDestroyView")
        super.onDestroyView()
    }
}
