package g3.viewchoosephoto.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection.scanFile
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import g3.viewchoosephoto.util.PermissionNewVideoUtils
import g3.viewchoosephoto.adapter.PhotoChooseAdapter
import g3.viewchoosephoto.R
import g3.viewchoosephoto.model.AlbumImage
import g3.viewchoosephoto.model.LocalImage
import g3.viewchoosephoto.util.FileUtils
import g3.viewchoosephoto.util.FunctionUtils.*
import g3.viewchoosephoto.util.ImageUtils
import g3.viewchoosephoto.util.ResizeView
import kotlinx.android.synthetic.main.activity_photo_picker.*
import java.io.File
import kotlin.math.roundToInt

class PhotoPickerActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adMobView: View
    private var mAlbumImages: ArrayList<AlbumImage>? = ArrayList()
    private var mPhotoChoose: ArrayList<LocalImage> = ArrayList()
    private var mFolderPosition: Int = 0
    private lateinit var mCurrentFrag: PhotoViewerFragment
    private var mLayoutManagerPhotoChoose: LinearLayoutManager? = null
    private var mAdapterPhotoChoose: PhotoChooseAdapter? = null
    private var pathSaveImageFromCamera: String? = null
    private var onClickBackButton: OnClickBackButton? = null
    private var onClickNextButton: OnClickNextButton? = null

    companion object {
        const val REQUEST_CODE_CAMERA = 111
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102
        private const val OUTPUT_FOLDER_NAME = "VideoMakerSlideshow"
        private val DEFAULT_FOLDER_OUTPUT =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .toString() + "/" + OUTPUT_FOLDER_NAME
        val DEFAULT_FOLDER_OUTPUT_TEMP = "$DEFAULT_FOLDER_OUTPUT/.Temp"

        const val PROVIDER = "g3.viewchoosephoto.provider"
    }

    /**
     *
     *
     */
    private var onItemClick = object :
        ItemClickFromPagerFragment {
        override fun onItemClickInFragment(position: Int) {
            if (mPhotoChoose.size < 60) {
                mPhotoChoose.add(mAlbumImages?.get(mFolderPosition)!!.localImages[position])
                container_rv_chosen_image.visibility = View.VISIBLE
                photo_picker_tv_number_of_chosen_photo.text = getString(R.string.text_number,mPhotoChoose.size)
                if (mPhotoChoose.size >= 3) {
                    photo_picker_fab_next.visibility = View.VISIBLE
                }
                mLayoutManagerPhotoChoose?.scrollToPosition(mPhotoChoose.size - 1)
                mAdapterPhotoChoose?.notifyDataSetChanged()
            }
        }
    }

    private var onRemoveItemClickItemPhotoListener =
        PhotoChooseAdapter.OnClickRemoveItemListener { position ->
            if (position < mPhotoChoose.size) {
                mPhotoChoose.removeAt(position)
                photo_picker_tv_number_of_chosen_photo.text = getString(R.string.text_number,mPhotoChoose.size)
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
        initView()
        resizeAdMobView()
        onPermissionFolder()
        initChosenImageRecyclerView()
    }

    /**
     *
     */
    private fun resizeAdMobView() {
        val screenDensity = ResizeView.getDisplayInfo().density
        Log.d("congnm","screen density: $screenDensity")
        val screenHeightInDp = ResizeView.getDisplayInfo().heightPixels.toDouble().roundToInt() / screenDensity
        val adMovViewParams = adMobView.layoutParams
        if (screenHeightInDp <= 400) {
            adMovViewParams.height = (32 * screenDensity).toInt()
        } else if (screenHeightInDp <= 720) {
            adMovViewParams.height = (50 * screenDensity).toInt()
        } else {
            adMovViewParams.height = (90 * screenDensity).toInt()
        }
    }

    private fun initView() {
        viewPager = findViewById(R.id.photo_picker_view_pager)
        tabLayout = findViewById(R.id.photo_picker_tab_layout_folder)
        adMobView = findViewById(R.id.photo_picker_adMob_container)
    }

    private fun onPermissionFolder() {
        PermissionNewVideoUtils.askForPermissionFolder(
            this,
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
        ) { initData() }
    }

    private fun initData() {
        mAlbumImages = ImageUtils.getAllImage(applicationContext) as ArrayList<AlbumImage>?
        setUpViewPagerWithTabLayout()
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mAlbumImages!!.size
            }

            override fun createFragment(position: Int): Fragment {
                mCurrentFrag = PhotoViewerFragment.newInstance(mAlbumImages!!, position)
                Log.d("congnm","createFragmentPager $position")
                mCurrentFrag.setListener(onItemClick)
                return mCurrentFrag
            }
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mFolderPosition = position
                viewPager.currentItem = position
                super.onPageSelected(position)
            }
        })
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = mAlbumImages!![position].name
            }).attach()
    }

    private fun initChosenImageRecyclerView() {
        mAdapterPhotoChoose = PhotoChooseAdapter(
            applicationContext,
            mPhotoChoose
        )
        mAdapterPhotoChoose!!.setOnClickRemoveItemListener(onRemoveItemClickItemPhotoListener)
        mLayoutManagerPhotoChoose =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        photo_picker_rv_chosen_image.adapter = mAdapterPhotoChoose
        photo_picker_rv_chosen_image.layoutManager = mLayoutManagerPhotoChoose
        photo_picker_iv_take_picture.setOnClickListener {
            PermissionNewVideoUtils.askForPermissionCamera(this,
                REQUEST_CODE_CAMERA
            ) {
                callCamera()
            }
        }
        photo_picker_btn_back.setOnClickListener {
            onClickBackButton?.doOnClickBackButton()
        }
        photo_picker_fab_next.setOnClickListener {
            onClickNextButton?.doOnClickNextButton(mPhotoChoose)
        }
        photo_picker_tv_number_of_chosen_photo.text = getString(R.string.text_number,mPhotoChoose.size)
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
                    initData()
                }
            }
        } else {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    openAppSettings(this, isCancel = false, isFinishActivity = false)
                }
            }
            if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    openAppSettings(this, isCancel = false, isFinishActivity = true)
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

    /**
     *
     * @param mActivity
     * @param permission
     * @param REQUEST_CODE
     */
    private fun requestPermission(
        mActivity: Activity?,
        permission: String,
        REQUEST_CODE: Int
    ) {
        val permissionsNeeded: MutableList<String> =
            java.util.ArrayList()
        permissionsNeeded.add(permission)
        ActivityCompat.requestPermissions(
            mActivity!!,
            permissionsNeeded.toTypedArray(),
            REQUEST_CODE
        )
    }

    /**
     *
     */
    private fun showDialogConfirm(
        activity: Activity?, message: Int,
        idYes: Int,
        idNo: Int,
        isCancel: Boolean,
        onYes: DialogInterface.OnClickListener?,
        onNo: DialogInterface.OnClickListener?
    ) {
        if (activity != null && !activity.isFinishing) {
            val builder =
                AlertDialog.Builder(activity)
            builder.setCancelable(isCancel)
            builder.setMessage(message)
            builder.setPositiveButton(idYes, onYes)
            builder.setNegativeButton(idNo, onNo)
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun showDenyDialog(
        activity: Activity?,
        onRetry: DialogInterface.OnClickListener?,
        onCancel: DialogInterface.OnClickListener?, isCancel: Boolean
    ) {
        showDialogConfirm(
            activity,
            R.string.app_name,
            R.string.app_name,
            R.string.app_name, isCancel, onRetry, onCancel
        )
    }

    private fun showDenyDialog(
        context: Context,
        permission: String,
        requestCode: Int,
        isFinishActivity: Boolean,
        isCancel: Boolean
    ) {
        showDenyDialog(
            context as Activity,
            DialogInterface.OnClickListener { _, _ ->
                requestPermission(
                    context, permission, requestCode
                )
            },
            DialogInterface.OnClickListener { _, _ -> if (isFinishActivity) context.finish() },
            isCancel
        )
    }

    private fun showRememberDialog(
        activity: Activity?,
        onSettings: DialogInterface.OnClickListener?,
        onCancel: DialogInterface.OnClickListener?, isCancel: Boolean
    ) {
        showDialogConfirm(
            activity,
            R.string.app_name,
            R.string.app_name,
            R.string.app_name, isCancel, onSettings, onCancel
        )
    }

    private fun openAppSettings(
        context: Context,
        isCancel: Boolean,
        isFinishActivity: Boolean
    ) {
        showRememberDialog(
            context as Activity,
            DialogInterface.OnClickListener { _, _ ->
                openAppSettings(
                    context,
                    context.getPackageName()
                )
            },
            DialogInterface.OnClickListener { _, _ -> if (isFinishActivity) context.finish() },
            isCancel
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                val localImage = LocalImage()
                localImage.path = pathSaveImageFromCamera
                if (FileUtils.fileExists(pathSaveImageFromCamera)) {
                    promptUserSaveImage()
                    if (mPhotoChoose.size < 60) {
                        mPhotoChoose.add(localImage)
                        container_rv_chosen_image.visibility = View.VISIBLE
                        photo_picker_tv_number_of_chosen_photo.text =
                            getString(R.string.text_number, mPhotoChoose.size)
                        if (mPhotoChoose.size >= 3) {
                            photo_picker_fab_next.visibility = View.VISIBLE
                        }
                        mLayoutManagerPhotoChoose!!.scrollToPosition(mPhotoChoose.size - 1)
                        mAdapterPhotoChoose!!.notifyDataSetChanged()
                    }
                } else {
                    showDialogConfirm(
                        this,
                        message = R.string.save_image_fail,
                        idNo = R.string.empty,
                        idYes = R.string.ok,
                        isCancel = false,
                        onYes = DialogInterface.OnClickListener { dialogInterface, _ -> dialogInterface.dismiss() },
                        onNo = DialogInterface.OnClickListener { _, _ ->  }
                    )
                }
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
            onYes = DialogInterface.OnClickListener { _, _ -> onYesSaveImage() },
            onNo = DialogInterface.OnClickListener { _, _ ->  }
            )
    }

    private fun onYesSaveImage() {
        val targetFile = File(this.mAlbumImages?.get(0)!!.path + System.currentTimeMillis() + ".jpg")
        Log.d("congnm","targetFilePath = ${targetFile.absolutePath} - pathSaveImage: $pathSaveImageFromCamera")
            File(pathSaveImageFromCamera!!).copyTo(
                target = targetFile,
                overwrite = false
            )
            scanFile(
                this, arrayOf(targetFile.absolutePath),
                null
            ) { path, uri ->
                mAlbumImages = ImageUtils.getAllImage(applicationContext) as ArrayList<AlbumImage>?
                Log.d("congnm", "onYesSaveImage ${viewPager.currentItem}")
            }
            viewPager.currentItem = 0
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
            startActivityForResult(i,
                REQUEST_CODE_CAMERA
            )
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        }
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
        fun doOnClickNextButton(chosenImages: ArrayList<LocalImage>)
    }

    fun setListenerOnClickNextButton(listener: OnClickNextButton) {
        onClickNextButton = listener
    }
}
