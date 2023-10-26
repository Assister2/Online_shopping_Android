package cliqbuy.model

import com.google.gson.annotations.SerializedName


data class PurchaseHistoryModel(

    @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),
    @SerializedName("meta") var meta: Meta? = Meta(),
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("status") var status: Int? = null,

    ) {


    data class Data(

        @SerializedName("id") var id: Int? = null,
        @SerializedName("code") var code: String? = null,
        @SerializedName("user_id") var userId: Int? = null,
        @SerializedName("payment_type") var paymentType: String? = null,
        @SerializedName("payment_status") var paymentStatus: String? = null,
        @SerializedName("payment_status_string") var paymentStatusString: String? = null,
        @SerializedName("delivery_status") var deliveryStatus: String? = null,
        @SerializedName("delivery_status_string") var deliveryStatusString: String? = null,
        @SerializedName("grand_total") var grandTotal: String? = null,
        @SerializedName("date") var date: String? = null,
        @SerializedName("tracking_number") var trackingNumebr: String? = null,
        @SerializedName("label_download") var labelDownload: String? = null,

        )

    data class Meta(

        @SerializedName("current_page") var currentPage: Int? = null,
        @SerializedName("from") var from: Int? = null,
        @SerializedName("last_page") var lastPage: Int? = null,
        @SerializedName("path") var path: String? = null,
        @SerializedName("per_page") var perPage: Int? = null,
        @SerializedName("to") var to: Int? = null,
        @SerializedName("total") var total: Int? = null,

        )

}