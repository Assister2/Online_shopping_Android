package cliqbuy.ui.view

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.lifecycle.Observer
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityOtpverificationBinding
import cliqbuy.helper.Enums
import cliqbuy.model.OtpVerificationModel

class OTPVerificationActivity : CommonActivity() {
    lateinit var otpVerificationHashMap: HashMap<String, String>
    private lateinit var binding: ActivityOtpverificationBinding
    lateinit var otpVerificationModel: OtpVerificationModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        initApiResponseHandler()
        binding.tvResend.paintFlags = binding.tvResend.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.btnConfirm.setSafeOnClickListener { confirmApiCall() }
        binding.tvResend.setSafeOnClickListener { resendApiCall() }
    }

    private fun resendApiCall() {
        otpVerificationHashMap = HashMap()
        otpVerificationHashMap["user_id"] = sessionManager.userId.toString()
        otpVerificationHashMap["verify_by"] = "email"
        otpVerificationHashMap["language"] =  commonMethods.getLangCode()
        commonViewModel!!.apiRequest(Enums.REQ_RESEND_CODE,otpVerificationHashMap,this)
    }

    private fun confirmApiCall() {
        if(binding.edtOtp.text.toString().isEmpty()){
            commonMethods.showToast(this,resources.getString(R.string.please_enter_verification_code))
        }else {
            otpVerificationHashMap = HashMap()
            otpVerificationHashMap["user_id"] = sessionManager.userId.toString()
            otpVerificationHashMap["verification_code"] = binding.edtOtp.text.toString()
            otpVerificationHashMap["language"] =  commonMethods.getLangCode()
            commonViewModel!!.apiRequest(Enums.REQ_CONFIRM_CODE, otpVerificationHashMap, this)
        }

    }

    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(this, Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_RESEND_CODE -> {
                        otpVerificationModel =commonViewModel?.liveDataResponse?.value as (OtpVerificationModel)

                        if (otpVerificationModel.result){
                            commonMethods.showToast(this,otpVerificationModel.statusMessage)
                        }else{
                            commonMethods.showToast(this,otpVerificationModel.statusMessage)
                        }
                    }
                    Enums.REQ_CONFIRM_CODE -> {
                        otpVerificationModel =commonViewModel?.liveDataResponse?.value as (OtpVerificationModel)

                        if (otpVerificationModel.result){
                            var intent = Intent(this, SigninSignupActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.cb_fade_in,
                                R.anim.cb_face_out)
                            commonMethods.showToast(this,otpVerificationModel.statusMessage)
                        }else{
                            commonMethods.showToast(this,otpVerificationModel.statusMessage)
                        }
                    }
                }

            }else{
                commonMethods.hideProgressDialog()
            }

        })


    }
}