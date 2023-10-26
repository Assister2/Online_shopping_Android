package cliqbuy.model

import com.google.gson.annotations.SerializedName

data class ProductSearchDataModel(
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),
    @SerializedName("links") var links: OtherLinks? = OtherLinks(),
    @SerializedName("meta") var meta: Meta? = Meta(),
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("status") var status: Int? = null

) {

    data class MetaLinks(
        @SerializedName("url") var url: String? = null,
        @SerializedName("label") var label: String? = null,
        @SerializedName("active") var active: Boolean? = null
    )

    data class DataLinks(
        @SerializedName("details") var details: String? = null,
        @SerializedName("products") var products: String? = null
    )

    data class OtherLinks(
        @SerializedName("first") var first: String? = null,
        @SerializedName("last") var last: String? = null,
        @SerializedName("prev") var prev: String? = null,
        @SerializedName("next") var next: String? = null
    )

    data class Data(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("thumbnail_image") var thumbnailImage: String? = null,
        @SerializedName("has_discount") var hasDiscount: Boolean? = null,
        @SerializedName("stroked_price") var strokedPrice: String? = null,
        @SerializedName("main_price") var mainPrice: String? = null,
        @SerializedName("logo") var logo: String? = null,
        @SerializedName("rating") var rating: Int? = null,
        @SerializedName("sales") var sales: Int? = null,
        @SerializedName("links") var links: DataLinks? = DataLinks()
    )

    data class Meta(
        @SerializedName("current_page") var currentPage: Int? = null,
        @SerializedName("from") var from: Int? = null,
        @SerializedName("last_page") var lastPage: Int? = null,
        @SerializedName("links") var links: ArrayList<MetaLinks> = arrayListOf(),
        @SerializedName("path") var path: String? = null,
        @SerializedName("per_page") var perPage: Int? = null,
        @SerializedName("to") var to: Int? = null,
        @SerializedName("total") var total: Int? = null
    )
}