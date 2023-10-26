package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by trioangle on 6/9/18.
 */

class CommonDataModel {

    @SerializedName("success")
    @Expose
    var success: Boolean = false

    @SerializedName("result")
    @Expose
    var result: Boolean = false

    @SerializedName("status")
    @Expose
    var statusCode = 0

    @SerializedName("google_enabled")
    @Expose
    var isGoogleEnabled: Boolean = false

    @SerializedName("facebook_enabled")
    @Expose
    var isFacebookEnabled: Boolean = false

    @SerializedName("apple_enabled")
    @Expose
    var isAppleEnabled: Boolean = false

    @SerializedName("currency_symbol")
    @Expose
    var currencySymbol: String = ""

    @SerializedName("currency_name")
    @Expose
    var currencyName: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""
    @SerializedName("sitename")
    @Expose
    var sitename: String = ""


}
