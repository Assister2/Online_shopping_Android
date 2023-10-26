package cliqbuy.model

import com.google.gson.annotations.SerializedName
import java.util.*

class WishlistModel {

    @SerializedName("data")
    var data: ArrayList<Data> = arrayListOf()

    @SerializedName("success")
    var success: Boolean = false

    @SerializedName("result")
    var result: Boolean = false

    @SerializedName("message")
    var successMsg =""

    @SerializedName("status")
    var status: Int? = null

    class Data {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("product")
        var product: Product? = Product()
    }

    class Product {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var itemName: String? = null

        @SerializedName("thumbnail_image")
        var thumbnailImage: String? = null

        @SerializedName("base_price")
        var itemPrice: String? = null

        @SerializedName("rating")
        var rating: Int? = null
    }


}