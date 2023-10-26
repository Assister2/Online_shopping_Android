package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserProfileModel {

    @SerializedName("success")
    @Expose
    var success: Boolean = false

    @SerializedName("cart_item_count")
    @Expose
    var cartItemCount: Int? = null

    @SerializedName("wishlist_item_count")
    @Expose
    var wishlistCount: Int? = null

    @SerializedName("order_count")
    @Expose
    var orderCount: Int? = null

    @SerializedName("user_type")
    @Expose
    var userType = ""

    @SerializedName("show_delete")
    @Expose
    var showDelete: Boolean = false

    @SerializedName("delete_message")
    @Expose
    var deleteMessage = ""

}