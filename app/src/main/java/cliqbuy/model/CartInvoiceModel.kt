package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CartInvoiceModel {

    @SerializedName("sub_total")
    @Expose
    var subTotal: String? = ""

    @SerializedName("tax")
    @Expose
    var tax: String? =""

    @SerializedName("shipping_cost")
    @Expose
    var shippingCost: String? = ""

    @SerializedName("discount")
    @Expose
    var discount: String? = ""

    @SerializedName("grand_total")
    @Expose
    var grandTotal: String = " "

    @SerializedName("grand_total_value")
    @Expose
    var grandTotalValue: Float = 0F

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = ""

    @SerializedName("coupon_applied")
    @Expose
    var couponApplied = false

    @SerializedName("is_coupon_enabled")
    @Expose
    var isCouponEnabled = false


}