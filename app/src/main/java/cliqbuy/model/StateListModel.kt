package cliqbuy.model

import com.google.gson.annotations.SerializedName

class StateListModel {

    @SerializedName("data")
    var data : ArrayList<StateListData> ? = null

    @SerializedName("success")
    var success : Boolean = false


    @SerializedName("status")
    var status  : Int = 0

    class StateListData {

        @SerializedName("id")
        var id  : Int = 0

        @SerializedName("cost")
        var cost  : Int = 0

        @SerializedName("name")
        var name: String = ""
    }
}
