package cliqbuy.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


import java.util.Objects

import butterknife.ButterKnife
import butterknife.BindView
import butterknife.OnClick
import cliqbuy.BuildConfig
import cliqbuy.R
import cliqbuy.databinding.FragmentHomeBinding


class RuntimePermissionDialogFragment : DialogFragment() {


    val cameraIcon = android.R.drawable.ic_menu_camera
    val locationIcon = android.R.drawable.ic_menu_mylocation
    val storageIcon = android.R.drawable.ic_menu_gallery
    val contactIcon = android.R.drawable.ic_menu_call
    val defaultIcon = cameraIcon

    private var requestCodeForCallbackIdentificationSubDivision: Int = 0

    lateinit var confirm : Button
    lateinit var decline : Button
    lateinit var tvMessage : TextView


    private val PERMISSION_REQUEST_CODE = 11
    private var permissionsRequestFor: Array<String>? = null
    private var mContext: Context? = null
    private var callbackListener: RuntimePermissionRequestedCallback? = null
    private var permissionIcon: Int = 0 // default 0
    private var permissionDescription: String? = null


    // this variable is declared to handle the allow Textview onClick process Dynamically,
    // if true -> it will request permission,
    // else open settings page to grand permission by user manually
    protected var ableToRequestPermission = true
    lateinit var binding: FragmentHomeBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragmenet_dialog, container, false)
        isCancelable = false
        Objects.requireNonNull(dialog!!.window)?.setBackgroundDrawableResource(android.R.color.transparent)
        confirm = rootView!!.findViewById<View>(R.id.btn_dialog_cancel) as Button
        decline = rootView.findViewById<View>(R.id.btn_dialog_ok) as Button
        tvMessage = rootView.findViewById<View>(R.id.tv_dialog_message) as TextView

        confirm.text = getString(R.string.continue_alert)
        decline.text = getString(R.string.not_now)
        showPermissionPopup()
        tvMessage.text = permissionDescription
        //setImageResourceAndPermissionDescriptionForPopup()
        return rootView
    }

    override fun onResume() {
        super.onResume()


    }
     fun showPermissionPopup() {
        getPermissionRequestedForIconAndDescription()


        confirm.setOnClickListener {
            allowPermission()
        }

        decline.setOnClickListener {
            notAllowPermission()
        }

    }

    fun notAllowPermission() {
        afterPermissionDenied()
    }

    fun allowPermission() {
        if (ableToRequestPermission) {
            requestNecessaryPermissions()
        } else {
            mContext!!.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)))
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
        callbackListener = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (permissions.isNotEmpty() && grantResults.isNotEmpty()) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]

                if (!shouldShowRequestPermissionRationale(permission) && grantResult != PackageManager.PERMISSION_GRANTED) {
                    notAbleToRequestPermission()
                    return
                } else if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    afterPermissionDenied()
                    return
                }
            }
            callbackListener!!.permissionGranted(requestCodeForCallbackIdentifications, requestCodeForCallbackIdentificationSubDivision)
            dismiss()

        } else {
          //  Toast.makeText(mContext, "permission size 0", Toast.LENGTH_SHORT).show()
        }
    }


    fun getPermissionRequestedForIconAndDescription() {
        when (permissionsRequestFor!![0]) {
            CAMERA_PERMISSION -> {
                permissionIcon = cameraIcon
                permissionDescription = mContext!!.resources.getString(R.string.camera_permission_description)+" "+getString(R.string.app_name)+" "+ getString(R.string.to_access_camera_and_storage)
            }
            WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                permissionIcon = storageIcon
                permissionDescription = mContext!!.resources.getString(R.string.storage_permission_description)+" "+getString(R.string.app_name)+" "+getString(R.string.to_access_storage)
            }
            LOCATION_PERMISSION -> {
                permissionIcon = locationIcon
                permissionDescription = mContext!!.resources.getString(R.string.location_permission_description)+" "+getString(R.string.app_name)+" "+getString(R.string.to_access_location)
            }
            CONTACT_PERMISSION -> {
                permissionIcon = contactIcon
                permissionDescription = mContext!!.resources.getString(R.string.contact_permission_description)+" "+getString(R.string.app_name)+" "+getString(R.string.to_access_contacts)
            }
            PHONE_STATE -> {
                permissionIcon = contactIcon
                permissionDescription = mContext!!.resources.getString(R.string.audio_permission_description)+" "+getString(R.string.app_name)+" "+getString(R.string.to_access_your_audio)
            }
            else -> {
                permissionIcon = cameraIcon
                permissionDescription = mContext!!.resources.getString(R.string.camera_permission_description)+" "+getString(R.string.app_name)+" "+ getString(R.string.to_access_camera_and_storage)
            }
        }
    }

    private fun requestNecessaryPermissions() {
        ableToRequestPermission = true
        requestPermissions(permissionsRequestFor!!, PERMISSION_REQUEST_CODE)
    }

    private fun notAbleToRequestPermission() {
        confirm.text =mContext!!.resources.getString(R.string.settings)
        //permissionAllow.text = mContext!!.resources.getString(R.string.settings)
        ableToRequestPermission = false

    }

    private fun afterPermissionDenied() {
        showPermissionDeniedMessageToUser()
        callbackListener!!.permissionDenied(requestCodeForCallbackIdentifications, requestCodeForCallbackIdentificationSubDivision)
        dismiss()
    }

    fun showPermissionDeniedMessageToUser() {
        mContext!!.showToast(resources.getString(R.string.enable_permissions_to_proceed_further))
9
    }

    interface RuntimePermissionRequestedCallback {
        fun permissionGranted(requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int)

        fun permissionDenied(requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int)
    }

    companion object {

        val CAMERA_PERMISSION = Manifest.permission.CAMERA
        val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        val CONTACT_PERMISSION = Manifest.permission.READ_CONTACTS
        val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
        val MODIFY_AUDIO_PERMISSION = Manifest.permission.MODIFY_AUDIO_SETTINGS
        val DEFAULT_PERMISSION_CODE = Manifest.permission.INTERNET
        val PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        @RequiresApi(33)
        val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
        val READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        @RequiresApi(33)
        val MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES

        val CAMERA_PERMISSION_ARRAY =  if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(CAMERA_PERMISSION,MEDIA_IMAGES)
        } else {
            arrayOf(CAMERA_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION, READ_EXTERNAL_STORAGE_PERMISSION)
        }

        // request Codes For Callback Identifications
        const val cameraAndGallaryCallBackCode = 0
        const val externalStoreageCallbackCode = 1
        const val locationCallbackCode = 2
        const val contactCallbackCode = 3
        const  val audioCallbackCode = 4
        private var requestCodeForCallbackIdentifications = 0
        val runtimePermissionDialogFragment = RuntimePermissionDialogFragment()

        fun checkPermissionStatus(mContext: Context, fragmentManager: FragmentManager, callbackListener: RuntimePermissionRequestedCallback, permissionsRequestFor: Array<String>, requestCodeForCallbackIdentification: Int, requestCodeForCallbackIdentificationSubDivision: Int) {
            requestCodeForCallbackIdentifications = requestCodeForCallbackIdentification
            /*
         * here function check permission status and then checks shouldAskPermissionForThisAndroidOSVersion or not
         * because some custom phone below Android M request permission from user at Run time example: redmi phones*/

            var allPermissionGranted: Boolean? = true

            for (permissionRequestFor in permissionsRequestFor) {
                if (checkSelfPermissions(mContext, permissionRequestFor)) {
                    allPermissionGranted = false
                    break
                }
            }
            if (!(allPermissionGranted)!!) {
                if (shouldAskPermissionForThisAndroidOSVersion()) {
                    runtimePermissionDialogFragment.permissionsRequestFor = permissionsRequestFor
                    runtimePermissionDialogFragment.callbackListener = callbackListener
                    runtimePermissionDialogFragment.requestCodeForCallbackIdentificationSubDivision = requestCodeForCallbackIdentificationSubDivision
                    runtimePermissionDialogFragment.mContext = mContext
                    runtimePermissionDialogFragment.show(fragmentManager, RuntimePermissionDialogFragment::class.java.name)
                } else {
                    //                we write code here becoz of static method, it not static method we call afterPermissionDenied()
                    callbackListener.permissionDenied(requestCodeForCallbackIdentification, requestCodeForCallbackIdentificationSubDivision)
                    Toast.makeText(mContext, mContext.resources.getString(R.string.enable_permissions_to_proceed_further), Toast.LENGTH_SHORT).show()

                }
            } else {
                callbackListener.permissionGranted(requestCodeForCallbackIdentification, requestCodeForCallbackIdentificationSubDivision)
            }
        }

        fun checkSelfPermissions(mContext: Context, permissionRequestFor: String): Boolean {
            return ContextCompat.checkSelfPermission(mContext, permissionRequestFor) != PackageManager.PERMISSION_GRANTED
        }

        fun shouldAskPermissionForThisAndroidOSVersion(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }

        fun dismissDialog() {
            if (runtimePermissionDialogFragment.isVisible)
                runtimePermissionDialogFragment.dismiss()
        }
    }
}
