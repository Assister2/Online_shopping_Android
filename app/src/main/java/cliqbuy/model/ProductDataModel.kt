package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductDataModel(

    @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),
    @SerializedName("success") var success: Boolean = false,
    @SerializedName("status") var status: Int = 0,
    @SerializedName("is_coupon_enabled") var isCouponEnabled: Boolean = false,
    @SerializedName("is_chat_enabled") var isChatEnabled: Boolean = false,

    ) {

    data class Photos(

        @SerializedName("variant") var variant: String = "",
        @SerializedName("path") var path: String = "",
        @SerializedName("is_selected") var isSelected: Boolean = false,

        )

    data class Data(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("added_by") var addedBy: String = "",
        @SerializedName("seller_id") var sellerId: Int = 0,
        @SerializedName("shop_id") var shopId: Int = 0,
        @SerializedName("shop_name") var shopName: String = "",
        @SerializedName("shop_logo") var shopLogo: String = "",
        @SerializedName("photos") var photos: ArrayList<Photos> = arrayListOf(),
        @SerializedName("thumbnail_image") var thumbnailImage: String = "",
        @SerializedName("tags") var tags: ArrayList<String> = arrayListOf(),
        @SerializedName("price_high_low") var priceHighLow: String = "",
        /*@SerializedName("choice_options"   ) var choiceOptions   : ArrayList<String> = arrayListOf(),
        @SerializedName("colors"           ) var colors          : ArrayList<String> = arrayListOf(),*/
        @SerializedName("has_discount") var hasDiscount: Boolean = false,
        @SerializedName("stroked_price") var strokedPrice: String = "",
        @SerializedName("main_price") var mainPrice: String = "",
        @SerializedName("calculable_price") var calculablePrice: Float = 0f,
        @SerializedName("currency_symbol") var currencySymbol: String = "",
        @SerializedName("current_stock") var currentStock: Int = 0,
        @SerializedName("unit") var unit: String = "",
        @SerializedName("rating") var rating: Int = 0,
        @SerializedName("rating_count") var ratingCount: Int = 0,
        @SerializedName("earn_point") var earnPoint: Int = 0,
        @SerializedName("minimum_purchase_qty") var minimumPurchaseQty: Int = 1,
        @SerializedName("description") var description: String? = null,
        @SerializedName("link") var link: String = "",
        @SerializedName("pdf_url") var pdfUrl: String = "",
        @SerializedName("is_show_stock_qty") var isShowStockQty: Boolean = false,
        @SerializedName("merchant_ship_engine_hold") var isMerchantEnabledShipEngine: Boolean = false,
        @SerializedName("user_address_count") var isUserNeedAddress: Boolean = false,
        @SerializedName("shipengine_enabled") var isShipEngineEnabled: String = "0",
        @SerializedName("is_show_stock_with_text") var isShowStockWithText: Boolean = false,
        @SerializedName("is_hide_stock") var isHideStock: Boolean = false,
        @SerializedName("shipping_type") var shippingType: Boolean = false,
        @SerializedName("shipping_lowest_original_price") var shippingEngineLowestPriceOriginal: Double = 0.0,
        @SerializedName("shipping_lowest_price") var shippingEngineLowestPrice: String = "",
        @SerializedName("shipengine_image") var shipEngineIamge: String = "",
        @SerializedName("delivery_days") var deliveryDays: Int = 0,

        )
}


