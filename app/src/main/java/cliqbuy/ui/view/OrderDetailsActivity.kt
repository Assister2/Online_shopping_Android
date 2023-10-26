package cliqbuy.ui.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityOrderDetailsBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.model.OrderDetailItemModel
import cliqbuy.model.OrderDetailsModel
import cliqbuy.ui.adapter.OrderDetailItemAdapter
import cliqbuy.ui.adapter.PurchaseHistoryAdapter
import cliqbuy.utils.inVisible
import cliqbuy.utils.visible
import com.squareup.picasso.Picasso

class OrderDetailsActivity : CommonActivity(), OrderDetailItemAdapter.onItemClick {

    lateinit var binding: ActivityOrderDetailsBinding
    var id = 0
    var orderDeatilsDataModel = OrderDetailsModel.Data()
    var orderItemDataModel = OrderDetailItemModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        binding.header.ivBack.visibility = View.VISIBLE
        binding.header.ivBack.setSafeOnClickListener { onBackPressed() }
        binding.header.tvTitle.text = resources.getString(R.string.order_details)
        if (intent.hasExtra("id"))
            id = intent.getIntExtra("id", 0)
        initApiResponseHandler()
        callOrderDetailsApi()
        callOrderItemApi()


        binding.tvTracking.setSafeOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.apply {
                this.putExtra(CommonKeys.REDIRECT_URL, orderDeatilsDataModel.labelDownload)
                this.putExtra("isFromTrack", true)
            }
            startActivity(intent)
        }

    }

    private fun callOrderDetailsApi() {
        commonMethods.showProgressDialog(this)
        var productHashMap: HashMap<String, String> = HashMap()
        productHashMap["id"] = id.toString()
        commonViewModel!!.apiRequest(Enums.REQ_ORDER_DETAILS, productHashMap, this)
    }

    private fun callOrderItemApi() {
        var productHashMap: HashMap<String, String> = HashMap()
        productHashMap["id"] = id.toString()
        commonViewModel!!.apiRequest(Enums.REQ_ORDER_ITEM, productHashMap, this)
    }

    private fun initApiResponseHandler() {
        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_ORDER_DETAILS -> {
                        commonMethods.hideProgressDialog()
                        val orderDeatilsModel =
                            commonViewModel!!.liveDataResponse.value as OrderDetailsModel
                        commonMethods.hideProgressDialog()
                        if (orderDeatilsModel.success!!) {
                            orderDeatilsDataModel = orderDeatilsModel.data[0]
                            onSuccessOrderDetail()
                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }
                    }

                    Enums.REQ_ORDER_ITEM -> {
                        orderItemDataModel =
                            commonViewModel!!.liveDataResponse.value as OrderDetailItemModel
                        commonMethods.hideProgressDialog()
                        if (orderItemDataModel.success!!) {
                            initOrderItemRecyclerview()
                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }
                    }

                }
            } else {
                commonMethods.hideProgressDialog()
            }

        })
    }

    private fun initOrderItemRecyclerview() {
        binding.rvItemList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        val orderDetailItemAdapter =
            OrderDetailItemAdapter(this, orderItemDataModel, this)
        binding.rvItemList.adapter = orderDetailItemAdapter
    }

    private fun onSuccessOrderDetail() {
        binding.tvOrderCode.text = orderDeatilsDataModel.code
        binding.tvShippingMethod.text =
            if (orderDeatilsDataModel.shippingType!!.isEmpty() || orderDeatilsDataModel.shippingType == "home_delivery")
                resources.getString(R.string.home_delivery)
            else if (orderDeatilsDataModel.shippingType == "ship_engine") {
                resources.getString(R.string.shipengine)
            } else
                resources.getString(R.string.pickup_point)

        binding.tvOrderDate.text = orderDeatilsDataModel.date
        binding.tvPaymentMethod.text = orderDeatilsDataModel.paymentType
        binding.tvPaymentStatus.text = orderDeatilsDataModel.paymentStatusString



        if (orderDeatilsDataModel.trackingNumebr.isNullOrEmpty()) {
            binding.tvTracking.visibility = View.GONE
            binding.trackTitle.visibility = View.GONE
        } else {
            binding.tvTracking.visibility = View.VISIBLE
            binding.trackTitle.visibility = View.VISIBLE
            binding.tvTracking.text = orderDeatilsDataModel.trackingNumebr
        }
        if (orderDeatilsDataModel.paymentStatus == "unpaid")
            Picasso.get().load(R.drawable.ic_unpaid).error(R.drawable.ic_unpaid)
                .placeholder(R.drawable.ic_unpaid).into(binding.ivPaid)
        else
            Picasso.get().load(R.drawable.ic_tick).error(R.drawable.ic_tick)
                .placeholder(R.drawable.ic_tick).into(binding.ivPaid)

        binding.tvDeliveryStatus.text = orderDeatilsDataModel.deliveryStatusString
        binding.tvTotalAmt.text = orderDeatilsDataModel.grandTotal

        binding.tvSubtotal.text = orderDeatilsDataModel.subtotal
        binding.tvTax.text = orderDeatilsDataModel.tax
        binding.tvShoppingCost.text = orderDeatilsDataModel.shippingCost
        binding.tvDiscount.text = orderDeatilsDataModel.couponDiscount
        binding.tvGrandTotal.text = orderDeatilsDataModel.grandTotal

        if (orderDeatilsDataModel.shippingType!!.isEmpty() || orderDeatilsDataModel.shippingType == "home_delivery"
            || orderDeatilsDataModel.shippingType == "ship_engine"
        ) {
            binding.tvName.text = orderDeatilsDataModel.shippingAddress!!.name
            binding.tvEmail.text = orderDeatilsDataModel.shippingAddress!!.email
            binding.tvAddress.text = orderDeatilsDataModel.shippingAddress!!.address
            binding.tvCountry.text = orderDeatilsDataModel.shippingAddress!!.country
            binding.tvCity.text = orderDeatilsDataModel.shippingAddress!!.city
            binding.tvPostalCode.text = orderDeatilsDataModel.shippingAddress!!.postalCode
            binding.tvPhone.text = orderDeatilsDataModel.shippingAddress!!.phone
            binding.lltEmail.visibility = View.VISIBLE
            binding.lltCountry.visibility = View.VISIBLE
            binding.lltCity.visibility = View.VISIBLE
            binding.lltPostalCode.visibility = View.VISIBLE
        } else {
            binding.tvName.text = orderDeatilsDataModel.pickupPointName
            binding.tvAddress.text = orderDeatilsDataModel.pickupPointAddress
            binding.tvPhone.text = orderDeatilsDataModel.pickupPointPhone
            binding.lltEmail.visibility = View.GONE
            binding.lltCountry.visibility = View.GONE
            binding.lltCity.visibility = View.GONE
            binding.lltPostalCode.visibility = View.GONE
        }
    }

    override fun onItemClicked(pos: Int, deliveryStatus: String) {
        val dialogBuilder = AlertDialog.Builder(this, R.style.OrderDialogStyle)
        val inflater = this.layoutInflater
        val dialog = inflater.inflate(R.layout.order_status_dialog, null)
        dialogBuilder.setView(dialog)
        dialogBuilder.setCancelable(true)
        val alertDialog = dialogBuilder.create()
        val view1 = dialog.findViewById(R.id.view_1) as View
        val view2 = dialog.findViewById(R.id.view_2) as View
        val view3 = dialog.findViewById(R.id.view_3) as View
        val view4 = dialog.findViewById(R.id.view_4) as View
        val view5 = dialog.findViewById(R.id.view_5) as View
        val view6 = dialog.findViewById(R.id.view_6) as View
        val ivConfirmed = dialog.findViewById(R.id.iv_tick_confirmed) as ImageView
        val ivOnTheWay = dialog.findViewById(R.id.iv_tick_on_the_way) as ImageView
        val ivDelivered = dialog.findViewById(R.id.iv_tick_deliverd) as ImageView

        if (deliveryStatus == CommonKeys.PENDING) {
            view1.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view2.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view3.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view4.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view5.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view6.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))

        } else if (deliveryStatus == CommonKeys.CONFIRMED) {
            view1.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view2.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view3.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view4.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view5.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view6.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            ivConfirmed.setImageResource(R.drawable.ic_tick)
        } else if (deliveryStatus == CommonKeys.ONTHEWAY) {
            view1.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view2.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view3.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view4.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view5.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            view6.background.setTint(commonMethods.dynamicTextColor(this, R.attr.bgFour))
            ivConfirmed.setImageResource(R.drawable.ic_tick)
            ivOnTheWay.setImageResource(R.drawable.ic_tick)
        } else if (deliveryStatus == CommonKeys.DELIVERED) {
            view1.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view2.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view3.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view4.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view5.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            view6.background.setTint(commonMethods.dynamicTextColor(this, R.attr.thumbsUp))
            ivConfirmed.setImageResource(R.drawable.ic_tick)
            ivOnTheWay.setImageResource(R.drawable.ic_tick)
            ivDelivered.setImageResource(R.drawable.ic_tick)
        }

        alertDialog.show()
    }
}