package g3.viewchoosephoto

import android.os.Environment

object AppConstant {
    const val REQUEST_CODE_CAMERA = 111
    const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102
    const val PROVIDER = "g3.viewchoosephoto.provider"

    private const val OUTPUT_FOLDER_NAME = "VideoMakerSlideshow"
    private val DEFAULT_FOLDER_OUTPUT =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .toString() + "/" + OUTPUT_FOLDER_NAME
    val DEFAULT_FOLDER_OUTPUT_TEMP = "$DEFAULT_FOLDER_OUTPUT/.Temp"

}