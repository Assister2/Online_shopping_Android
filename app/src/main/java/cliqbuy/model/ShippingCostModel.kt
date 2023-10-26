package cliqbuy.model

import com.google.gson.annotations.SerializedName

class ShippingCostModel {

    @SerializedName("result")
    var result : Boolean = false

    @SerializedName("shipping_type")
    var shippingType: String = ""

    @SerializedName("value_string")
    var valueString: String = ""

}