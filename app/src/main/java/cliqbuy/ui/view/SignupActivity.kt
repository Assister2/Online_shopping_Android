package cliqbuy.ui.view

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils

import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.common.CommonMethods
import cliqbuy.common.CommonMethods.Companion.DebuggableLogE
import cliqbuy.databinding.SignupPageBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT
import cliqbuy.helper.Enums.REQ_SIGNUP
import cliqbuy.model.SigninSignupModel
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleCallback
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleConfiguration
import com.willowtreeapps.signinwithapplebutton.view.BaseUrl
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.HashMap

class SignupActivity : CommonActivity() {
    private lateinit var binding: SignupPageBinding
    lateinit var registerHashMap: HashMap<String, String>
    lateinit var signinSignupModel: SigninSignupModel
    private var isInternetAvailable: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        initApiResponseHandler()
        faceBookInitialize()
        googlePlusInitialize()
        appleLoginInitialize()
        initViews()

        binding.tvAppName.text= resources.getString(R.string.join_txt)+" "+sessionManager.appName
        if (sessionManager.userName!!.isNotEmpty()) {
            binding.edtName.setText(sessionManager.userName!!)
        }
        binding.btnSignup.setSafeOnClickListener { validateFun() }
        binding.ivClose.setSafeOnClickListener { callSignIn() }
        binding.btnLogin.setSafeOnClickListener { callSignIn() }
        binding.cvFacebook.setSafeOnClickListener { signUpWithFacebook() }
        binding.cvGoogle.setSafeOnClickListener { signUpWithGoogle() }
        customTextView(binding.tvAgreement,this,this)

    }

    private fun initViews() {
        if (CommonKeys.isGoogleEnabled)
            binding.cvGoogle.visibility = View.VISIBLE
        else
            binding.cvGoogle.visibility = View.GONE

        if (CommonKeys.isAppleEnabled)
            binding.cvApple.visibility = View.VISIBLE
        else
            binding.cvApple.visibility = View.GONE

        if (CommonKeys.isFacebookEnabled)
            binding.cvFacebook.visibility = View.VISIBLE
        else
            binding.cvFacebook.visibility = View.GONE

        if (!CommonKeys.isGoogleEnabled && !CommonKeys.isAppleEnabled && !CommonKeys.isFacebookEnabled){
            binding.tvOrLogin.visibility = View.GONE
            binding.lltSocialLogin.visibility = View.GONE
        }
    }


    private fun validateFun() {
        val email = binding.edtEmail.text.toString().trim { it <= ' ' }
        val name = binding.edtName.text.toString().trim { it <= ' ' }
        val password = binding.edtPassword.text.toString().trim { it <= ' ' }
        val rePassword = binding.edtRePassword.text.toString().trim { it <= ' ' }

        if (name!!.isEmpty()){
            commonMethods.showToast(this,resources.getString(R.string.enter_name))
        }else if (email.isEmpty()){
            commonMethods.showToast(this,resources.getString(R.string.enter_email))
        }else if (!isValidEmail(email)){
            commonMethods.showToast(this,resources.getString(R.string.enter_valid_email))
        }else if (password.isEmpty()){
            commonMethods.showToast(this,resources.getString(R.string.enter_password))
        }else if (!isValidPassword(password)){
            commonMethods.showToast(this,resources.getString(R.string.password_must_be))
        }else if (rePassword.isEmpty()){
            commonMethods.showToast(this,resources.getString(R.string.enter_re_password))
        }else if (!isValidPassword(rePassword)){
            commonMethods.showToast(this,resources.getString(R.string.re_password_must_be))
        }else if (!rePassword.contains(password)){
            commonMethods.showToast(this,resources.getString(R.string.password_mismatch))
        }else if (!binding.cbTerms.isChecked){
            commonMethods.showToast(this,resources.getString(R.string.please_agree_terms))
        }else{
            callRegisterApi()
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun appleLoginInitialize() {
        //This BaseUrl.baseUrl For Check the URL is AppleCallback in siginwithApplicationmodule
        BaseUrl.appleCallbackUrl = resources.getString(R.string.appleCallback)
        val configuration = SignInWithAppleConfiguration.Builder()
            .clientId(resources.getString(R.string.apple_client_id))
            .redirectUri(resources.getString(R.string.appleCallback))
            .scope(CommonKeys.Apple_Login_Scope)
            .build()

        val callback = object : SignInWithAppleCallback {
            override fun onSuccessOnSignIn(response: String) {


                var json: JSONObject? = null
                try {
                    json = JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                var result: Boolean? = false
                var token : String? = ""
                var id : Int? = 0
                var name : String? = ""
                var message : String? = ""
                var email : String? = ""
                var avatar : String? = ""
                var phone : String? = ""

                try {
                    result = json?.getBoolean("result")
                    token = json?.getString("access_token")
                    message = json?.getString("message")
                    id = json?.getJSONObject("user")?.getInt("id")
                    name = json?.getJSONObject("user")?.getString("name")
                    email = json?.getJSONObject("user")?.getString("email")
                    avatar = json?.getJSONObject("user")?.getString("avatar")
                    phone = json?.getJSONObject("user")?.getString("phone")

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (result!!) {
                    sessionManager.userId = id.toString()
                    sessionManager.accessToken = token
                    sessionManager.userEmail = email
                    sessionManager.userName = name
                    if (avatar != "null" || avatar != null) {
                        sessionManager.userAvatar = avatar
                    }
                    if (phone != "null" || phone != null) {
                        sessionManager.userPhone = phone
                    }
                    commonMethods.showToast(this@SignupActivity,message!!)

                    callHome()
                }else{
                    commonMethods.showToast(this@SignupActivity,resources.getString(R.string.please_try_again))
                }



            }

            override fun onSignInWithAppleSuccess(authorizationCode: String) {}

            override fun onSignInWithAppleFailure(error: Throwable) {}

            override fun onSignInWithAppleCancel() {}
        }

        binding.signInWithAppleButtonWhite.setUpSignInWithAppleOnClick(supportFragmentManager, configuration, "Apple", callback)
    }

    private fun callHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType,1) }
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.cb_fade_in,
            R.anim.cb_face_out
        ).toBundle()
        startActivity(intent, animation)
    }


    private fun callRegisterApi() {
        registerHashMap = HashMap()
        registerHashMap["name"] = binding.edtName.text.toString()
        registerHashMap["email_or_phone"] = binding.edtEmail.text.toString()
        registerHashMap["password"] = binding.edtPassword.text.toString()
        registerHashMap["password_confirmation"] = binding.edtRePassword.text.toString()
        registerHashMap["register_by"] = "email"
        registerHashMap["language"] = commonMethods.getLangCode()
        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_SIGNUP,registerHashMap,this)

    }


    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(this, Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    REQ_SIGNUP -> {
                        commonMethods.hideProgressDialog()
                        signinSignupModel =commonViewModel?.liveDataResponse?.value as (SigninSignupModel)

                        if (signinSignupModel.result){
                            sessionManager.userId = signinSignupModel.userId.toString()
                            commonMethods.showToast(this,signinSignupModel.statusMessage)
                            var intent = Intent(this, OTPVerificationActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.cb_fade_in,
                                R.anim.cb_face_out)
                        }else{
                            commonMethods.showToast(this,signinSignupModel.statusMessage)
                        }
                    }
                }

            }else{
                commonMethods.hideProgressDialog()
            }

        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)


        } else if (requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) {
            if (resultCode == Activity.RESULT_OK) {
                commonMethods.showToast(this,"Facebook Success")
            }
        } else if (requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) {
            if (resultCode == Activity.RESULT_OK) {
                clearSocialCredintials()
                commonMethods.showToast(this,"Facebook 2 Success")
            }
        } else {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }

    }



    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            getProfileInformation(account!!)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google login", "signInResult:failed code=" + e.statusCode)
            commonMethods.showToast(this,"signInResult:failed code=" + e.statusCode)
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private fun getProfileInformation(account: GoogleSignInAccount) {

        googleID = account.id!!

        googleFullName = account.displayName!!
        if (account.photoUrl != null)
            googleUserProfile = account.photoUrl.toString()
        else
            googleUserProfile = ""

        googleEmail = account.email!!


        googleUserProfile = googleUserProfile.replace("s96-c", "s400-c")
        val splitStr = googleFullName.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val firstName = splitStr[0]
        var lastName = ""
        for (i in 1 until splitStr.size) {
            lastName = lastName + " " + splitStr[i]
        }
        if (lastName == "") lastName = " "

        sessionManager.userEmail = googleEmail
        sessionManager.facebookId = ""
        sessionManager.appleId = ""
        sessionManager.googleId = googleID
        sessionManager.userName = firstName + lastName
        sessionManager.userAvatar = googleUserProfile + ""


            if (count == 1) {
                signOut()
                if (sessionManager.userEmail!!.isEmpty()){
                    callRegisterActivity()
                    commonMethods.showToast(this,resources.getString(R.string.please_fill_form))
                }else{
                    callSocialLoginApi(sessionManager.userName!!,sessionManager.userEmail!!,
                        sessionManager.userAvatar!!
                    )
                } }
            count++


    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this) { }
    }

    fun clearSocialCredintials() {
        sessionManager.facebookId = ""
        sessionManager.appleId = ""
        sessionManager.googleId = ""
        sessionManager.userName = ""
        sessionManager.userEmail = ""
    }

    private fun callSignIn() {
        var intent = Intent(this, SigninSignupActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.cb_fade_in,
            R.anim.cb_face_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        callSignIn()
    }


}