package cliqbuy.ui.view

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivitySigninSignupBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.CommonKeys.identifyMatrix
import cliqbuy.helper.Enums
import cliqbuy.helper.Enums.REQ_CHANGE_LANGUAGE
import cliqbuy.helper.Enums.REQ_COMMON_DATA
import cliqbuy.helper.Enums.REQ_LOGIN
import cliqbuy.model.CommonDataModel
import cliqbuy.model.SigninSignupModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleCallback
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleConfiguration
import com.willowtreeapps.signinwithapplebutton.view.BaseUrl
import org.json.JSONObject

class SigninSignupActivity : CommonActivity() {


    var email = ""
    var password = ""
    lateinit var loginHashMap: HashMap<String, String>
    private lateinit var loginViewModel: SigninSignupModel
    private lateinit var binding: ActivitySigninSignupBinding
    lateinit var commonDataModel: CommonDataModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninSignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        initApiResponseHandler()
        googlePlusInitialize()
        faceBookInitialize()
        appleLoginInitialize()
        initViews()
        getCommonData()
        binding.tvSignUp.setSafeOnClickListener { callSignUp() }
        binding.ivClose.setSafeOnClickListener { callHome() }
        binding.siginButton.setSafeOnClickListener { validateCredential() }
        binding.cvGoogle.setSafeOnClickListener { signUpWithGoogle() }
        binding.cvFacebook.setSafeOnClickListener { signUpWithFacebook() }
        binding.cvApple.setSafeOnClickListener { }
        binding.forgetPassword.setSafeOnClickListener { callForget() }

    }

    private fun callForget() {

        val intent = Intent(this, ForgetPasswordActivity::class.java)
        overridePendingTransition(R.anim.cb_fade_in, R.anim.cb_face_out)
        startActivity(intent)
    }

    private fun getCommonData() {
        commonViewModel!!.apiRequest(REQ_COMMON_DATA, commonHashMap, this)

    }

    private fun initViews() {
        if (CommonKeys.isGoogleEnabled) binding.cvGoogle.visibility = View.VISIBLE
        else binding.cvGoogle.visibility = View.GONE

        if (CommonKeys.isAppleEnabled) binding.cvApple.visibility = View.VISIBLE
        else binding.cvApple.visibility = View.GONE

        if (CommonKeys.isFacebookEnabled) binding.cvFacebook.visibility = View.VISIBLE
        else binding.cvFacebook.visibility = View.GONE

        if (!CommonKeys.isGoogleEnabled && !CommonKeys.isAppleEnabled && !CommonKeys.isFacebookEnabled) {
            binding.tvLoginWtih.visibility = View.GONE
            binding.lltSocialLogin.visibility = View.GONE
        }
    }

    private fun appleLoginInitialize() {
        //This BaseUrl.baseUrl For Check the URL is AppleCallback in siginwithApplicationmodule
        BaseUrl.appleCallbackUrl = resources.getString(R.string.appleCallback)
        val configuration = SignInWithAppleConfiguration.Builder()
            .clientId(resources.getString(R.string.apple_client_id))
            .redirectUri(resources.getString(R.string.appleCallback))
            .scope(CommonKeys.Apple_Login_Scope).build()

        val callback = object : SignInWithAppleCallback {
            override fun onSuccessOnSignIn(response: String) {

                var json: JSONObject? = null
                try {
                    json = JSONObject(
                        response.substring(
                            response.indexOf("{"), response.lastIndexOf("}") + 1
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                var result: Boolean? = false
                var token: String? = ""
                var id: Int? = 0
                var name: String? = ""
                var message: String? = ""
                var email: String? = ""
                var avatar: String? = ""
                var phone: String? = ""
                var isNeedAddress: Boolean = false

                try {
                    result = json?.getBoolean("result")
                    token = json?.getString("access_token")
                    message = json?.getString("message")
                    id = json?.getJSONObject("user")?.getInt("id")
                    name = json?.getJSONObject("user")?.getString("name")
                    email = json?.getJSONObject("user")?.getString("email")
                    avatar = json?.getJSONObject("user")?.getString("avatar")
                    phone = json?.getJSONObject("user")?.getString("phone")
                    isNeedAddress =
                        json?.getJSONObject("user")?.getBoolean("user_address_count") ?: false

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
                    commonMethods.showToast(this@SigninSignupActivity, message!!)
                    if (isNeedAddress.not()) moveAddressScreen()
                    else callHome()
                } else {
                    commonMethods.showToast(
                        this@SigninSignupActivity, resources.getString(R.string.please_try_again)
                    )
                }


            }

            override fun onSignInWithAppleSuccess(authorizationCode: String) {}

            override fun onSignInWithAppleFailure(error: Throwable) {}

            override fun onSignInWithAppleCancel() {}
        }

        binding.signInWithAppleButtonWhite.setUpSignInWithAppleOnClick(
            supportFragmentManager, configuration, "Apple", callback
        )
    }


    private fun validateCredential() {

        email = binding.edtEmail.text.toString().trim { it <= ' ' }
        password = binding.edtPassword.text.toString().trim { it <= ' ' }
        when {
            email.isEmpty() -> {
                commonMethods.showToast(this, resources.getString(R.string.enter_email))
                return
            }

            password.isEmpty() -> {
                commonMethods.showToast(this, resources.getString(R.string.enter_password))
                return
            }

            else -> {
                validateMailWithPassword(email, password)
            }
        }
    }

    private fun validateMailWithPassword(email: String, password: String) {
        if (password.length > 5) {

            when {
                isValidEmail(email) -> {
                    callLogin()
                }

                else -> {
                    commonMethods.showToast(this, resources.getString(R.string.enter_valid_email))
                }
            }


        } else {
            commonMethods.showToast(this, resources.getString(R.string.password_must_be))

        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun callLogin() {
        loginHashMap = HashMap()
        loginHashMap["email"] = email
        loginHashMap["password"] = password
        loginHashMap["identity_matrix"] = identifyMatrix
        loginHashMap["language"] = commonMethods.getLangCode()
        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_LOGIN, loginHashMap, this)

    }


    override fun onBackPressed() {
        super.onBackPressed()
        callHome()
    }


    private fun callSignUp() {
        val intent = Intent(this, SignupActivity::class.java)
        overridePendingTransition(R.anim.cb_fade_in, R.anim.cb_face_out)
        startActivity(intent)
    }


    private fun initApiResponseHandler() {

        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel!!.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    REQ_LOGIN -> {
                        commonMethods.hideProgressDialog()
                        loginViewModel =
                            commonViewModel?.liveDataResponse?.value as (SigninSignupModel)

                        if (loginViewModel.result) {

                            sessionManager.accessToken = loginViewModel.accessToken!!
                            sessionManager.userId = loginViewModel.user!!.id.toString()
                            sessionManager.tokenType = loginViewModel.userTokenType
                            sessionManager.userEmail = loginViewModel.user!!.userMail
                            sessionManager.userName = loginViewModel.user!!.userName
                            sessionManager.userPhone = loginViewModel.user!!.userPhone
                            sessionManager.userAvatar = loginViewModel.user!!.userOriginalAvatar

                            updateLanguage()

                            if (loginViewModel.user!!.isNeedAddress.not()) moveAddressScreen()
                            else callHome()

                        } else if (!loginViewModel.result) {
                            commonMethods.showToast(this, loginViewModel.statusMessage)
                        }

                    }

                    REQ_CHANGE_LANGUAGE -> {

                    }

                    REQ_COMMON_DATA -> {
                        commonDataModel =
                            commonViewModel?.liveDataResponse?.value as (CommonDataModel)

                        if (commonDataModel.success) {

                            CommonKeys.isGoogleEnabled = commonDataModel.isGoogleEnabled
                            CommonKeys.isFacebookEnabled = commonDataModel.isFacebookEnabled
                            CommonKeys.isAppleEnabled = commonDataModel.isAppleEnabled
                            initViews()
                        } else {
                            commonMethods.showToast(this, "failure") //for_temp
                        }

                    }
                }
            }
        })
    }

    override fun moveAddressScreen() {
        Intent(this@SigninSignupActivity, AddressDetailsActivity::class.java).also {
            it.putExtra("comeFom", "login")
            startActivity(it)
        }
    }

    private fun updateLanguage() {
        if (!sessionManager.accessToken.isNullOrEmpty()) {
            commonViewModel!!.apiRequest(Enums.REQ_CHANGE_LANGUAGE, getData(), this)
        }
    }

    private fun getData() = HashMap<String, String>().apply {
        println("### user id -->${sessionManager.userId}")
        put("language", sessionManager.languageCode.let {
            if (it == "ar") "sa"
            else "en"
        })
        put("user_id", sessionManager.userId.toString())
    }

    private fun callHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType, 1) }
        val animation = ActivityOptions.makeCustomAnimation(
            this, R.anim.cb_fade_in, R.anim.cb_face_out
        ).toBundle()
        startActivity(intent, animation)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)


        } else if (requestCode == CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) {
            if (resultCode == Activity.RESULT_OK) {
                commonMethods.showToast(this, "Facebook Success")
            }
        } else if (requestCode == CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) {
            if (resultCode == Activity.RESULT_OK) {
                clearSocialCredentials()
                commonMethods.showToast(this, "Facebook 2 Success")
            }
        } else {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun clearSocialCredentials() {

        sessionManager.facebookId = ""
        sessionManager.appleId = ""
        sessionManager.googleId = ""
        sessionManager.userName = ""
        sessionManager.userEmail = ""
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
            commonMethods.showToast(this, resources.getString(R.string.please_try_again))
        }
    }

    private fun getProfileInformation(account: GoogleSignInAccount) {

        googleID = account.id!!

        googleFullName = account.displayName!!
        if (account.photoUrl != null) googleUserProfile = account.photoUrl.toString()
        else googleUserProfile = ""

        googleEmail = account.email!!


        googleUserProfile = googleUserProfile.replace("s96-c", "s400-c")
        val splitStr =
            googleFullName.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
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
            callSocialLoginApi(
                sessionManager.userName!!, sessionManager.userEmail!!, sessionManager.userAvatar!!
            )
        }
        count++


    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this) { }
    }


}