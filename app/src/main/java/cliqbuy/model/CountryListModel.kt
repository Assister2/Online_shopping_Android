package cliqbuy.model

import com.google.gson.annotations.SerializedName

class CountryListModel {
    @SerializedName("data")
    var data : ArrayList<CountryListData> ? = null

    @SerializedName("success")
    var success : Boolean = false


    @SerializedName("status")
    var status  : Int = 0

    class CountryListData {

        @SerializedName("id")
        var id  : Int = 0

        @SerializedName("status")
        var status  : Int = 0

        @SerializedName("code")
        var code: String = ""

        @SerializedName("name")
        var name: String = ""
    }
}
