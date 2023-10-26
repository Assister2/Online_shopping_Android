package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PaymentMethodsModel {

    @SerializedName("payment_type")
    var paymentType: String = " "

    @SerializedName("payment_type_key")
    var paymentTypeKey: String=" "

    @SerializedName("image")
    var image: String = " "

    @SerializedName("name")
    var name: String = " "

    @SerializedName("title")
    var title: String=" "

    @SerializedName("is_selected")
    @Expose
    var isSelected = false

}