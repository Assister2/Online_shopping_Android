package cliqbuy.ui.view

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatDelegate
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityWebViewBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.CommonKeys.REDIRECT_URL
import cliqbuy.helper.CommonKeys.STRIPE
import cliqbuy.helper.CommonKeys.defaultPaymentType
import cliqbuy.helper.CommonKeys.payWith
import cliqbuy.helper.Enums
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import java.util.HashMap

class WebViewActivity : CommonActivity() {

    lateinit var binding: ActivityWebViewBinding
    var type: String? = null

    var isfromTrack = true
    lateinit var progressDialog: ProgressDialog


    var title = ""
    var paymentMode = ""
    var url = ""
    var totalAmount = ""
    var userId = ""

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.header.ivBack.visibility = View.VISIBLE
        binding.header.tvTitle.visibility = View.VISIBLE
        getIntentValues()

        if (intent.getBooleanExtra("isFromTrack", false)) {
            binding.header.tvTitle.text = resources.getString(R.string.tracking)
        } else {
            binding.header.tvTitle.text = resources.getString(R.string.pay_with) + " " + title
        }

        commonMethods.showProgressDialog(this@WebViewActivity)

        binding.header.ivBack.setSafeOnClickListener { onBackPressed() }

        /*
        binding.paymentWv.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.e(TAG, "onConsoleMessage: console " + consoleMessage.message())
                return true
            }
        }
*/

        binding.paymentWv.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("pageloaded", "onPageStarted")
                if (intent.getBooleanExtra("isFromTrack", false).not())
                    commonMethods.hideProgressDialog()

            }

            override fun onPageFinished(view: WebView, url: String) {
                commonMethods.hideProgressDialog()
                commonMethods.hideProgressDialog()
                Log.d("pageloaded", "onPageFinished")
                binding.paymentWv.loadUrl("javascript:android.showHTML(document.body.getElementsByTagName('pre')[0].innerHTML);")

            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                super.onReceivedError(view, request, error)
                Log.d("pageloaded", "onReceivedError")

                commonMethods.hideProgressDialog()
            }

        }
        binding.paymentWv.settings.javaScriptEnabled = true
        binding.paymentWv.addJavascriptInterface(
            MyJavaScriptInterface(applicationContext),
            "android"
        )

        if (url.isNotEmpty()) {
            val language = "&language=${getLanguage()}"
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                if (paymentMode == STRIPE) {
                    val stripeData =
                        "amount=$totalAmount&payment_type=$defaultPaymentType&user_id=$userId"
                    binding.paymentWv.loadUrl(url + stripeData + language)
                } else binding.paymentWv.loadUrl(url)

            } else {
                if (paymentMode == STRIPE) {
                    val stripeData =
                        "amount=$totalAmount&payment_type=$defaultPaymentType&user_id=$userId"
                    Log.e("url", url + stripeData)
                    binding.paymentWv.loadUrl(url + stripeData + language)
                    println("payment url $url$stripeData")
                } else binding.paymentWv.loadUrl(url + language)

            }
        }
    }

    private fun getLanguage(): String {
        return sessionManager.languageCode.let {
            if (it == "ar") "sa"
            else "en"
        }
    }

    private fun getIntentValues() {

        if (intent.hasExtra(REDIRECT_URL)) {
            url = intent.getStringExtra(REDIRECT_URL)!!
            if (intent.getBooleanExtra("isFromTrack", false)) {
                if (url.contains(".pdf", ignoreCase = true))
                    url = "http://docs.google.com/gview?embedded=true&url=" + url
            }
        }
        if (intent.hasExtra("paymentMode")) {
            title = intent.getStringExtra("paymentMode").toString()
            paymentMode = title
        }
        if (intent.hasExtra("amount")) {
            totalAmount = intent.getStringExtra("amount").toString()
        }
        if (intent.hasExtra("id")) {
            userId = intent.getStringExtra("id").toString()
        }
    }


    override fun onBackPressed() {
        if (binding.paymentWv.canGoBack()) {
            binding.paymentWv.goBack()
            finish()
        } else {
            super.onBackPressed()
            finish()
        }
    }

    private fun setProgress() {
        progressDialog = ProgressDialog(this)
        // progressDialog.setMessage(resources.getString(R.string.loading))
        progressDialog.setCancelable(false)
    }


    inner class MyJavaScriptInterface(private var ctx: Context) {
        @JavascriptInterface
        fun showHTML(html: String) {
            println("HTML$html")
            Log.d("pageloaded", "showHTML")
            var response: JSONObject? = null
            commonMethods.hideProgressDialog()
            try {
                response = JSONObject(html)
                if (response != null) {
                    //    Log.d("responses", response.getBoolean("ship_engine_error").toString())

                    if (response.has("ship_engine_error") && response.getBoolean("ship_engine_error")) {

                        gobacktocart()
                    }

                    val result = response.getBoolean("result")
                    val statusMessage = response.getString("message")
                    Log.d("web_result", result.toString())
                    Log.d("web_message", statusMessage)
                    redirect(response.toString())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (t: Throwable) {
                Log.e("My App", "${t.message} \"$response\"")
            }
        }
    }

    private fun callCartApi() {
        commonMethods.showProgressDialog(this@WebViewActivity)
        var cartHashMap: HashMap<String, String> = HashMap()
        cartHashMap["id"] = sessionManager.userId.toString()
        commonViewModel!!.apiRequest(
            Enums.REQ_DELIVERY_INFO,
            cartHashMap,
            this@WebViewActivity
        )
    }

    fun gobacktocart() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType, 3) }
        intent.also { it.putExtra("redirect", 0) }
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
        callCartApi()
    }

    private fun redirect(htmlResponse: String) {
        try {

            val intent = Intent()
            intent.putExtra("response", htmlResponse)
            setResult(RESULT_OK, intent)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}