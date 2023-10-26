package cliqbuy.model

import com.google.gson.annotations.SerializedName


data class StoreDetailsModel (

  @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf(),
  @SerializedName("success" ) var success : Boolean?        = null,
  @SerializedName("status"  ) var status  : Int?            = null

){
  data class Data (

    @SerializedName("id"          ) var id         : Int?              = null,
    @SerializedName("user_id"     ) var userId     : Int?              = null,
    @SerializedName("name"        ) var name       : String?           = null,
    @SerializedName("logo"        ) var logo       : String?           = null,
    @SerializedName("sliders"     ) var sliders    : ArrayList<String> = arrayListOf(),
    @SerializedName("address"     ) var address    : String?           = null,
    @SerializedName("facebook"    ) var facebook   : String?           = null,
    @SerializedName("google"      ) var google     : String?           = null,
    @SerializedName("twitter"     ) var twitter    : String?           = null,
    @SerializedName("true_rating" ) var trueRating : Int?              = null,
    @SerializedName("rating"      ) var rating     : Int?              = null

  )
}