package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserAccessModel {

    @SerializedName("result")
    @Expose
    var result: Boolean = false

    @SerializedName("id")
    @Expose
    var userId = 0

    @SerializedName("type")
    @Expose
    var userType = ""

    @SerializedName("name")
    @Expose
    var userName = ""

    @SerializedName("email")
    @Expose
    var userMail = ""

    @SerializedName("avatar")
    @Expose
    var userAvatar = ""

    @SerializedName("avatar_original")
    @Expose
    var userOriginalAvatar = ""

    @SerializedName("phone")
    @Expose
    var userPhone = ""
}