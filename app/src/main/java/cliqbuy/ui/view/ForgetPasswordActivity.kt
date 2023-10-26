package cliqbuy.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityForgetPasswordBinding
import cliqbuy.helper.CommonKeys.mail
import cliqbuy.helper.Enums.REQ_FORGET_PASSWORD
import cliqbuy.helper.Enums.REQ_RESEND_CODE
import cliqbuy.helper.Enums.REQ_RESEND_PASSWORD_CODE
import cliqbuy.helper.Enums.REQ_RESET_PASSWORD
import cliqbuy.model.SigninSignupModel

class ForgetPasswordActivity : CommonActivity() {


    var email = ""
    private var verificationCode = ""
    private var password = ""
    private var confirmPassword = ""
    lateinit var forgetHashMap: HashMap<String, String>
    private lateinit var forgetViewModel: SigninSignupModel
    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        initApiResponseHandler()

        binding.btnSendCode.setSafeOnClickListener { validateEmail() }
        binding.ivClose.setSafeOnClickListener {  onBackPressed() }
        binding.btnConfirm.setSafeOnClickListener { validateInputData() }
        binding.tvResendCode.setSafeOnClickListener { callResendApi() }

    }

    private fun callResendApi() {
        val resendHashMap: HashMap<String, String> = HashMap()
        resendHashMap["email_or_phone"]=email
        resendHashMap["verify_by"]= mail
        resendHashMap["language"]= commonMethods.getLangCode()

        commonViewModel!!.apiRequest(REQ_RESEND_PASSWORD_CODE,resendHashMap,this)
    }

    private fun validateInputData() {
        verificationCode = binding.edtCode.text.toString().trim { it <= ' ' }
        password = binding.edtPassword.text.toString().trim { it <= ' ' }
        confirmPassword = binding.edtRePassword.text.toString().trim { it <= ' ' }

        when {
            verificationCode.isEmpty() -> {
                commonMethods.showToast(this, resources.getString(R.string.enter_the_code))
                return
            }
            password.isEmpty() -> {
                commonMethods.showToast(this, resources.getString(R.string.enter_password))
                return
            }
            confirmPassword.isEmpty() -> {
                commonMethods.showToast(this, resources.getString(R.string.confirm_your_password))
                return
            }
            else -> if (password.length < 6) {
                commonMethods.showToast(this, resources.getString(R.string.password_must_be))
                return
            } else
                validatePassword()
        }

    }

    private fun validatePassword() {

        if (confirmPassword.contains(password)) callResetPasswordApi()
        else commonMethods.showToast(this, resources.getString(R.string.password_mismatch))
    }

    private fun callResetPasswordApi() {

        val passwordHashMap: HashMap<String, String> = HashMap()
        passwordHashMap["verification_code"] = binding.edtCode.text.toString()
        passwordHashMap["password"] = binding.edtPassword.text.toString()
        passwordHashMap["language"]= commonMethods.getLangCode()


        commonViewModel!!.apiRequest(REQ_RESET_PASSWORD, passwordHashMap, this)

    }

    private fun validateEmail() {

        email = binding.edtEmail.text.toString().trim { it <= ' ' }

        if (email.isEmpty()) {
            commonMethods.showToast(this, resources.getString(R.string.enter_email))
            return
        } else if (!isValidEmail(email)) {
            commonMethods.showToast(this, resources.getString(R.string.enter_valid_email))
            return
        } else {
            callForgetRequest()
        }
    }

    private fun callForgetRequest() {

        forgetHashMap = HashMap()
        forgetHashMap["email_or_phone"] = email
        forgetHashMap["send_code_by"] = mail
        forgetHashMap["language"]= commonMethods.getLangCode()
        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_FORGET_PASSWORD, forgetHashMap, this)
    }




    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }


    override fun onBackPressed() = if (binding.rltConfirm.isVisible){
        binding.rltConfirm.visibility =View.GONE
        binding.tvHeader.text = resources.getString(R.string.forgot_password)
        binding.rltSendCode.visibility = View.VISIBLE
    }else{
        super.onBackPressed()
    }


    private fun initApiResponseHandler() {

        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel!!.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    REQ_FORGET_PASSWORD -> {
                        commonMethods.hideProgressDialog()
                        forgetViewModel = commonViewModel?.liveDataResponse?.value as (SigninSignupModel)

                        if (forgetViewModel.result) {
                            commonMethods.showToast(this, forgetViewModel.statusMessage)
                            binding.rltSendCode.visibility = View.GONE
                            binding.tvHeader.text = resources.getString(R.string.enter_code)
                            binding.rltConfirm.visibility = View.VISIBLE


                        } else if (!forgetViewModel.result) {
                            commonMethods.showToast(this, forgetViewModel.statusMessage)
                        }

                    }
                    REQ_RESET_PASSWORD -> {
                        forgetViewModel = commonViewModel?.liveDataResponse?.value as (SigninSignupModel)
                        if (forgetViewModel.result) {

                            commonMethods.showToast(this, forgetViewModel.statusMessage)
                            callSignInSignup()
                        } else {
                            commonMethods.showToast(this, forgetViewModel.statusMessage)
                        }
                    }
                    REQ_RESEND_PASSWORD_CODE->{

                        forgetViewModel = commonViewModel?.liveDataResponse?.value as (SigninSignupModel)
                        commonMethods.showToast(this, forgetViewModel.statusMessage)

                    }
                }
            }
        })
    }

    private fun callSignInSignup() {

        val intent = Intent(this, SigninSignupActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.cb_fade_in, R.anim.cb_face_out)
    }

}