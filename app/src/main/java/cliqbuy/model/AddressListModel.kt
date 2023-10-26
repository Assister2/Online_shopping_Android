package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddressListModel {

    @SerializedName("data")
    var data : ArrayList<AddressListData> ? = null


    @SerializedName("success")
    var success : Boolean = false

    @SerializedName("result")
    var result : Boolean = false

    @SerializedName("message")
    var message: String = ""

    @SerializedName("status")
    var status  : Int = 0

    class AddressListData {
        @SerializedName("id")
        var id : Int = 0

        @SerializedName("lat")
        var lat : Float = 0F

        @SerializedName("lang")
        var lang : Float = 0F

        @SerializedName("set_default")
        var setDefault : Int = 0

        @SerializedName("user_id")
        var userId: Int = 0

        @SerializedName("address")
        var address: String = ""

        @SerializedName("country")
        var country: String = ""

        @SerializedName("city")
        var city: String = ""

        @SerializedName("phone")
        var phone: String = ""

        @SerializedName("postal_code")
        var postalCode: String = ""

        @SerializedName("state")
        var state: String = ""

        @SerializedName("location_available")
        var locationAvailable : Boolean = false

        @SerializedName("is_selected")
        @Expose
        var isSelected = false
    }
}