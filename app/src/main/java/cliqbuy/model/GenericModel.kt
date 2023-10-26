package cliqbuy.model

import com.google.gson.annotations.SerializedName

class GenericModel {

    @SerializedName("success")
    var success: Boolean = false

    @SerializedName("result")
    var result: Boolean = false
    @SerializedName("ship_engine_error")
    var shipEngineError: Boolean = false

    @SerializedName("message")
    var message: String = ""

    @SerializedName("status")
    var status: Int = 0

    @SerializedName("path")
    var userImage: String = ""

    @SerializedName("url")
    var url: String = ""

    @SerializedName("original_address")
    lateinit var originalAddress: AddressModel

    @SerializedName("matched_address")
    lateinit var matchedAddress: AddressModel

    @SerializedName("ship_engine")
     var isShipEngineEnabled: Boolean =false


    data class AddressModel(

        @SerializedName("name") var name: String? = null,
        @SerializedName("phone") var phone: String? = null,
        @SerializedName("company_name") var companyName: String? = null,
        @SerializedName("address_line1") var addressLine1: String? = null,
        @SerializedName("address_line2") var addressLine2: String? = null,
        @SerializedName("address_line3") var addressLine3: String? = null,
        @SerializedName("city_locality") var cityLocality: String? = null,
        @SerializedName("state_province") var stateProvince: String? = null,
        @SerializedName("postal_code") var postalCode: String? = null,
        @SerializedName("country_code") var countryCode: String? = null,
        @SerializedName("address_residential_indicator") var addressResidentialIndicator: String? = null,

        )

}