package g3.viewchoosephoto;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends Activity implements View.OnClickListener,
        FolderAdapter.OnClickItemFolderListener,
        PhotoAdapter.OnClickItemPhotoListener,
        PhotoChooseAdapter.OnClickRemoveItemListener{
    private String TAG = "MainActivity1";

    private LinearLayout lnListPhotoBack;
    private ImageView mImgBack;

    private LinearLayout lnCallCamera;
    private ImageView mImgPhotograph;

    private TextView mTvNameFolder;
    private TextView mTvPlease;
    private TextView mTvNumber;
    private TextView mTvNote;
    private TextView mTvStart;

    private RelativeLayout mRlContainerPlease;
    private RelativeLayout mrlSelectNumber;

    private RecyclerView mRecyclerFolder;
    private LinearLayoutManager mLayoutManagerFolder;
    private FolderAdapter mAdapterFolder;
    private List<AlbumImage> mAlbumImages = new ArrayList<>();

    private RecyclerView mRecyclerPhotoChoose;
    private LinearLayoutManager mLayoutManagerPhotoChoose;
    private PhotoAdapter mAdapterPhoto;
    private List<LocalImage> mLocalImages = new ArrayList<>();

    private RecyclerView mRecyclerPhoto;
    private GridLayoutManager mLayoutManagerPhoto;
    private PhotoChooseAdapter mAdapterPhotoChoose;
    private ArrayList<LocalImage> mPhotoChoose = new ArrayList<>();

    private String pathSaveImageFromCamera;

    public static final int REQUEST_CODE_CAMERA = 111;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

    public static final String OUTPUT_FOLDER_NAME = "VideoMakerSlideshow";
    public final static String DEFAULT_FOLDER_OUTPUT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + OUTPUT_FOLDER_NAME;
    public final static String DEFAULT_FOLDER_OUTPUT_TEMP = DEFAULT_FOLDER_OUTPUT + "/.Temp";

    public static final String PROVIDER = "g3.viewchoosephoto.provider";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void initView() {
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgPhotograph = (ImageView) findViewById(R.id.iv_Photograph);
        lnCallCamera = (LinearLayout) findViewById(R.id.ln_call_camera);

        mTvNameFolder = (TextView) findViewById(R.id.tvNameFolder);
        mTvPlease = (TextView) findViewById(R.id.tvPlease);
        mTvNumber = (TextView) findViewById(R.id.tvNumber);
        mTvNote = findViewById(R.id.list_photo_tv_note);

        mRlContainerPlease = (RelativeLayout) findViewById(R.id.rlContainerPlease);
        mrlSelectNumber = (RelativeLayout) findViewById(R.id.rl_select_number);
        mTvStart = (TextView) findViewById(R.id.tv_star_list_photo);
        lnListPhotoBack = findViewById(R.id.ln_list_photo_back);
        lnListPhotoBack.setOnClickListener(this);

        mRecyclerFolder = (RecyclerView) findViewById(R.id.recyclerFolder);
        mLayoutManagerFolder = new LinearLayoutManager(this);
        mAdapterFolder = new FolderAdapter(this, mAlbumImages);
        mRecyclerFolder.setLayoutManager(mLayoutManagerFolder);
        mRecyclerFolder.setAdapter(mAdapterFolder);
        mAdapterFolder.setOnClickItemFolderListener(this);

        mRecyclerPhoto = (RecyclerView) findViewById(R.id.recyclerPhoto);
        mLayoutManagerPhoto = new GridLayoutManager(this, 3);
        mAdapterPhoto = new PhotoAdapter(this, mLocalImages);
        mRecyclerPhoto.setLayoutManager(mLayoutManagerPhoto);
        mRecyclerPhoto.setAdapter(mAdapterPhoto);
        mAdapterPhoto.setOnClickItemPhotoListener(this);

        mRecyclerPhotoChoose = (RecyclerView) findViewById(R.id.recyclerPhotoChoose);
        mLayoutManagerPhotoChoose = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAdapterPhotoChoose = new PhotoChooseAdapter(this, mPhotoChoose);
        mRecyclerPhotoChoose.setLayoutManager(mLayoutManagerPhotoChoose);
        mRecyclerPhotoChoose.setAdapter(mAdapterPhotoChoose);
        mAdapterPhotoChoose.setOnClickRemoveItemListener(this);

        lnCallCamera.setOnClickListener(this);
        mTvStart.setOnClickListener(this);

        onPermissionFolder();

        initControl();
    }

    public void initControl() {
        mRecyclerFolder.setVisibility(View.VISIBLE);
        mRecyclerPhoto.setVisibility(View.GONE);
        mRlContainerPlease.setVisibility(View.GONE);
        mTvPlease.setVisibility(View.VISIBLE);
    }

    public void onPermissionFolder() {
        Log.e(TAG,"onPermissionFolder start");
        PermissionNewVideoUtils.askForPermissionFolder(this,
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                new PermissionListener() {
                    @Override
                    public void onDonePermission() {
                        getDummyFolder();
                    }
                });
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onClickItem(int position) {
        mRecyclerPhoto.setVisibility(View.VISIBLE);
        mRecyclerFolder.setVisibility(View.GONE);
        mTvNameFolder.setText(mAlbumImages.get(position).getName());

        mLocalImages.clear();
        mLocalImages.addAll(mAlbumImages.get(position).getLocalImages());
        mAdapterPhoto.notifyDataSetChanged();
    }

    @Override
    public void onClickItemPhoto(int position) {
        if (mPhotoChoose.size() < 60) {
            mPhotoChoose.add(mLocalImages.get(position));
            mLayoutManagerPhotoChoose.scrollToPosition(mPhotoChoose.size() - 1);
            mAdapterPhotoChoose.notifyDataSetChanged();

            if (mPhotoChoose.isEmpty()) {
                mTvPlease.setVisibility(View.VISIBLE);
                mRlContainerPlease.setVisibility(View.GONE);
            } else {
                mTvPlease.setVisibility(View.GONE);
                mRlContainerPlease.setVisibility(View.VISIBLE);
                mTvNumber.setText(getString(R.string.text_number, mPhotoChoose.size()));
            }
        }
    }

    @Override
    public void onRemoveItem(int position) {
        if (position < mPhotoChoose.size()) {
            mPhotoChoose.remove(position);
        }
        mAdapterPhotoChoose.notifyDataSetChanged();

        if (mPhotoChoose.isEmpty()) {
            mTvPlease.setVisibility(View.VISIBLE);
            mRlContainerPlease.setVisibility(View.GONE);
        } else {
            mTvPlease.setVisibility(View.GONE);
            mRlContainerPlease.setVisibility(View.VISIBLE);
            mTvNumber.setText(getString(R.string.text_number, mPhotoChoose.size()));
        }
    }

    /**
     *
     * @param mActivity
     * @param permission
     * @param REQUEST_CODE
     */
    public static void requestPermission(final Activity mActivity, final String permission, final int REQUEST_CODE) {
        List<String> permissionsNeeded = new ArrayList<String>();
        permissionsNeeded.add(permission);
        ActivityCompat.requestPermissions(mActivity, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_CODE);

//        if (FunctionUtils.getCurrentSdkVersion() >= 23) {
//            List<String> permissionsNeeded = new ArrayList<String>();
//            permissionsNeeded.add(permission);
//            ActivityCompat.requestPermissions(mActivity, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_CODE);
//        }
    }

    public static void showDialogConfirm(Activity activity, int message,
                                  int idYes,
                                  int idNo,
                                  boolean isCancel,
                                  DialogInterface.OnClickListener onYes,
                                  DialogInterface.OnClickListener onNo) {
        if (activity != null && !activity.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(isCancel);
            builder.setMessage(message);
            builder.setPositiveButton(idYes, onYes);
            builder.setNegativeButton(idNo, onNo);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public static void showDenyDialog(final Activity activity,
                                      DialogInterface.OnClickListener onRetry,
                                      DialogInterface.OnClickListener onCancel, boolean isCancel) {
        showDialogConfirm(activity, R.string.app_name,
                R.string.app_name, R.string.app_name, isCancel, onRetry, onCancel);
    }

    public static void showDenyDialog(final Context context, final String permission, final int requestCode, final boolean isFinishActivity, boolean isCancel) {
        showDenyDialog((Activity) context, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermission((Activity) context, permission, requestCode);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinishActivity)
                    ((Activity) context).finish();
            }
        }, isCancel);
    }

    public static void showRememberDialog(final Activity activity,
                                          DialogInterface.OnClickListener onSettings,
                                          DialogInterface.OnClickListener onCancel, boolean isCancel) {
        showDialogConfirm(activity, R.string.app_name,
                R.string.app_name, R.string.app_name, isCancel, onSettings, onCancel);
    }

    public static void openAppSettings(final Context context, final boolean isCancel, final boolean isFinishActivity) {
        showRememberDialog((Activity) context, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FunctionUtils.openAppSettings(context, context.getPackageName());
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinishActivity)
                    ((Activity) context).finish();
            }
        }, isCancel);
    }

    private void callCamera() {
        // save to cache dir
        File path = new File(getFilesDir(), "Temp");
        FunctionUtils.createFolder(path.getAbsolutePath());
        File image = new File(path, "image.jpg" + System.currentTimeMillis());
        pathSaveImageFromCamera = image.getAbsolutePath();

        FunctionUtils.createFolder(DEFAULT_FOLDER_OUTPUT_TEMP);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(
                MainActivity1.this,
                PROVIDER,
                image);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        try {
            startActivityForResult(i, REQUEST_CODE_CAMERA);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void getDummyFolder() {
        List<AlbumImage> albumImages = ImageUtils.getAllImage(this);
        if (albumImages != null) {
            mAlbumImages.addAll(albumImages);
            mAdapterFolder.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions.length == 0) return;
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callCamera();
                }
            }

            if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getDummyFolder();
                }
            }
        } else {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    openAppSettings(MainActivity1.this, false, false);
                }
            }
            if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    openAppSettings(MainActivity1.this, false, true);
                    return;
                }

                showDenyDialog(MainActivity1.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                        true,
                        false);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                LocalImage localImage = new LocalImage();
                localImage.setPath(pathSaveImageFromCamera);
                if (mPhotoChoose.size() < 60) {
                    mPhotoChoose.add(localImage);
                    mLayoutManagerPhotoChoose.scrollToPosition(mPhotoChoose.size() - 1);
                    mAdapterPhotoChoose.notifyDataSetChanged();

                    if (mPhotoChoose.isEmpty()) {
                        mTvPlease.setVisibility(View.VISIBLE);
                        mRlContainerPlease.setVisibility(View.GONE);
                    } else {
                        mTvPlease.setVisibility(View.GONE);
                        mRlContainerPlease.setVisibility(View.VISIBLE);
                        mTvNumber.setText(getString(R.string.text_number, mPhotoChoose.size()));
                    }
                }
            }
        }
    }
}
