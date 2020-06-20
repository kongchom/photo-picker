package g3.viewchoosephoto.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection.scanFile
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import g3.viewchoosephoto.AppConstant.DEFAULT_FOLDER_OUTPUT_TEMP
import g3.viewchoosephoto.AppConstant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
import g3.viewchoosephoto.AppConstant.PROVIDER
import g3.viewchoosephoto.AppConstant.REQUEST_CODE_CAMERA
import g3.viewchoosephoto.BuildConfig
import g3.viewchoosephoto.util.PermissionNewVideoUtils
import g3.viewchoosephoto.R
import g3.viewchoosephoto.di.AppComponent
import g3.viewchoosephoto.di.AppModule
import g3.viewchoosephoto.di.DaggerAppComponent
import g3.viewchoosephoto.model.AlbumImage
import g3.viewchoosephoto.model.LocalImage
import g3.viewchoosephoto.util.DialogUtil
import g3.viewchoosephoto.util.DialogUtil.showDenyDialog
import g3.viewchoosephoto.util.DialogUtil.showDialogConfirm
import g3.viewchoosephoto.util.FileUtils
import g3.viewchoosephoto.util.FunctionUtils.*
import g3.viewchoosephoto.util.ResizeView
import g3.viewchoosephoto.viewmodel.PhotoPickerViewModel
import kotlinx.android.synthetic.main.activity_photo_picker.*
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

class PhotoPickerActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adMobView: View
    private lateinit var mCurrentFrag: PhotoViewerFragment
    private var mLayoutManagerPhotoChoose: LinearLayoutManager? = null
    private var mAdapterPhotoChoose: PhotoChooseAdapter? = null
    private var pathSaveImageFromCamera: String? = null
    private var onClickBackButton: OnClickBackButton? = null
    private var onClickNextButton: OnClickNextButton? = null
    private lateinit var appComponent: AppComponent

    //Inject viewModel
    @Inject
    lateinit var mViewModel: PhotoPickerViewModel

    /**
     *Callback on user select image on image list recycler view
     *
     */
    private var onItemClick = object :
        ItemClickFromPagerFragment {
        override fun onItemClickInFragment(position: Int) {
            mViewModel.addImageToPhotoChoseList(position)
            updateViewChoseImageRv()
        }
    }

    /**
     *Callback on user remove image on chose images recycler view
     *
     */
    private var onRemoveItemClickItemPhotoListener =
        PhotoChooseAdapter.OnClickRemoveItemListener { position ->
            mViewModel.removeImage(position)
            mAdapterPhotoChoose!!.notifyDataSetChanged()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_picker)
        initDagger()
        onPermissionFolder()
        initView()
        resizeAdMobView()
        mViewModel.listLocalAlbum.observe(this, Observer {
            setUpViewPagerWithTabLayout(it as ArrayList<AlbumImage>)
        })
        mViewModel.listPhotoChose.observe(this, Observer {
            initChosenImageRecyclerView(it)
        })
        mViewModel.sizeOfListPhotoChose.observe(this, Observer {
            photo_picker_tv_number_of_chosen_photo.text = getString(R.string.text_number, it)
            setChosenImageRvVisibility(it)
        })
    }

    private fun initDagger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
        appComponent.inject(this)
    }

    /**
     *
     */
    private fun resizeAdMobView() {
        val screenDensity = ResizeView.getDisplayInfo().density
        val screenHeightInDp =
            ResizeView.getDisplayInfo().heightPixels.toDouble().roundToInt() / screenDensity
        val adMovViewParams = adMobView.layoutParams
        when {
            screenHeightInDp <= 400 -> {
                adMovViewParams.height = (32 * screenDensity).toInt()
            }
            screenHeightInDp <= 720 -> {
                adMovViewParams.height = (50 * screenDensity).toInt()
            }
            else -> {
                adMovViewParams.height = (90 * screenDensity).toInt()
            }
        }
    }

    private fun initView() {
        viewPager = findViewById(R.id.photo_picker_view_pager)
        tabLayout = findViewById(R.id.photo_picker_tab_layout_folder)
        adMobView = findViewById(R.id.photo_picker_adMob_container)
        photo_picker_iv_take_picture.setOnClickListener {
            PermissionNewVideoUtils.askForPermissionCamera(
                this,
                REQUEST_CODE_CAMERA
            ) {
                callCamera()
            }
        }
    }

    private fun onPermissionFolder() {
        PermissionNewVideoUtils.askForPermissionFolder(
            this,
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
        ) { }
    }

    private fun setUpViewPagerWithTabLayout(albumImages: ArrayList<AlbumImage>) {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return albumImages.size
            }

            override fun createFragment(position: Int): Fragment {
                mCurrentFrag = PhotoViewerFragment.newInstance(albumImages, position)
                mCurrentFrag.setListener(onItemClick)
                return mCurrentFrag
            }
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mViewModel.setFolderPosition(position)
                viewPager.currentItem = position
                super.onPageSelected(position)
            }
        })
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = albumImages[position].name
            }).attach()
    }

    private fun initChosenImageRecyclerView(listPhotoChose: List<LocalImage>) {
        mAdapterPhotoChoose = PhotoChooseAdapter(
            applicationContext,
            listPhotoChose
        )
        mAdapterPhotoChoose!!.setOnClickRemoveItemListener(onRemoveItemClickItemPhotoListener)
        mLayoutManagerPhotoChoose =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        photo_picker_rv_chosen_image.adapter = mAdapterPhotoChoose
        photo_picker_rv_chosen_image.layoutManager = mLayoutManagerPhotoChoose
        photo_picker_btn_back.setOnClickListener {
            onClickBackButton?.doOnClickBackButton()
        }
        photo_picker_fab_next.setOnClickListener {
            onClickNextButton?.doOnClickNextButton(listPhotoChose)
        }
        photo_picker_tv_number_of_chosen_photo.text =
            getString(R.string.text_number, listPhotoChose.size)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isEmpty()) return
        if (ActivityCompat.checkSelfPermission(
                this,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callCamera()
                }
            }
            if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mViewModel.loadData()
                }
            }
        } else {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    DialogUtil.openAppSettings(this, isCancel = false, isFinishActivity = false)
                }
            }
            if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    DialogUtil.openAppSettings(this, isCancel = false, isFinishActivity = true)
                    return
                }
                showDenyDialog(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                    isFinishActivity = true,
                    isCancel = false
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (FileUtils.fileExists(pathSaveImageFromCamera)) {
                    promptUserSaveImage()
                    mViewModel.addImageFromCamera(pathSaveImageFromCamera!!,false)
                    updateViewChoseImageRv()
                }
            } else {
                showDialogConfirm(
                    this,
                    message = R.string.save_image_fail,
                    idNo = R.string.empty,
                    idYes = R.string.ok,
                    isCancel = false,
                    onYes = DialogInterface.OnClickListener { dialogInterface, _ -> dialogInterface.dismiss() },
                    onNo = DialogInterface.OnClickListener { _, _ -> }
                )
            }
        }
    }


    private fun promptUserSaveImage() {
        showDialogConfirm(
            this,
            message = R.string.do_you_want_to_save_this_image,
            idNo = R.string.no,
            idYes = R.string.yes,
            isCancel = false,
            onYes = DialogInterface.OnClickListener { _, _ ->
                mViewModel.addImageFromCamera(pathSaveImageFromCamera!!, true)
                scanFile(
                    this, arrayOf(mViewModel.mTargetFile.absolutePath),
                    null
                ) { _, _ ->
                    mViewModel.reloadImageList()
                }
                viewPager.currentItem = 0
            },
            onNo = DialogInterface.OnClickListener { _, _ -> }
        )
    }

    private fun callCamera() {
        // save to cache dir
        val path = File(filesDir, "Temp")
        createFolder(path.absolutePath)
        val image =
            File(path, "image.jpg" + System.currentTimeMillis())
        pathSaveImageFromCamera = image.absolutePath
        createFolder(DEFAULT_FOLDER_OUTPUT_TEMP)
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = FileProvider.getUriForFile(
            this,
            PROVIDER,
            image
        )
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        val resInfoList =
            packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        try {
            startActivityForResult(
                i,
                REQUEST_CODE_CAMERA
            )
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        }
    }

    private fun setChosenImageRvVisibility(size: Int) {
        when {
            size >= 3 -> {
                container_rv_chosen_image.visibility = View.VISIBLE
                photo_picker_fab_next.visibility = View.VISIBLE
            }
            (size in 1..2) -> {
                photo_picker_fab_next.visibility = View.GONE
                container_rv_chosen_image.visibility = View.VISIBLE
            }
            else -> {
                photo_picker_fab_next.visibility = View.GONE
                container_rv_chosen_image.visibility = View.GONE
            }
        }
    }

    private fun updateViewChoseImageRv() {
        mLayoutManagerPhotoChoose?.scrollToPosition(
            mViewModel.sizeOfListPhotoChose.value!!.minus(1)
        )
        mAdapterPhotoChoose?.notifyDataSetChanged()
    }

    interface ItemClickFromPagerFragment {
        fun onItemClickInFragment(position: Int)
    }

    interface OnClickBackButton {
        fun doOnClickBackButton()
    }

    fun setListenerOnClickBackButton(listener: OnClickBackButton) {
        onClickBackButton = listener
    }

    interface OnClickNextButton {
        fun doOnClickNextButton(chosenImages: List<LocalImage>)
    }

    fun setListenerOnClickNextButton(listener: OnClickNextButton) {
        onClickNextButton = listener
    }
}
