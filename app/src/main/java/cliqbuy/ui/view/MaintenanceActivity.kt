package cliqbuy.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.configs.AppController
import cliqbuy.interfaces.ApiResponseCommonInterface
import cliqbuy.viewmodel.CommonViewModel

class MaintenanceActivity : CommonActivity(), ApiResponseCommonInterface {



    lateinit var dialog: AlertDialog


    @BindView(R.id.tv_once_dev)
    lateinit var tvMaintenanceMessage: TextView

    @BindView(R.id.rlt_try_again)
    lateinit var rltTryAgain: RelativeLayout


    @OnClick(R.id.rlt_try_again)
    fun tryAgain() {
        rltTryAgain.isEnabled = false
        //checkMaintenance()
    }

    @OnClick(R.id.tv_once_dev)
    fun tvSupportRedirect() {

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", getString(R.string.support_mail), null))
        startActivity(Intent.createChooser(emailIntent, null))

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)
        ButterKnife.bind(this)
        AppController.appComponent!!.inject(this)
        initViewModel()
        isInMaintenance = true
        val contactUs = getString(R.string.support_txt)
        val word = getString(R.string.support_mail)
        val spannableString = SpannableString(contactUs)
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.thumbdown_color)),
            contactUs.indexOf(word), contactUs.indexOf(word) + word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        tvMaintenanceMessage!!.text = spannableString
    }

    companion object {
        var isInMaintenance = false
    }


    /**
     * Check Force Update
     */
    /*private fun checkMaintenance() {
        if (!commonMethods.isOnline(applicationContext)) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_internet_connection))
        } else {
            commonViewModel.apiRequest(Enums.REG_GET_CHECK_VERSION, getCheckVersionParams(), this)
        }
    }

    private fun getCheckVersionParams(): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap["token"] = sessionManager.accessToken!!
        hashMap["user_type"] = sessionManager.type!!
        hashMap["device_id"] = sessionManager.deviceId!!
        hashMap["version"] = CommonMethods.getAppVersionNameFromGradle()
        hashMap["device_type"] = CommonKeys.DeviceTypeAndroid
        hashMap["language"] = sessionManager.languageCode!!
        return hashMap
    }*/

    override fun onSuccessResponse(jsonResponse: LiveData<Any>, requestCode: Int) {
        jsonResponse.observe(this, androidx.lifecycle.Observer {
            /*try {
                commonMethods.hideProgressDialog()
            } catch (e: Exception) {
                e.printStackTrace()
            }*/

            when (requestCode) {
                /*Enums.REG_GET_CHECK_VERSION -> {
                    SplashActivity.checkVersionModel = jsonResponse.value as (CheckVersionModel)
                    if (SplashActivity.checkVersionModel?.statusCode.equals("1")) {
                        val intent = Intent(this, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else if (!TextUtils.isEmpty(SplashActivity.checkVersionModel?.statusMessage)) {
                        commonMethods.showMessage(
                            this,
                            dialog,
                            SplashActivity.checkVersionModel?.statusMessage!!
                        )
                    }
                }*/
            }
        })
    }

    override fun onFailureResponse(errorResponse: String, requestCode: Int) {
        TODO("Not yet implemented")
    }

}