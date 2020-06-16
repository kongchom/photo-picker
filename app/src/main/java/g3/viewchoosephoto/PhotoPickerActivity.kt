package g3.viewchoosephoto

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_photo_picker.*

class PhotoPickerActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private var mAdapterPhoto: PhotoAdapter? = null
    private var mLocalImages: ArrayList<LocalImage>? = ArrayList()
    private var mAlbumImages: ArrayList<AlbumImage>? = ArrayList()
    private var mPhotoChoose: ArrayList<LocalImage> = ArrayList()
    private var mFolderPosition: Int = 0
    private lateinit var mCurrentFrag: PhotoViewerFragment

    private var mLayoutManagerPhotoChoose: LinearLayoutManager? = null
    private var mAdapterPhotoChoose: PhotoChooseAdapter? = null

    private var onItemClick = object : ListenFromFragment{
        override fun onItemClickInFragment(position: Int) {
            Log.d("congnm lis", position.toString())
            container_rv_chosen_image.visibility = View.VISIBLE
            photo_picker_fab_next.visibility = View.VISIBLE
            mPhotoChoose.add(mAlbumImages?.get(mFolderPosition)!!.localImages[position])
            Log.d("congnm", mPhotoChoose.size.toString())
            mLayoutManagerPhotoChoose?.scrollToPosition(mPhotoChoose.size - 1)
            mAdapterPhotoChoose?.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_picker)
        onPermissionFolder()
        initData()
        setUpViewPagerWithTabLayout()
        initViews()
    }

    private fun onPermissionFolder() {
        PermissionNewVideoUtils.askForPermissionFolder(
            this,
            MainActivity1.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
        ) { initData() }
    }

    private fun initData() {
        mAlbumImages = ImageUtils.getAllImage(applicationContext) as ArrayList<AlbumImage>?
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPager = findViewById(R.id.photo_picker_view_pager)
        tabLayout = findViewById(R.id.photo_picker_tab_layout_folder)
        viewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mAlbumImages!!.size
            }
            override fun createFragment(position: Int): Fragment {
                mFolderPosition = position
                Log.d("congnm","onCreateFragmentPager")
                mCurrentFrag = PhotoViewerFragment.newInstance(mAlbumImages!!, position)
                mCurrentFrag.setListener(onItemClick)
                return mCurrentFrag
            }
        }
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = mAlbumImages!![position].name
            }).attach()
    }

    private fun initViews() {
        mAdapterPhotoChoose = PhotoChooseAdapter(applicationContext,mPhotoChoose)
        mLayoutManagerPhotoChoose = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL, false)
        photo_picker_rv_chosen_image.adapter = mAdapterPhotoChoose
        photo_picker_rv_chosen_image.layoutManager = mLayoutManagerPhotoChoose
    }

    interface ListenFromFragment {
        fun onItemClickInFragment(position: Int)
    }
}