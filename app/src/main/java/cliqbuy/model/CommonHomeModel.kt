package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class CommonHomeModel {

    @SerializedName("message")
    @Expose
    var statusMessage: String=""

    @SerializedName("result")
    @Expose
    var result: Boolean = false

    @SerializedName("success")
    @Expose
    var success: Boolean = false

    @SerializedName("is_in_wishlist")
    @Expose
    var isInWishlist: Boolean = false

    @SerializedName("status")
    @Expose
    var status: Int=0

    @SerializedName("data")
    @Expose
    var data: ArrayList<PhotoModel>? = null

    @SerializedName("type")
    @Expose
    var type: String=""

    class PhotoModel {
        @SerializedName("photo")
        @Expose
        var photo: String? = null

        @SerializedName("id")
        @Expose
        var id: Int=0

        @SerializedName("name")
        @Expose
        var name: String=""

        @SerializedName("banner")
        @Expose
        var banner: String=""

        @SerializedName("icon")
        @Expose
        var icon: String=""

        @SerializedName("main_price")
        @Expose
        var mainPrice: String=""

        @SerializedName("stroked_price")
        @Expose
        var strokedPrice: String=""

        @SerializedName("has_discount")
        @Expose
        var hasDiscount: Boolean = false

        @SerializedName("thumbnail_image")
        @Expose
        var thumbnailImage: String=""

        @SerializedName("number_of_children")
        @Expose
        var numberOfChildren: Int=0

        @SerializedName("links")
        @Expose
        var links: LinksModel? = null

        @SerializedName("title")
        @Expose
        var title: String=""

        @SerializedName("date")
        @Expose
        var flashDealTimer: Long=0
    }

    class LinksModel {

        @SerializedName("products")
        @Expose
        var products: String=""

        @SerializedName("sub_categories")
        @Expose
        var subCategories: String=""
    }
}