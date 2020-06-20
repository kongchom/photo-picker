package g3.viewchoosephoto.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import g3.viewchoosephoto.R

object DialogUtil {

    fun showDialogConfirm(
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

    fun showDenyDialog(
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

    fun showDenyDialog(
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

    fun showRememberDialog(
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

    fun openAppSettings(
        context: Context,
        isCancel: Boolean,
        isFinishActivity: Boolean
    ) {
        showRememberDialog(
            context as Activity,
            DialogInterface.OnClickListener { _, _ ->
                FunctionUtils.openAppSettings(
                    context,
                    context.getPackageName()
                )
            },
            DialogInterface.OnClickListener { _, _ -> if (isFinishActivity) context.finish() },
            isCancel
        )
    }
}