package cliqbuy.model

import com.google.gson.annotations.SerializedName


data class OrderDetailsModel(

    @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("status") var status: Int? = null,

    ) {
    data class ShippingAddress(

        @SerializedName("name") var name: String? = null,
        @SerializedName("email") var email: String? = null,
        @SerializedName("address") var address: String? = null,
        @SerializedName("country") var country: String? = null,
        @SerializedName("city") var city: String? = null,
        @SerializedName("postal_code") var postalCode: String? = null,
        @SerializedName("phone") var phone: String? = null,

        )

    data class Data(

        @SerializedName("id") var id: Int? = null,
        @SerializedName("code") var code: String? = null,
        @SerializedName("user_id") var userId: Int? = null,
        @SerializedName("shipping_address") var shippingAddress: ShippingAddress? = ShippingAddress(),
        @SerializedName("payment_type") var paymentType: String? = null,
        @SerializedName("shipping_type") var shippingType: String? = null,
        @SerializedName("shipping_type_string") var shippingTypeString: String? = null,
        @SerializedName("payment_status") var paymentStatus: String? = null,
        @SerializedName("payment_status_string") var paymentStatusString: String? = null,
        @SerializedName("delivery_status") var deliveryStatus: String? = null,
        @SerializedName("delivery_status_string") var deliveryStatusString: String? = null,
        @SerializedName("grand_total") var grandTotal: String? = null,
        @SerializedName("coupon_discount") var couponDiscount: String? = null,
        @SerializedName("shipping_cost") var shippingCost: String? = null,
        @SerializedName("subtotal") var subtotal: String? = null,
        @SerializedName("tax") var tax: String? = null,
        @SerializedName("pickup_point_name") var pickupPointName: String? = null,
        @SerializedName("pickup_point_address") var pickupPointAddress: String? = null,
        @SerializedName("pickup_point_phone") var pickupPointPhone: String? = null,
        @SerializedName("date") var date: String? = null,
        @SerializedName("cancel_request") var cancelRequest: Boolean? = null,
        @SerializedName("tracking_number") var trackingNumebr: String? = null,
        @SerializedName("label_download") var labelDownload: String? = null,
    )
}