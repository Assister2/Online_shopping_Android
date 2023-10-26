package cliqbuy.helper

import android.content.SharedPreferences
import cliqbuy.configs.AppController
import javax.inject.Inject


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SessionManager {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init {
        AppController.appComponent!!.inject(this)
    }

    var language: String?
        get() = sharedPreferences!!.getString("language", "English")
        set(language) = sharedPreferences!!.edit().putString("language", language).apply()

    var languageCode: String?
        get() = sharedPreferences!!.getString("languagecode", "en")
        set(languagecode) = sharedPreferences!!.edit().putString("languagecode", languagecode).apply()

    var currencyCode: String?
        get() = sharedPreferences!!.getString("currencyCode", "")
        set(currencyCode) = sharedPreferences!!.edit().putString("currencyCode", currencyCode).apply()

    var currencyName: String?
        get() = sharedPreferences!!.getString("currencyName", "")
        set(currencyName) = sharedPreferences!!.edit().putString("currencyName", currencyName).apply()
    var appName: String?
        get() = sharedPreferences!!.getString("appName", "")
        set(appName) = sharedPreferences!!.edit().putString("appName", appName).apply()

    var currencySymbol: String?
        get() = sharedPreferences!!.getString("currencysymbol", "")
        set(currencySymbol) = sharedPreferences!!.edit().putString("currencysymbol", currencySymbol).apply()

    var appleLoginClientId: String?
        get() = sharedPreferences!!.getString("appleLoginClientId", "")
        set(appleLoginClientId)=sharedPreferences!!.edit().putString("appleLoginClientId", appleLoginClientId).apply()

    var token: String?
        get() = sharedPreferences!!.getString("token", "")
        set(token) = sharedPreferences!!.edit().putString("token", token).apply()

    var facebookId: String?
        get() = sharedPreferences?.getString("facebookid", "")
        set(facebookid) = sharedPreferences!!.edit().putString("facebookid", facebookid).apply()

    var appleId: String?
        get() = sharedPreferences!!.getString("appleid", "")
        set(appleid) = sharedPreferences!!.edit().putString("appleid", appleid).apply()

    var googleId: String?
        get() = sharedPreferences!!.getString("googleid", "")
        set(languagecode) = sharedPreferences!!.edit().putString("googleid", languagecode).apply()

    var accessToken: String?
        get() = sharedPreferences!!.getString("accessToken", "")
        set(accessToken) = sharedPreferences!!.edit().putString("accessToken", accessToken).apply()

    var tokenType: String?
        get() = sharedPreferences!!.getString("tokenType", "")
        set(tokenType) = sharedPreferences!!.edit().putString("tokenType", tokenType).apply()


    var userId: String?
        get() = sharedPreferences!!.getString("userId", "")
        set(userId) = sharedPreferences!!.edit().putString("userId", userId).apply()

    var userEmail: String?
        get() = sharedPreferences!!.getString("userEmail", "")
        set(userEmail) = sharedPreferences!!.edit().putString("userEmail", userEmail).apply()

    var userPhone: String?
        get() = sharedPreferences!!.getString("userPhone", "")
        set(userPhone) = sharedPreferences!!.edit().putString("userPhone", userPhone).apply()

    var userName: String?
        get() = sharedPreferences!!.getString("userName", "")
        set(userName) = sharedPreferences!!.edit().putString("userName", userName).apply()

    var userAvatar: String?
        get() = sharedPreferences!!.getString("userAvatar", "")
        set(userAvatar) = sharedPreferences!!.edit().putString("userAvatar", userAvatar).apply()

    var isSocialLogin: Boolean?
        get() = sharedPreferences!!.getBoolean("isSocialLogin", false)
        set(isSocialLogin) = sharedPreferences!!.edit().putBoolean("isSocialLogin", isSocialLogin!!).apply()


    fun clearToken() {
        sharedPreferences!!.edit().putString("token", "").apply()
    }

    fun clearAll() {
        sharedPreferences!!.edit().clear().apply()
    }
}