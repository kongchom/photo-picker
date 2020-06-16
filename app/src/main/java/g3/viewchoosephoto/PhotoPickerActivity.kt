package g3.viewchoosephoto

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PhotoPickerActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private var mAdapterPhoto: PhotoAdapter? = null
    private var mLocalImages: ArrayList<LocalImage>? = null
    private var mAlbumImages: ArrayList<AlbumImage>? = null
    private var mPhotoChosen: ArrayList<LocalImage>? = null

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
        mLocalImages?.addAll(mAlbumImages!![1]!!.localImages)
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPager = findViewById(R.id.photo_picker_view_pager)
        tabLayout = findViewById(R.id.photo_picker_tab_layout_folder)
        mAdapterPhoto = PhotoAdapter(applicationContext,mLocalImages)
//        viewPager.adapter = FolderPhotoAdapter(mAlbumImages!!,applicationContext)
        viewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mAlbumImages!!.size
            }

            override fun createFragment(position: Int): Fragment {
                Log.d("congnm","onCreateFragmentPager")
                return PhotoViewerFragment.newInstance(mAlbumImages!!, position)
            }
        }

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = mAlbumImages!![position].name
            }).attach()
    }

    private fun initViews() {

    }
}