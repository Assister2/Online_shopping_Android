package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OtpVerificationModel {

    @SerializedName("message")
    @Expose
    var statusMessage: String=""

    @SerializedName("result")
    @Expose
    var result: Boolean = false
}