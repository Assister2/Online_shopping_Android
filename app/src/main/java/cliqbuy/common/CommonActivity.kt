package cliqbuy.common

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import cliqbuy.R
import dev.b3nedikt.restring.Restring.wrapContext
import dev.b3nedikt.viewpump.ViewPumpContextWrapper
import cliqbuy.configs.AppController
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.helper.SessionManager
import cliqbuy.interfaces.ApiService
import cliqbuy.model.SigninSignupModel
import cliqbuy.ui.view.AddressDetailsActivity
import cliqbuy.ui.view.HomeActivity
import cliqbuy.ui.view.SigninSignupActivity
import cliqbuy.ui.view.SignupActivity
import cliqbuy.viewmodel.CommonViewModel
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.inject.Inject

abstract class CommonActivity : AppCompatActivity() {
    init {
        AppController.appComponent!!.inject(this)

    }

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var apiService: ApiService

    var commonViewModel: CommonViewModel? = null

    lateinit var socialLoginViewModel: SigninSignupModel

    //Facebook
    var callbackManager: CallbackManager? = null
    lateinit var fbEmail: String
    lateinit var fbFullName: String
    lateinit var fbID: String
    lateinit var fbImageURL: String
    lateinit var parameters: Bundle

    //Google
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 0
    lateinit var googleEmail: String
    lateinit var googleFullName: String
    lateinit var googleUserProfile: String
    lateinit var googleID: String
    var count = 1

