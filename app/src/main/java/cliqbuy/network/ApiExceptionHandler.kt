package cliqbuy.network

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import cliqbuy.BuildConfig
import cliqbuy.R
import cliqbuy.common.CommonMethods
import cliqbuy.configs.AppController
import cliqbuy.helper.Constants
import cliqbuy.helper.SessionManager
import cliqbuy.model.JsonResponse
import cliqbuy.ui.view.MaintenanceActivity
import cliqbuy.ui.view.SplashActivity
import cliqbuy.utils.LogManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class ApiExceptionHandler {
    var apiResponseHandler = ApiResponseHandler()


    @Inject
    lateinit var jsonResp: JsonResponse

    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var sessionManager: SessionManager

    init {
        AppController.appComponent!!.inject(this)

    }


    suspend fun exceptionHandler(response: Response<Any>?, context: Context): ApiResponseHandler {
        try {

            if (response != null) {
                if (response.isSuccessful && response.body() != null) {

                    println("JSON RESPONSE : " + gson.toJson(response!!.body()))
                    apiResponseHandler = ApiResponseHandler()
                    apiResponseHandler.isSuccess = true
                    apiResponseHandler.errorResonse = ""

                } else {
                    errorHandling(response, context)
                }

            } else {
                errorResponse(context.getString(R.string.internal_server_error))
            }


        } catch (e: HttpException) {
            errorResponse(e.message!!)

        } catch (e: Throwable) {
            errorResponse(e.message!!)
        }
        return apiResponseHandler
    }

    private fun errorHandling(response: Response<Any>, context: Context) {
        if (response.errorBody() != null) {

            if (response.code() == 401){
                commonMethods.showToast(context as Activity,context.getString(R.string.internal_server_error))
            }
            if (response.code() == 500){
                commonMethods.showToast(context as Activity,context.getString(R.string.internal_server_error))
            }
            if (response.code() == 404) {

                val lang = sessionManager.language
                val langCode = sessionManager.languageCode
                val currency = sessionManager.currencyCode
                val currencysymbol = sessionManager.currencySymbol
                val clientId = context.resources.getString(R.string.apple_client_id)

                sessionManager.clearToken()
                sessionManager.clearAll()

                sessionManager.language = lang
                sessionManager.languageCode = langCode
                sessionManager.currencyCode = currency
                sessionManager.appleLoginClientId = clientId
                sessionManager.currencySymbol = currencysymbol

                val intent = Intent(context, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                (context as Activity).finish()
                (context as Activity).finishAffinity()
                errorResponse(context.getString(R.string.token_expired))

            } else if (response.code() == 503) {
                if (!MaintenanceActivity.isInMaintenance) {
                    val intent = Intent(context, MaintenanceActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            } else {
                errorResponse(context.getString(R.string.internal_server_error))
            }

        } else {
            errorResponse(context.getString(R.string.internal_server_error))
        }
    }

    fun errorResponse(errorMessage: String) {
        apiResponseHandler = ApiResponseHandler()
        apiResponseHandler.isSuccess = false
        apiResponseHandler.errorResonse = errorMessage
    }


    fun mapResponseFromServer(context: Context, response: Response<ResponseBody?>?): JsonResponse? {
        try {
            if (response != null) {
                jsonResp.responseCode = response.code()
                jsonResp.isSuccess = false
                jsonResp.statusMsg = context.resources.getString(R.string.internal_server_error)
                if (response.isSuccessful && response.body() != null) {
                    val strJson = response.body()!!.string()
                    jsonResp.strResponse = strJson
                    jsonResp.statusMsg = getStatusMsg(strJson)
                    jsonResp.statusCode = getStatusCode(strJson)
                    if (jsonResp.statusMsg.equals("Token Expired", ignoreCase = true)) {
                        jsonResp.statusMsg =
                            context.resources.getString(R.string.internal_server_error)
                    }
                    jsonResp.isSuccess = isSuccess(strJson)
                    if (BuildConfig.DEBUG) {
                        LogManager.e(strJson)
                    }

                } else {
                    val strJson = response.errorBody()!!.string()
                    jsonResp.errorMsg = response.errorBody()!!.string()
                    errorHandling(response as Response<Any>, context)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonResp
    }

    private fun getStatusMsg(jsonString: String): String {
        var statusMsg = ""
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                statusMsg = commonMethods.getJsonValue(
                    jsonString,
                    Constants.STATUS_MSG,
                    String::class.java
                ) as String
                if ("Token Expired".equals(statusMsg, ignoreCase = true)) {
                    val token = commonMethods.getJsonValue(
                        jsonString,
                        Constants.REFRESH_ACCESS_TOKEN,
                        String::class.java
                    ) as String
                    sessionManager.token = token
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return statusMsg
    }

    private fun getStatusCode(jsonString: String): String {
        var statuscode = ""
        var statusMsg = ""
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                statuscode = commonMethods.getJsonValue(
                    jsonString,
                    Constants.STATUS_CODE,
                    String::class.java
                ) as String
                statusMsg = commonMethods.getJsonValue(
                    jsonString,
                    Constants.STATUS_MSG,
                    String::class.java
                ) as String
                if ("Token Expired".equals(statusMsg, ignoreCase = true)) {
                    val token = commonMethods.getJsonValue(
                        jsonString,
                        Constants.REFRESH_ACCESS_TOKEN,
                        String::class.java
                    ) as String
                    sessionManager.token = token
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return statuscode
    }

    private fun isSuccess(jsonString: String): Boolean {
        var isSuccess = false
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                val statusCode = commonMethods.getJsonValue(
                    jsonString,
                    Constants.STATUS_CODE,
                    String::class.java
                ) as String
                isSuccess =
                    !TextUtils.isEmpty(statusCode) && ("1" == statusCode || "2" == statusCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isSuccess
    }

}