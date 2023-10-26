package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PickupPointModel {

    @SerializedName("data")
    var data    : ArrayList<PickupData> ? = null


    @SerializedName("success")
    var success : Boolean = false


    @SerializedName("status")
    var status  : Int = 0

    class PickupData {
        @SerializedName("id")
        var id : Int = 0

        @SerializedName("staff_id")
        var staffId: Int = 0

        @SerializedName("name")
        var name: String = ""

        @SerializedName("address")
        var address: String = ""

        @SerializedName("phone")
        var phone : String = ""

        @SerializedName("pick_up_status")
        var pickUpStatus : Int = 0

        @SerializedName("cash_on_pickup_status")
        var cashOnPickupStatus : String = ""

        @SerializedName("is_selected")
        @Expose
        var isSelected = false
    }
}