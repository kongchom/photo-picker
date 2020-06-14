package g3.viewchoosephoto;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

class FunctionUtils {

    public static DisplayMetrics getDisplayInfo() {
        return Resources.getSystem().getDisplayMetrics();
    }

    public static int getCurrentSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static void displaySquareImage(Context context, ImageView imageView, Object url, int size) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(size, size);

        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(options)
                .into(imageView);
    }

    public static void displaySquareImageCenterInside(Context context, ImageView imageView, Object url, int size) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerInside()
                .override(size, size);

        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(options)
                .into(imageView);
    }

    public static void openAppSettings(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void createFolder(String dir) {
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Tests if a string is blank: null, empty, or only whitespace (" ", \r\n,
     * \t, etc)
     *
     * @param string A value input
     * @return true if string is blank
     */
    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }
        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!Character.isWhitespace(string.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }
}
