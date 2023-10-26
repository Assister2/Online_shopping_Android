package cliqbuy.model

import com.google.gson.annotations.SerializedName

data class UpdateQuantityModel(

    @SerializedName("status") var status: Boolean = false,
    @SerializedName("message") var message: String? = null,
    @SerializedName("rate_id") var rateId: String? = null,
)