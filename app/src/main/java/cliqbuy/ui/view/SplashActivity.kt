package cliqbuy.ui.view

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import android.widget.TextView
import androidx.lifecycle.Observer
import cliqbuy.BuildConfig
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums.REQ_COMMON_DATA
import cliqbuy.model.CommonDataModel
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : CommonActivity() {

    lateinit var commonDataModel: CommonDataModel
    lateinit var tvVersion : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initViewModel()
        initApiResponseHandler()
        getCommonData()
        println("### -- lang code    ${sessionManager.languageCode}")
        tvVersion = findViewById(R.id.tv_app_version)
        tvVersion.text = "V "+ BuildConfig.VERSION_NAME
    }


    private fun getCommonData() {

        if (commonMethods.isOnline(this)) {

            commonViewModel!!.apiRequest(REQ_COMMON_DATA, commonHashMap, this)
        } else {
            commonMethods.showToastLong(this, resources.getString(R.string.no_internet_connection))
        }

    }


    private fun initApiResponseHandler() {
        commonViewModel!!.liveDataResponse.observe(this, Observer {


            if (commonViewModel!!.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    REQ_COMMON_DATA -> {
                        commonDataModel = commonViewModel?.liveDataResponse?.value as (CommonDataModel)

                        if (commonDataModel.success) {

                            CommonKeys.isGoogleEnabled = commonDataModel.isGoogleEnabled
                            CommonKeys.isFacebookEnabled = commonDataModel.isFacebookEnabled
                            CommonKeys.isAppleEnabled = commonDataModel.isAppleEnabled
                            sessionManager.currencySymbol = commonDataModel.currencySymbol
                            sessionManager.currencyName = commonDataModel.currencyName
                            sessionManager.appName=commonDataModel.sitename
                            callHandler()
                        }else{
                            commonMethods.showToast(this,"failure") //for_temp
                        }

                    }
                }
            }
        })
    }

    private fun callHandler() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            val intent = Intent(this, HomeActivity::class.java)
            intent.also { it.putExtra(CommonKeys.fragmentType,1) }
            val animation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.cb_fade_in,
                R.anim.cb_face_out
            ).toBundle()
            startActivity(intent, animation)
            finish()
        }, 2000)    }
}