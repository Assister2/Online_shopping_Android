package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Created by trioangle on 14/2/23.
 */

class SigninSignupModel : Serializable {

    @SerializedName("message")
    @Expose
    var statusMessage: String = ""

    @SerializedName("success_message")
    @Expose
    var successMessage: String = ""

    @SerializedName("result")
    @Expose
    var result: Boolean = false

    @SerializedName("user_id")
    @Expose
    var userId = 0

    @SerializedName("access_token")
    @Expose
    var accessToken = " "

    @SerializedName("token_type")
    @Expose
    var userTokenType = " "

    @SerializedName("user")
    @Expose
    var user: UserDetails? = null

    class UserDetails : Serializable {

        @SerializedName("id")
        @Expose
        var id = 0

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


        @SerializedName("user_address_count")
        @Expose
        var isNeedAddress: Boolean = false

    }
}
