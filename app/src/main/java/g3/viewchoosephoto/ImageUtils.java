package g3.viewchoosephoto;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

class ImageUtils {
    public static List<AlbumImage> getAllImage(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        List<LocalImage> mLocalImages = new ArrayList<>();

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor cc = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);

        if (cc != null && cc.moveToFirst()) {
            int count = cc.getCount();
            for (int i = 0; i < count; i++) {
                if (cc.isClosed()) break;

                cc.moveToPosition(i);
                int id = cc.getInt(cc.getColumnIndex(MediaStore.MediaColumns._ID));

                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
                String name = cc.getString(1);
                String path = cc.getString(0);
                if (!FunctionUtils.isBlank(path)) {
                    if (FileUtils.fileExists(path)) {
                        LocalImage localImage = new LocalImage();
                        localImage.setUri(uri);
                        localImage.setPath(path);
                        localImage.setName(name);
                        mLocalImages.add(localImage);
                    }
                }
            }
            //https://stackoverflow.com/a/23293930/938427
            if (!cc.isClosed()) {
                cc.close();
            }
        }
        return new ArrayList<>(getAlbumImage(mLocalImages));
    }

    private static List<AlbumImage> getAlbumImage(List<LocalImage> localImages) {
        List<AlbumImage> albumImages = new ArrayList<>();

        for (LocalImage localImage : localImages) {
            if (!checkAlbum(albumImages, localImage)) {
                List<LocalImage> localImagesAlbum = new ArrayList<>();
                localImagesAlbum.add(localImage);
                AlbumImage albumImage = new AlbumImage(localImagesAlbum, getAlbumName(localImage.getPath()));
                albumImage.setPath(getPathAlbum(localImage.getPath()));
                albumImages.add(albumImage);
            }
        }
        return albumImages;
    }

    private static String getAlbumName(String path) {
        String[] names = path.split("/");
        return names.length >= 2 ? names[names.length - 2] : names[0];
    }

    private static String getPathAlbum(String path) {
        String[] names = path.split("/");
        // LogUtils.d("ttt", "getPathAlbum: " + path + " : " + names.length + " : ");
        return path.substring(0, path.length() - names[names.length - 1].length());
    }

    private static boolean checkAlbum(List<AlbumImage> albumImages, LocalImage localImage) {
        boolean check = false;
        for (int i = 0; i < albumImages.size(); i++) {
            if (getAlbumName(localImage.getPath()).equals(albumImages.get(i).getName())) {
                check = true;
                albumImages.get(i).getLocalImages().add(localImage);
                break;
            }
            check = false;
        }
        return check;
    }
}
