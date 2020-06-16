package g3.viewchoosephoto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_photo_picker.*


class PhotoPickerActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private var mAlbumImages: ArrayList<AlbumImage>? = ArrayList()
    private var mPhotoChoose: ArrayList<LocalImage> = ArrayList()
    private var mFolderPosition: Int = 0
    private lateinit var mCurrentFrag: PhotoViewerFragment

    private var mLayoutManagerPhotoChoose: LinearLayoutManager? = null
    private var mAdapterPhotoChoose: PhotoChooseAdapter? = null

    private var onItemClick = object : ItemClickFromPagerFragment{
        override fun onItemClickInFragment(position: Int) {
            Log.d("congnm lis", position.toString())
            container_rv_chosen_image.visibility = View.VISIBLE
            mPhotoChoose.add(mAlbumImages?.get(mFolderPosition)!!.localImages[position])
            if (mPhotoChoose.size >= 3) {
                photo_picker_fab_next.visibility = View.VISIBLE
            }
            Log.d("congnm", mPhotoChoose.size.toString())
            mLayoutManagerPhotoChoose?.scrollToPosition(mPhotoChoose.size - 1)
            mAdapterPhotoChoose?.notifyDataSetChanged()
        }
    }

    private var onRemoveItemClickItemPhotoListener = PhotoChooseAdapter.OnClickRemoveItemListener { position ->
        if (position < mPhotoChoose.size) {
            mPhotoChoose.removeAt(position)
        }
        mAdapterPhotoChoose!!.notifyDataSetChanged()
        if (mPhotoChoose.size < 3) {
            photo_picker_fab_next.visibility = View.GONE
        }
        if (mPhotoChoose.isEmpty()) {
            container_rv_chosen_image.visibility = View.GONE
            photo_picker_fab_next.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_picker)
        onPermissionFolder()
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
        setUpViewPagerWithTabLayout()
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPager = findViewById(R.id.photo_picker_view_pager)
        tabLayout = findViewById(R.id.photo_picker_tab_layout_folder)
        viewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mAlbumImages!!.size
            }
            override fun createFragment(position: Int): Fragment {
                mCurrentFrag = PhotoViewerFragment.newInstance(mAlbumImages!!, position)
                mCurrentFrag.setListener(onItemClick)
                return mCurrentFrag
            }
        }
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mFolderPosition = position
                super.onPageSelected(position)
            }
        })
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = mAlbumImages!![position].name
            }).attach()
    }

    private fun initViews() {
        mAdapterPhotoChoose = PhotoChooseAdapter(applicationContext,mPhotoChoose)
        mAdapterPhotoChoose!!.setOnClickRemoveItemListener(onRemoveItemClickItemPhotoListener)
        mLayoutManagerPhotoChoose = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL, false)
        photo_picker_rv_chosen_image.adapter = mAdapterPhotoChoose
        photo_picker_rv_chosen_image.layoutManager = mLayoutManagerPhotoChoose

        photo_picker_iv_take_picture.setOnClickListener {
            val granted = requestCamera().blockingGet()
            if (granted) {
                Log.d("congnm12","granted")
            } else {

            }
        }
    }

    private fun requestCamera(): Single<Boolean> {
        val rxPermissionsObservables : Observable<Boolean> = RxPermissions(this).request(Manifest.permission.CAMERA)
        return rxPermissionsObservables.single(true)
    }

    interface ItemClickFromPagerFragment {
        fun onItemClickInFragment(position: Int)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MainActivity1.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData()
            }
        }
    }
}