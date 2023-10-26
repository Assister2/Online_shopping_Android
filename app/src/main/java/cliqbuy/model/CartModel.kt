package cliqbuy.model

import com.google.gson.annotations.SerializedName


data class CartModel(

    @SerializedName("name") var name: String? = null,
    @SerializedName("owner_id") var ownerId: Int? = null,
    @SerializedName("is_pickup_point_enabled") var isPickupPointEnabled: Boolean? = null,
    @SerializedName("cart_items") var cartItems: ArrayList<CartItems> = arrayListOf(),

    ) {
    data class CartItems(

        @SerializedName("id") var id: Int? = null,
        @SerializedName("owner_id") var ownerId: Int? = null,
        @SerializedName("user_id") var userId: Int? = null,
        @SerializedName("product_id") var productId: Int? = null,
        @SerializedName("product_name") var productName: String? = null,
        @SerializedName("product_thumbnail_image") var productThumbnailImage: String? = null,
        @SerializedName("variation") var variation: String? = null,
        @SerializedName("price") var price: Float? = 0f,
        @SerializedName("currency_symbol") var currencySymbol: String? = null,
        @SerializedName("tax") var tax: Int? = null,
        @SerializedName("shipping_cost") var shippingCost: Float? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("lower_limit") var lowerLimit: Int? = null,
        @SerializedName("upper_limit") var upperLimit: Int? = null,
        @SerializedName("item_total_amount") var itemTotalAmount: Float? = null,
        @SerializedName("shop_total_amount") var shopTotalAmount: Float? = null,
        @SerializedName("overall_amount") var overallAmount: Float? = null,
        @SerializedName("shipengineData") var shipengineData: ShipengineData? = ShipengineData(),

        @SerializedName("package_type") var packageType: String? = "",
        @SerializedName("shipengine_found") var shipengineFound: Boolean = false,
        @SerializedName("service_code") var serviceCode: String? = "",
        var selectedPos: Int = 0
    )

    data class EstimateData(

        @SerializedName("delivery_days") var deliveryDays: Int? = null,
        @SerializedName("rate_id") var rateId: String? = null,
        @SerializedName("carrier_id") var carrierId: String? = null,
        @SerializedName("service_code") var serviceCode: String? = null,
        @SerializedName("service_type") var serviceType: String? = null,
        @SerializedName("carrier_code") var carrierCode: String? = null,
        @SerializedName("package_type") var packageType: String? = null,
        @SerializedName("shipping_amount") var shippingAmount: ShippingAmount? = ShippingAmount(),

        )

    data class ShipengineData(

        @SerializedName("id") var id: Int? = null,
        @SerializedName("product_id") var productId: Int? = null,
        @SerializedName("shipengine") var shipengine: Boolean? = null,
        @SerializedName("estimate_data") var estimateData: ArrayList<EstimateData> = arrayListOf(),
        )

    data class ShippingAmount(

        @SerializedName("currency") var currency: String? = null,
        @SerializedName("original_amount") var originalAmount: Double? = null,
        @SerializedName("amount") var amount: String? = null,

        )
}