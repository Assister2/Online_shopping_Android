package cliqbuy.model

import com.google.gson.annotations.SerializedName

class CityListModel {

    @SerializedName("data")
    var data : ArrayList<CityListData> ? = null

    @SerializedName("success")
    var success : Boolean = false


    @SerializedName("status")
    var status  : Int = 0

    class CityListData {

        @SerializedName("id")
        var id  : Int = 0

        @SerializedName("country_id")
        var countryId  : Int = 0

        @SerializedName("cost")
        var cost  : Int = 0

        @SerializedName("name")
        var name: String = ""
    }
}
