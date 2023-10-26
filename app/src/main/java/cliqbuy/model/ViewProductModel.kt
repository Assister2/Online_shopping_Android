package cliqbuy.model

import com.google.gson.annotations.SerializedName


data class ViewProductModel (

    @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf(),
    @SerializedName("links"   ) var links   : Link?          = Link(),
    @SerializedName("meta"    ) var meta    : Meta?           = Meta(),
    @SerializedName("success" ) var success : Boolean?        = null,
    @SerializedName("status"  ) var status  : Int?            = null

){
    data class Links (

        @SerializedName("details" ) var details : String? = null

    )
    data class Data (

        @SerializedName("id"              ) var id             : Int?     = null,
        @SerializedName("name"            ) var name           : String?  = null,
        @SerializedName("thumbnail_image" ) var thumbnailImage : String?  = null,
        @SerializedName("has_discount"    ) var hasDiscount    : Boolean? = null,
        @SerializedName("stroked_price"   ) var strokedPrice   : String?  = null,
        @SerializedName("main_price"      ) var mainPrice      : String?  = null,
        @SerializedName("rating"          ) var rating         : Int?     = null,
        @SerializedName("sales"           ) var sales          : Int?     = null,
        @SerializedName("links"           ) var links          : Links?   = Links()

    )
    data class Link (

        @SerializedName("first" ) var first : String? = null,
        @SerializedName("last"  ) var last  : String? = null,
        @SerializedName("prev"  ) var prev  : String? = null,
        @SerializedName("next"  ) var next  : String? = null

    )
    data class Metas (

        @SerializedName("url"    ) var url    : String?  = null,
        @SerializedName("label"  ) var label  : String?  = null,
        @SerializedName("active" ) var active : Boolean? = null

    )
    data class Meta (

        @SerializedName("current_page" ) var currentPage : Int?             = null,
        @SerializedName("from"         ) var from        : Int?             = null,
        @SerializedName("last_page"    ) var lastPage    : Int?             = null,
        @SerializedName("links"        ) var Metas       : ArrayList<Metas> = arrayListOf(),
        @SerializedName("path"         ) var path        : String?          = null,
        @SerializedName("per_page"     ) var perPage     : Int?             = null,
        @SerializedName("to"           ) var to          : Int?             = null,
        @SerializedName("total"        ) var total       : Int?             = null

    )
}