    //Hashmap
    val commonHashMap: HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(currentTheme)
        setTheme(currentFont)
        initViewModel()
        initResponseApi()
    }

    private fun initResponseApi() {

        commonViewModel!!.liveDataResponse.observe(this, androidx.lifecycle.Observer {

            if (commonViewModel!!.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_SOCIAL_LOGIN -> {

                        socialLoginViewModel =
                            commonViewModel?.liveDataResponse?.value as (SigninSignupModel)

                        if (socialLoginViewModel.result) {
                            sessionManager.accessToken = socialLoginViewModel.accessToken!!
                            sessionManager.userId = socialLoginViewModel.user!!.id.toString()
                            sessionManager.tokenType = socialLoginViewModel.userTokenType
                            sessionManager.userEmail = socialLoginViewModel.user!!.userMail
                            sessionManager.userName = socialLoginViewModel.user!!.userName
                            sessionManager.userPhone = socialLoginViewModel.user!!.userPhone
                            if (socialLoginViewModel.user!!.userOriginalAvatar.isNotEmpty()) {
                                sessionManager.isSocialLogin = true
                                sessionManager.userAvatar =
                                    socialLoginViewModel.user!!.userOriginalAvatar
                            } else {
                                sessionManager.isSocialLogin = false
                            }

                            if (socialLoginViewModel.user!!.isNeedAddress.not())
                                moveAddressScreen()
                            else
                                callHomeActivity()

                        } else if (!socialLoginViewModel.result) {
                            sessionManager.userName = ""
                            commonMethods.showToast(this, socialLoginViewModel.successMessage)
                        }

                    }
                }
            }
        })
    }

    open fun moveAddressScreen() {
        Intent(this, AddressDetailsActivity::class.java).also {
            it.putExtra("comeFom", "login")
            startActivity(it)
        }
    }

    private fun callHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType, 1) }
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.cb_fade_in,
            R.anim.cb_face_out
        ).toBundle()
        startActivity(intent, animation)
    }

    fun callRegisterActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.cb_fade_in,
            R.anim.cb_face_out
        ).toBundle()
        startActivity(intent, animation)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(updateLocale(newBase))
    }

    override fun getResources(): Resources {
        return wrapContext(baseContext).resources
    }

    public fun initViewModel() {
        commonViewModel = ViewModelProvider(this).get(CommonViewModel::class.java)
        commonViewModel?.initAppController(this, this)
    }

    fun callSocialLoginApi(name: String, email: String, profile: String) {
        var socialLoginHashMap: HashMap<String, String> = HashMap()
        socialLoginHashMap["name"] = name
        socialLoginHashMap["email"] = email
        socialLoginHashMap["provider"] = getAuthType()
        if (profile.isEmpty())
            socialLoginHashMap["avatar_original"] =
                resources.getString(R.string.baseUrl) + "/public/assets/img/placeholder.jpg"
        else
            socialLoginHashMap["avatar_original"] = profile
        commonViewModel!!.apiRequest(Enums.REQ_SOCIAL_LOGIN, socialLoginHashMap, this)
    }

    fun getAuthType(): String {
        return if (sessionManager.facebookId!!.isNotEmpty()) {
            "facebook"
        } else if (sessionManager.googleId!!.isNotEmpty()) {
            "google"
        } else if (sessionManager.appleId!!.isNotEmpty()) {
            "apple"
        } else {
            "email"
        }
    }

    fun getAuthId(): String? {
        return if (sessionManager.facebookId!!.isNotEmpty()) {
            sessionManager.facebookId
        } else if (sessionManager.googleId!!.isNotEmpty()) {
            sessionManager.googleId
        } else if (sessionManager.appleId!!.isNotEmpty()) {
            sessionManager.appleId
        } else {
            ""
        }
    }

    /**
     * Solving multiple clicks problem
     */

    class SafeClickListener(
        private var defaultInterval: Int = 1000,
        private val onSafeCLick: (View) -> Unit,
    ) : View.OnClickListener {
        private var lastTimeClicked: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    fun signUpWithFacebook() {
        LoginManager.getInstance().logOut()
        if (!isPackageInstalled("com.facebook.katana", this))
            LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY)

        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
    }

    fun isPackageInstalled(packageName: String, context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun signUpWithGoogle() {
        count = 1
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun googlePlusInitialize() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun faceBookInitialize() {
        //Facebook Initialize
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        /**
         * Get Facebook key hash for devloper
         */
        getFbKeyHash(CommonMethods.appPackageName)

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(loginResult: LoginResult) {
                    /**
                     * Get facebook user details
                     */
                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, _ ->
                        fbEmail = `object`!!.optString("email")
                        fbFullName = `object`!!.optString("name")
                        println("FullName $fbFullName and obejct ${`object`.toString()}")
                        fbID = `object`!!.optString("id")
                        fbImageURL = "https://graph.facebook.com/" + fbID + "/picture?type=large";
                        println("FBIMAGE URL " + fbImageURL)

                        sessionManager.userName = fbFullName
                        sessionManager.userEmail = fbEmail
                        sessionManager.facebookId = fbID
                        sessionManager.userAvatar = fbImageURL
                        sessionManager.googleId = ""
                        sessionManager.appleId = ""

                        if (sessionManager.userEmail!!.isEmpty()) {
                            callRegisterActivity()
                            commonMethods.showToast(
                                this@CommonActivity,
                                resources.getString(R.string.please_fill_form)
                            )
                        } else {
                            callSocialLoginApi(
                                sessionManager.userName!!, sessionManager.userEmail!!,
                                sessionManager.userAvatar!!
                            )
                        }
                    }

                    parameters = Bundle()
                    parameters.putString("fields", "id,name,link,gender,birthday,email")
                    request.parameters = parameters
                    request.executeAsync()

                    val bundle = Bundle()
                    bundle.putString("fields", "token_for_business")

                }

                override fun onCancel() {

                }

                override fun onError(e: FacebookException) {
                    CommonMethods.DebuggableLogE("Facebooksdk", "Login with Facebook failure", e)
                    Toast.makeText(
                        applicationContext,
                        "An unknown network error has occured",
                        Toast.LENGTH_LONG
                    ).show()

                }

            })
    }

    /**
     * Create FB KeyHash
     */
    fun getFbKeyHash(packageName: String) {

        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));

                CommonMethods.DebuggableLogE("hash key", something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            CommonMethods.DebuggableLogE("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            CommonMethods.DebuggableLogE("no such an algorithm", e.toString())
        } catch (e: Exception) {
            CommonMethods.DebuggableLogE("exception", e.toString())
        }

    }

    /**
     * Custom text view to link
     */
    fun customTextView(view: TextView, context: Context, activity: Activity) {

        var termpolicy = context.getString(R.string.termPolicyUrl) //Demo Url for Terms and policy

        val styleSpan = StyleSpan(Typeface.BOLD)

        val spanTxt = SpannableStringBuilder(
            context.resources.getString(R.string.sigin_terms1)
        )
        spanTxt.append(" ")
        spanTxt.append(context.resources.getString(R.string.terms_conditions))

        spanTxt.setSpan(styleSpan, 0, spanTxt.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        spanTxt.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val url = termpolicy
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    activity.startActivity(i)

                }
            },
            spanTxt.length - context.resources.getString(R.string.terms_conditions).length,
            spanTxt.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanTxt.setSpan(
            ForegroundColorSpan(setBgColor(context, R.attr.bgThird)),
            spanTxt.length - context.resources.getString(R.string.terms_conditions).length,
            spanTxt.length,
            0
        )
        spanTxt.append(".")


        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private fun setBgColor(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    fun clearSession() {
        sessionManager.facebookId = ""
        sessionManager.appleId = ""
        sessionManager.googleId = ""
        sessionManager.userName = ""
        sessionManager.userEmail = ""
        sessionManager.userPhone = ""
        sessionManager.userId = ""
        sessionManager.userAvatar = ""
        sessionManager.accessToken = ""
        /*sessionManager.language = "English"
        sessionManager.languageCode = "en"*/
        sessionManager.isSocialLogin = false
    }

    /**  set language for whole Application */
    private fun updateLocale(newBase: Context?): Context? {
        var newBase = newBase
        val lang: String = sessionManager.languageCode!!
        val locale = Locale(lang)
        val config = Configuration(newBase?.resources?.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        newBase = newBase?.createConfigurationContext(config)
        newBase?.resources?.updateConfiguration(config, newBase.resources.displayMetrics)
        println("### -- locale    ${sessionManager.languageCode}")

        return newBase
    }

    companion object {
        var currentTheme = R.style.AppTheme_CliqbBuy
        var currentDilogTheme = R.style.DialogTheme_Cliqbuy
        var currentFont = R.style.font_1

        fun isDarkTheme(resources: Resources): Boolean {
            return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
    }
}