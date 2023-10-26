package cliqbuy.common

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import cliqbuy.BuildConfig
import cliqbuy.R
import cliqbuy.configs.AppController
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Constants
import cliqbuy.helper.SessionManager
import org.json.JSONObject
import java.io.*
import javax.inject.Inject

class CommonMethods {

    internal var mProgressDialog: Dialog? = null

    init {
        AppController.appComponent!!.inject(this)
    }

    @Inject
    lateinit var sessionManager: SessionManager
    fun isOnline(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    fun getJsonValue(jsonString: String, key: String, `object`: Any): Any {
        var object1 = `object`
        try {
            val jsonObject = JSONObject(jsonString)
            if (jsonObject.has(key)) object1 = jsonObject.get(key)
        } catch (e: Exception) {
            e.printStackTrace()
            return Any()
        }

        return object1
    }

    fun showToast(activity: Activity, message: String) {
        if (message.isNotEmpty())
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(activity: Activity, message: String) {
        if (message.isNotEmpty()) for (i in 0..7) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun encodeCaptureImage(context: Context, imageUri: Uri): String {

        val input = context.contentResolver.openInputStream(imageUri)
        val image = BitmapFactory.decodeStream(input, null, null)

        // Encode image to base64 string
        val bytes = ByteArrayOutputStream()
        image!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val imageBytes = bytes.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


    fun encodeLibraryImage(bitmap: Bitmap): String? {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteFormat: ByteArray = stream.toByteArray()
        return Base64.encodeToString(byteFormat, Base64.DEFAULT)
    }

    fun getDefaultFileName(context: Context): File {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            context.resources.getString(R.string.app_name) + System.currentTimeMillis() + ".png"
        )
    }

    fun galleryIntent(activity: AppCompatActivity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, Constants.SELECT_FILE)
    }

    fun cameraFilePath(context: Context): File {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            context.resources.getString(R.string.app_name) + System.currentTimeMillis() + ".png"
        )
    }

    fun cameraIntent(imageFile: File?, activity: AppCompatActivity) {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageUri = FileProvider.getUriForFile(
            activity,
            BuildConfig.APPLICATION_ID + ".provider",
            imageFile!!
        )
        try {
            val resolvedIntentActivities = activity.packageManager.queryIntentActivities(
                cameraIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (resolvedIntentInfo in resolvedIntentActivities) {

                val packageName = resolvedIntentInfo.activityInfo.packageName
                activity.grantUriPermission(
                    packageName,
                    imageUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            cameraIntent.putExtra("return-data", true)
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        activity.startActivityForResult(cameraIntent, Constants.PICK_IMAGE_REQUEST_CODE)
        refreshGallery(activity, imageFile)
    }

    fun refreshGallery(context: Context, file: File) {
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file) //out is your file you saved/deleted/moved/copied
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream?, output: FileOutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input!!.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }

    fun showMessage(activity: Activity, message: String) {

        val inflater: LayoutInflater = activity.layoutInflater

        val dialoglayout: View = inflater.inflate(R.layout.dialog_show_message, null)
        val builder = AlertDialog.Builder(activity, R.style.customDialog)
        builder.setCancelable(true)
        builder.setView(dialoglayout)

        val text = dialoglayout.findViewById(R.id.tv_custom_message) as TextView
        val cancelbtn: Button = dialoglayout.findViewById(R.id.button_cancel) as Button
        val ll_buttons: LinearLayout =
            dialoglayout.findViewById(R.id.common_buttons) as LinearLayout
        val ok_common: Button = dialoglayout.findViewById(R.id.button_common_ok) as Button
        text.text = message
        ll_buttons.visibility = View.GONE
        ok_common.visibility = View.VISIBLE

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
        ok_common.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    fun showCustomDialog(
        resId: Int,
        layoutInflater: LayoutInflater,
        context: Context,
    ): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context, R.style.DialogStyle)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(resId, null)
        dialogBuilder.setView(dialogView)
        return dialogBuilder.create()
    }

    fun hideProgressDialog() {

        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog?.dismiss()
        }
    }

    fun showProgressDialog(context: Context) {
        try {
            mProgressDialog = getDialog(context, R.layout.app_loader_view)
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event -> keyCode == KeyEvent.KEYCODE_BACK })
            if (!mProgressDialog!!.isShowing) {
                try {
                    mProgressDialog?.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun copyContentToClipboard(mContext: Context, textToBeCopied: String) {
        val cManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val cData = ClipData.newPlainText("text", textToBeCopied)
        if (!TextUtils.isEmpty(textToBeCopied)) {
            cManager.setPrimaryClip(cData)
            showUserMessage(mContext.resources.getString(R.string.link_copied))
        } else {
            showUserMessage(mContext.resources.getString(R.string.link_not_copied))
        }

    }

    fun getLangCode(): String {
        return if (sessionManager.languageCode == "ar") {
            "sa"
        } else {
            "en"
        }
    }

    fun showUserMessage(message: String?) {
        try {
            if (!TextUtils.isEmpty(message)) {
                Toast.makeText(AppController.contexts, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getDialog(mContext: Context, mLayout: Int): Dialog {
        val mDialog = Dialog(mContext)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        mDialog.setContentView(mLayout)
        mDialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        mDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        return mDialog

    }

    fun dynamicTextColor(context: Context, attributeSet: Int): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attributeSet, value, true)
        return value.data
    }

    fun rotateArrow(ivArrow: ImageView, context: Context) {
        if (context.resources.getString(R.string.layout_direction) == "1") {
            ivArrow.rotation = 180f
        } else {
            ivArrow.rotation = 0f
        }
    }

    companion object {

        val appPackageName: String
            get() {
                var packageName: String
                try {
                    packageName = AppController.contexts.packageName
                } catch (e: Exception) {
                    packageName = ""
                    e.printStackTrace()
                }

                return packageName
            }

        fun DebuggableLogE(tag: String, message: String) {
            try {
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (CommonKeys.isLoggable!!) {
                Log.e(tag, message)
            }
        }

        fun DebuggableLogE(tag: String, message: String, tr: Throwable) {
            try {
                if (CommonKeys.isLoggable!!) {
                    Log.e(tag, message, tr)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun DebuggableLogI(tag: String, message: String?) {
            try {
                if (CommonKeys.isLoggable!!) {
                    Log.i(tag, message!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun DebuggableLogD(tag: String, message: String) {
            try {
                if (CommonKeys.isLoggable!!) {
                    Log.d(tag, message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun DebuggableLogV(tag: String, message: String) {
            try {
                if (CommonKeys.isLoggable!!) {
                    Log.v(tag, message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }
}