package cliqbuy.ui.view



import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityProfileBinding
import cliqbuy.helper.CommonKeys.fragmentType
import cliqbuy.helper.Constants.PICK_IMAGE_REQUEST_CODE
import cliqbuy.helper.Constants.SELECT_FILE
import cliqbuy.helper.Enums
import cliqbuy.helper.Enums.REQ_DELETE_PROFILE
import cliqbuy.helper.Enums.REQ_UPDATE_PROFILE
import cliqbuy.helper.Enums.REQ_UPDATE_USER_IMAGE
import cliqbuy.model.CommonHomeModel
import cliqbuy.model.GenericModel
import cliqbuy.utils.*
import cliqbuy.utils.RuntimePermissionDialogFragment.Companion.checkPermissionStatus
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ProfileActivity : CommonActivity(),RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback {

    lateinit var profileModel :GenericModel
    lateinit var commonHomeModel: CommonHomeModel
    var name=""
    var password=""
    var confirmPassword=""
    var showdelete : Boolean=true
    lateinit var deleteAcountHashMap: HashMap<String, String>
    private var imagePath: String = ""
    private lateinit var imageFile: File
    private var imageUri: Uri? = null
    var bitMap: Bitmap? = null

    lateinit var binding: ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.edtName.setText(sessionManager.userName!!)
        loadImage(sessionManager.userAvatar!!)
        binding.commonHeader.ivBack.visibility = View.VISIBLE
        binding.commonHeader.tvTitle.text = resources.getString(R.string.profile)
        val delete:String =intent.getStringExtra("delete").toString()
        showdelete= intent.getBooleanExtra("showdelete",true)
        binding.commonHeader.ivBack.setSafeOnClickListener { callAccountFragment() }

        binding.ivEditProfileImage.setSafeOnClickListener { checkAccessPermission() }

        binding.btnDeleteProfile.setOnClickListener {
            showDialog(delete,showdelete)
        }

        //binding.btnDeleteProfile.setOnClickListener {  showBottomSheetDialog() }
        binding.btnUpdateProfile.setSafeOnClickListener { validateInput() }
        initViewModel()
        initApiResponseHandler()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        callAccountFragment()
    }

    private fun validateInput() {

        name = binding.edtName.text.trim().toString()
        password = binding.edtPassword.text.trim().toString()
        confirmPassword = binding.edtConfirmPassword.text.trim().toString()

        if (name.isEmpty()) commonMethods.showToast(this,resources.getString(R.string.enter_name))

        else if (name.isNotEmpty() && password.isEmpty() && confirmPassword.isEmpty()) updateUserProfile()

        else if (password.isEmpty()) commonMethods.showToast(this,resources.getString(R.string.enter_password))

        else if (password.isNotEmpty()){
            if (!validatePassword(password)) commonMethods.showToast(this,resources.getString(R.string.password_must_be))
            else validateConfirmPassword(password,confirmPassword)
        }


    }

    private fun validateConfirmPassword(password: String, confirmPassword: String) {

        when {
            confirmPassword.isEmpty() -> commonMethods.showToast(this,resources.getString(R.string.enter_re_password))
            confirmPassword.contains(password) -> updateUserProfile()
            else -> commonMethods.showToast(this,resources.getString(R.string.password_mismatch))
        }

    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun updateUserProfile() {

        val profileHashMap: HashMap<String, String> = HashMap()
        profileHashMap["id"] = sessionManager.userId!!
        profileHashMap["name"] =name
        profileHashMap["password"] = password

      commonViewModel!!.apiRequest(REQ_UPDATE_PROFILE,profileHashMap,this)

    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this,R.style.AppBottomSheetDialogThemeCurved)
        bottomSheetDialog.setContentView(R.layout.bottomsheet_dialog)
        val btn_close: Button = bottomSheetDialog.findViewById(R.id.btn_close)!!
        val btn_close1: TextView = bottomSheetDialog.findViewById(R.id.btn_close1)!!
        val delete_profile: Button = bottomSheetDialog.findViewById(R.id.btn_delete_profile)!!
        val description: TextView = bottomSheetDialog.findViewById(R.id.description)!!
        val otp: EditText = bottomSheetDialog.findViewById(R.id.otp)!!

        description.text =  resources.getString(R.string.enter_otp_to)+" "+sessionManager.userEmail!!.substring(0,1)+resources.getString(R.string.to_delete)
        btn_close.setOnClickListener {  bottomSheetDialog.dismiss() }
        btn_close1.setOnClickListener {  bottomSheetDialog.dismiss() }
        delete_profile.setOnClickListener {
            if (otp.text.toString().isNotEmpty()){
                callDeleteApi(otp.text.toString())
            }else{
                Toast.makeText(applicationContext,resources.getString(R.string.please_enter_otp),Toast.LENGTH_SHORT).show()
            }
        }
        bottomSheetDialog.show()
    }

    private fun showDialog(delete: String,showDelete:Boolean) {
        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialog = inflater.inflate(R.layout.custom_dialog_delete_msg, null)
        dialogBuilder.setView(dialog)
        dialogBuilder.setCancelable(false)
        val alertDialog = dialogBuilder.create()
        val title: TextView = dialog.findViewById(R.id.title)!!
        val msg: TextView = dialog.findViewById(R.id.tv_message)!!
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)!!
        val confirm: Button = dialog.findViewById(R.id.btn_ok)!!


        msg.text = delete
        if(showDelete){
            cancel.visible()
            confirm.text = getString(R.string.confirm)
            title.text = getString(R.string.delete_account_msg)
        }else{
            cancel.inVisible()
            confirm.text = getString(R.string.ok)

        }
        cancel.setOnClickListener { alertDialog.dismiss() }
        confirm.setOnClickListener {
            if (showDelete){
                DeleteAccountOtpApi()
                showBottomSheetDialog()
                alertDialog.dismiss()
            }
            else{
                alertDialog.dismiss()
            }
            }
        alertDialog.show()
    }
    private fun callDeleteApi(otp:String) {
        commonMethods.showProgressDialog(this)
        deleteAcountHashMap = HashMap()
        deleteAcountHashMap["otp"] = otp
        commonViewModel!!.apiRequest(Enums.REQ_DELETE_PROFILE,deleteAcountHashMap,this)
    }
    private fun DeleteAccountOtpApi() {
        val deleteotpHashmap: HashMap<String, String> = HashMap()
        deleteotpHashmap["access_token"] = sessionManager.accessToken.toString()
        commonViewModel!!.apiRequest(Enums.REQ_DELETE_OTP,deleteotpHashmap,this)
    }



    private fun callAccountFragment() {

        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(fragmentType,4) }
        startActivity(intent)
        overridePendingTransition(R.anim.cb_fade_in, R.anim.cb_face_out)

    } private fun callSigninsignuppage() {

        val intent = Intent(this, SigninSignupActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.cb_fade_in, R.anim.cb_face_out)

    }


    private fun pickProfileImg() {
        val view = layoutInflater.inflate(R.layout.camera_dialog_layout,null)
        val lltCamera = view.findViewById<FloatingActionButton>(R.id.llt_camera)
        val lltLibrary = view.findViewById<FloatingActionButton>(R.id.llt_library)
        val bottomSheetDialog = Dialog(this, R.style.MaterialDialogSheet)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCancelable(true)
        if (bottomSheetDialog.window == null) return
        bottomSheetDialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog.window!!.setGravity(Gravity.BOTTOM)
        bottomSheetDialog.show()

        lltCamera.setSafeOnClickListener {
            bottomSheetDialog.dismiss()
            cameraIntent()
            binding.btnUpdateProfile.setBackgroundResource(R.drawable.bg_curved_primary)
            binding.btnUpdateProfile.isEnabled = true
        }

        lltLibrary.setSafeOnClickListener {
            bottomSheetDialog.dismiss()
            imageFile = commonMethods.getDefaultFileName(this)
            commonMethods.galleryIntent(this)
            binding.btnUpdateProfile.setBackgroundResource(R.drawable.bg_curved_primary)
            binding.btnUpdateProfile.isEnabled = true
        }
    }

    private fun cameraIntent() {
        imageFile = commonMethods.cameraFilePath(this)
        commonMethods.cameraIntent(imageFile,this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_IMAGE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {

                imageUri = Uri.fromFile(imageFile)
                val encodedCaptureData = commonMethods.encodeCaptureImage(this, imageUri!!)
                Log.e("encodedImage", encodedCaptureData)
                imagePath = imageUri!!.pathSegments.last()

                updateUserImage(encodedCaptureData, imagePath)
            }
            SELECT_FILE -> if (resultCode == Activity.RESULT_OK) {

                onSelectFromGalleryResult(data)
            }
        }

    }



    private fun onSelectFromGalleryResult(data: Intent?) {
        try {
            val inputStream = this.contentResolver.openInputStream(data!!.data!!)
            val fileOutputStream = FileOutputStream(imageFile)
            commonMethods.copyStream(inputStream, fileOutputStream)
            fileOutputStream.close()
            inputStream?.close()
            bitMap = null
            if (data != null) {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val picturePath = cursor.getString(columnIndex)
                cursor.close()
                bitMap = BitmapFactory.decodeFile(picturePath)

                val encodeLibraryImg= commonMethods.encodeLibraryImage(bitMap!!)
                Log.e("en",encodeLibraryImg.toString())
                val imagePath = picturePath.substring(picturePath.lastIndexOf("/")).replace("/","")

                if (!TextUtils.isEmpty(encodeLibraryImg)) {
                    updateUserImage(encodeLibraryImg!!,imagePath)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }


    private fun checkAccessPermission() {
        checkPermissionStatus(this, supportFragmentManager, this, RuntimePermissionDialogFragment.CAMERA_PERMISSION_ARRAY, 0, 0)
    }

    override fun permissionGranted(requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int) {
        pickProfileImg()
    }


    override fun permissionDenied(requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int) {
       // commonMethods.showToast(this,resources.getString(R.string.permission_denied))
    }
    private fun loadImage(imageUrl: String) {
        if (imageUrl.isNotEmpty() && imageUrl != "null")
            if (!sessionManager.isSocialLogin!!)
                Picasso.get().load(resources.getString(R.string.imageUrl)+imageUrl).into(binding.ivProfile)
            else
                Picasso.get().load(imageUrl).into(binding.ivProfile)

        else
            Picasso.get().load(R.drawable.ic_default_profile_pic).into(binding.ivProfile)

    }


    private fun updateUserImage(encodedImageData: String, imagePath: String) {

        val imageHashMap: HashMap<String, String> = HashMap()
        imageHashMap["id"]=sessionManager.userId!!
        imageHashMap["image"]=encodedImageData
        imageHashMap["filename"]=imagePath

        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_UPDATE_USER_IMAGE,imageHashMap,this)

    }
   private fun clearSessions(){
        sessionManager.facebookId = ""
        sessionManager.appleId = ""
        sessionManager.googleId = ""
        sessionManager.userName = ""
        sessionManager.userEmail = ""
        sessionManager.userPhone = ""
        sessionManager.userId = ""
        sessionManager.userAvatar = ""
        sessionManager.accessToken = ""
    }
    private fun initApiResponseHandler() {
        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel!!.apiResponseHandler!!.isSuccess) commonMethods.hideProgressDialog()
                when (commonViewModel!!.responseCode) {
                REQ_UPDATE_PROFILE -> {
                    profileModel = commonViewModel!!.liveDataResponse.value as GenericModel
                    if (profileModel.result) {

                        commonMethods.showToast(this, profileModel.message)
                        sessionManager.userName = name
                        callAccountFragment()
                    } else {
                        commonMethods.hideProgressDialog()
                        commonMethods.showToast(this, profileModel.message)
                    }
                }
                REQ_UPDATE_USER_IMAGE->{
                    profileModel = commonViewModel!!.liveDataResponse.value as GenericModel
                    if (profileModel.result){
                        commonMethods.hideProgressDialog()
                        loadImage(profileModel.userImage)
                        sessionManager.userAvatar = profileModel.userImage!!
                        commonMethods.hideProgressDialog()
                        applicationContext.showToast(profileModel.message)
                    }else {
                        commonMethods.hideProgressDialog()
                        this.showToast(resources.getString(R.string.try_again))
                    }
                }
                    REQ_DELETE_PROFILE -> {
                        commonHomeModel = commonViewModel!!.liveDataResponse.value as CommonHomeModel
                        if (commonHomeModel.result) {

                            commonMethods.showToast(this, commonHomeModel.statusMessage)

                            callSigninsignuppage()
                            clearSessions()
                        } else {
                            commonMethods.hideProgressDialog()
                            commonMethods.showToast(this, commonHomeModel.statusMessage)
                        }
                    }
            }


        })
    }


}