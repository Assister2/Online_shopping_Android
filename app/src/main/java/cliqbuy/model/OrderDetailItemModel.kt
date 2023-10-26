package cliqbuy.model

import com.google.gson.annotations.SerializedName


data class OrderDetailItemModel (

    @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf(),
    @SerializedName("success" ) var success : Boolean?        = null,
    @SerializedName("status"  ) var status  : Int?            = null

){
    data class Data (

        @SerializedName("id"                     ) var id                   : Int?     = null,
        @SerializedName("product_id"             ) var productId            : Int?     = null,
        @SerializedName("product_name"           ) var productName          : String?  = null,
        @SerializedName("variation"              ) var variation            : String?  = null,
        @SerializedName("price"                  ) var price                : String?  = null,
        @SerializedName("tax"                    ) var tax                  : String?  = null,
        @SerializedName("shipping_cost"          ) var shippingCost         : String?  = null,
        @SerializedName("coupon_discount"        ) var couponDiscount       : String?  = null,
        @SerializedName("quantity"               ) var quantity             : Int?     = null,
        @SerializedName("payment_status"         ) var paymentStatus        : String?  = null,
        @SerializedName("payment_status_string"  ) var paymentStatusString  : String?  = null,
        @SerializedName("delivery_status"        ) var deliveryStatus       : String?  = null,
        @SerializedName("delivery_status_string" ) var deliveryStatusString : String?  = null,
        @SerializedName("refund_section"         ) var refundSection        : Boolean? = null,
        @SerializedName("refund_button"          ) var refundButton         : Boolean? = null,
        @SerializedName("refund_label"           ) var refundLabel          : String?  = null,
        @SerializedName("refund_request_status"  ) var refundRequestStatus  : Int?     = null

    )
}