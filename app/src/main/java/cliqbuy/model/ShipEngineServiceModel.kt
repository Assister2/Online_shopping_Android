package cliqbuy.model

import com.google.gson.annotations.SerializedName

data class ShipEngineServiceModel(

  @SerializedName("status") var status: Boolean? = null,
  @SerializedName("destination_zip_code") var destinationZipCode: String? = null,
  @SerializedName("ship_quantity") var shipQuantity: String? = null,
  @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),

  ) {
    data class Data(

      @SerializedName("delivery_days") var deliveryDays: Int? = null,
      @SerializedName("rate_id") var rateId: String? = null,
      @SerializedName("carrier_id") var carrierId: String? = null,
      @SerializedName("service_code") var serviceCode: String? = null,
      @SerializedName("service_type") var serviceType: String? = null,
      @SerializedName("carrier_code") var carrierCode: String? = null,
      @SerializedName("package_type") var packageType: String? = null,
      @SerializedName("shipping_amount") var shippingAmount: ShippingAmount? = ShippingAmount(),

      )

    data class ShippingAmount(

      @SerializedName("currency") var currency: String? = null,
      @SerializedName("original_amount") var originalAmount: Double? = null,
      @SerializedName("amount") var amount: String? = null,

      )
}