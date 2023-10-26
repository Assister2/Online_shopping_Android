package cliqbuy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SellerModels (



    @SerializedName("data"    ) var data    : ArrayList<SellerData> = arrayListOf(),
    @SerializedName("links"   ) var links   : Link?          = Link(),
    @SerializedName("meta"    ) var meta    : Meta?           = Meta(),
    @SerializedName("success" ) var success : Boolean?        = null,
    @SerializedName("status"  ) var status  : Int?            = null


){

    data class Links (

        @SerializedName("details" ) var details : String? = null

    )

    data class SellerData (

        @SerializedName("id"              ) var id             : Int?     = null,
        @SerializedName("name"            ) var name           : String?  = null,
        @SerializedName("logo" )            var logo : String?  = null,
        @SerializedName("links"           ) var links          : Links?   = Links()

    )

    data class Link (

        @SerializedName("products" ) var products : String? = null,

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