package g3.viewchoosephoto.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import g3.viewchoosephoto.PermissionListener;

public class PermissionNewVideoUtils {

    public static void askForPermissionCamera(Activity activity, int requestCode, PermissionListener permissionListener) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
                } else {
                    activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
                }
            }
        } else {
            permissionListener.onDonePermission();
        }
    }
    public static void askForPermissionFolder(Activity activity, int requestCode, PermissionListener permissionListener) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                } else {
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
            }
        } else {
            permissionListener.onDonePermission();
        }
    }
}
