package cliqbuy.ui.view

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityCheckoutBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.CommonKeys.COD
import cliqbuy.helper.CommonKeys.PAYMENT_STRIPE
import cliqbuy.helper.CommonKeys.PAYPAL
import cliqbuy.helper.CommonKeys.REDIRECT_URL
import cliqbuy.helper.CommonKeys.STRIPE
import cliqbuy.helper.CommonKeys.defaultId
import cliqbuy.helper.CommonKeys.defaultPaymentType
import cliqbuy.helper.Constants.PAYMENT_CODE
import cliqbuy.helper.Enums
import cliqbuy.helper.Enums.REQ_APPLY_COUPON
import cliqbuy.helper.Enums.REQ_CART_INVOICE
import cliqbuy.helper.Enums.REQ_ORDER_BY_CASH
import cliqbuy.helper.Enums.REQ_ORDER_BY_PAYPAL
import cliqbuy.helper.Enums.REQ_PAYMENT_TYPES
import cliqbuy.helper.Enums.REQ_REMOVE_COUPON
import cliqbuy.model.CartInvoiceModel
import cliqbuy.model.GenericModel
import cliqbuy.model.PaymentMethodsModel
import cliqbuy.ui.adapter.PaymentListAdapter
import cliqbuy.utils.showToast
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.indices
import kotlin.collections.set

class CheckoutActivity : CommonActivity(), PaymentListAdapter.OnClick {

    lateinit var binding: ActivityCheckoutBinding
    lateinit var checkoutModel: CartInvoiceModel
    private var paymentMethodsModel = ArrayList<PaymentMethodsModel>()
    private var couponModel = GenericModel()
    private var coupon = ""
    private var paymentType = ""
    private var webUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.ivBack.visibility = View.VISIBLE
        binding.header.tvTitle.visibility = View.VISIBLE
        binding.header.tvTitle.text = getString(R.string.checkout)

        binding.tvSeeDetails.setPaintFlags(binding.tvSeeDetails.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

        binding.header.ivBack.setSafeOnClickListener { onBackPressed() }

        binding.btnPlaceorder.setSafeOnClickListener { placeOrder() }

        binding.tvSeeDetails.setSafeOnClickListener { showInvoicePopup() }

        binding.btnApplycoupon.setSafeOnClickListener { validateInputCoupon() }

        binding.swipeToRefresh.setOnRefreshListener { getInvoiceAndPaymentMethods() }

        getInvoiceAndPaymentMethods()
        initViewModel()
        initApiResponseHandler()

    }

    private fun placeOrder() {
        if (paymentType != "") when (paymentType) {

            COD -> orderByCash()

            PAYPAL -> orderByPaypal()

            STRIPE -> orderByStripe()
        } else this.showToast(resources.getString(R.string.select_payment))
    }

    private fun orderByStripe() {
        val stripeUrl = resources.getString(R.string.apiBaseUrl) + PAYMENT_STRIPE
        callWebView(stripeUrl)

    }

    private fun orderByPaypal() {
        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_ORDER_BY_PAYPAL, paymentHashMap(), this)
    }

    private fun orderByCash() {

        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_ORDER_BY_CASH, paymentHashMap(), this)
    }

    private fun validateInputCoupon() {

        if (!checkoutModel.couponApplied) {
            coupon = binding.edtCouponCode.text.trim().toString()
            if (coupon.isNotEmpty()) applyCoupon()
            else this.showToast(resources.getString(R.string.please_enter_coupon))
        } else {
            removeCoupon()
        }


    }

    private fun removeCoupon() {
        commonViewModel!!.apiRequest(REQ_REMOVE_COUPON, couponHashMap(), this)
    }

    private fun couponHashMap(): HashMap<String, String> {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["user_id"] = sessionManager.userId!!
        hashMap["owner_id"] = defaultId
        hashMap["coupon_code"] = coupon
        return hashMap
    }

    private fun paymentHashMap(): HashMap<String, String> {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["user_id"] = sessionManager.userId!!
        hashMap["owner_id"] = defaultId
        if (paymentType != COD) hashMap["payment_type"] = defaultPaymentType!!
        else {
            hashMap["payment_type"] = paymentType!!
            hashMap["amount"] = checkoutModel.grandTotal!!
        }
        return hashMap
    }

    private fun applyCoupon() {
        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_APPLY_COUPON, couponHashMap(), this)
    }

    private fun getInvoiceAndPaymentMethods() {
        getPaymentMethods()
        if (sessionManager.accessToken != "") getInvoice()
        binding.swipeToRefresh.isRefreshing = false
    }

    private fun getPaymentMethods() {
        val paymentParams: HashMap<String, String> = HashMap()
        paymentParams["user_id"] = sessionManager.userId.toString()

        commonViewModel!!.apiRequest(REQ_PAYMENT_TYPES, paymentParams, this)
    }

    override fun onResume() {
        super.onResume()

    }

    private fun showInvoicePopup() {

        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_invoice, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)


        val close = dialogView.findViewById<View>(R.id.iv_close) as ImageView
        val subTotal = dialogView.findViewById<View>(R.id.tv_value_subtotal) as TextView
        val tax = dialogView.findViewById<View>(R.id.tv_value_tax) as TextView
        val shippingCost = dialogView.findViewById<View>(R.id.tv_value_shippingcost) as TextView
        val discount = dialogView.findViewById<View>(R.id.tv_value_discount) as TextView
        val grandTotal = dialogView.findViewById<View>(R.id.tv_value_grant_total) as TextView

        subTotal.text = checkoutModel.subTotal
        tax.text = checkoutModel.tax
        shippingCost.text = checkoutModel.shippingCost
        discount.text = checkoutModel.discount
        grandTotal.text = checkoutModel.grandTotal

        close.setSafeOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }


    private fun getInvoice() {

        val checkOutParams: HashMap<String, String> = HashMap()
        checkOutParams["id"] = sessionManager.userId!!
        checkOutParams["default_id"] = defaultId

        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_CART_INVOICE, checkOutParams, this)
    }

    private fun initApiResponseHandler() {

        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                try {
                    when (commonViewModel!!.responseCode) {

                        REQ_CART_INVOICE -> {

                            commonMethods.hideProgressDialog()
                            checkoutModel =
                                commonViewModel!!.liveDataResponse.value as CartInvoiceModel
                            binding.tvValueTotalprice.text = checkoutModel.grandTotal
                            binding.coupon.isVisible = checkoutModel.isCouponEnabled

                            if (checkoutModel.couponApplied) {
                                binding.edtCouponCode.setText(checkoutModel.couponCode!!)
                                binding.btnApplycoupon.setTextColor(
                                    commonMethods.dynamicTextColor(
                                        this,
                                        R.attr.txtSecondary
                                    )
                                )
                                binding.btnApplycoupon.text = resources.getString(R.string.remove)
                                binding.btnApplycoupon.isAllCaps = false
                                binding.btnApplycoupon.setBackgroundResource(R.drawable.bg_right_rounded_corner_red)
                            } else {
                                binding.edtCouponCode.setText("")
                                binding.btnApplycoupon.setTextColor(
                                    commonMethods.dynamicTextColor(
                                        this,
                                        R.attr.txtPrimary
                                    )
                                )
                                binding.btnApplycoupon.text =
                                    resources.getString(R.string.apply_coupon)
                                binding.btnApplycoupon.isAllCaps = true
                                binding.btnApplycoupon.setBackgroundResource(R.drawable.bg_right_rounded_corner)
                            }
                        }

                        REQ_PAYMENT_TYPES -> {
                            paymentMethodsModel =
                                commonViewModel!!.liveDataResponse.value as ArrayList<PaymentMethodsModel>
                            paymentMethodsModel[0].isSelected = true
                            paymentType = paymentMethodsModel[0].paymentTypeKey!!

                            initRecyclerView()
                        }

                        REQ_APPLY_COUPON -> {
                            commonMethods.hideProgressDialog()
                            couponModel = it as GenericModel
                            this.showToast(couponModel.message)
                            if (couponModel.result) getInvoice()
                        }

                        REQ_REMOVE_COUPON -> {
                            couponModel = it as GenericModel
                            this.showToast(couponModel.message)
                            if (couponModel.result) getInvoice()
                        }

                        REQ_ORDER_BY_CASH -> {
                            commonMethods.hideProgressDialog()
                            couponModel = it as GenericModel
                            if (couponModel.result) {
                                this.showToast(couponModel.message)
                                callPurchaseHistory()
                            } else {
                                this.showToast(couponModel.message)
                                if (couponModel.shipEngineError)
                                    gobacktocart()
                            }

                        }

                        REQ_ORDER_BY_PAYPAL -> {
                            commonMethods.hideProgressDialog()
                            couponModel = it as GenericModel
                            if (couponModel.result) {
                                webUrl = couponModel.url!!
                                callWebView(webUrl)
                            } else {
                                this.showToast(couponModel.message)
                                if (couponModel.shipEngineError)
                                    gobacktocart()
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            } else {
                commonMethods.hideProgressDialog()
                commonMethods.showToast(this, resources.getString(R.string.please_try_again))
            }


        })
    }


    private fun callWebView(webUrl: String) {
        openActivityForResult(webUrl)

    }

    fun gobacktocart() {

        callCartApi()
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType, 3) }
        intent.also { it.putExtra("redirect", 0) }
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun callCartApi() {
        commonMethods.showProgressDialog(this@CheckoutActivity)
        var cartHashMap: HashMap<String, String> = HashMap()
        cartHashMap["id"] = sessionManager.userId.toString()
        commonViewModel!!.apiRequest(
            Enums.REQ_DELIVERY_INFO,
            cartHashMap,
            this@CheckoutActivity
        )
    }

    private fun openActivityForResult(webUrl: String) {
        Log.d("paymentcall",paymentType +"  "+"openActivityForResult")
        val intent = Intent(this, WebViewActivity::class.java)
        intent.apply {
            this.putExtra(REDIRECT_URL, webUrl)
            this.putExtra("paymentMode", paymentType)
            if (paymentType == STRIPE) {
                this.putExtra("amount", checkoutModel.grandTotalValue.toString())
                this.putExtra("id", sessionManager.userId!!)
            }
        }
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                if (result.data != null) {
                    val jsonResponse = result.data!!.extras!!.getString("response")!!
                    val response = JSONObject(jsonResponse)
                    val result = response.getBoolean("result")
                    val message = response.getString("message")
                    Log.e("result", result.toString())
                    Log.e("message", message.toString())

                    if (result) {
                        commonMethods.showToast(this, message)
                        callPurchaseHistory()
                    } else {
                        commonMethods.showToast(this, message)
                        getInvoiceAndPaymentMethods()
                    }
                }

            }
        }
    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == PAYMENT_CODE) {
             if (data != null) {
                 val jsonResponse = data.extras!!.getString("response")!!
                 val response = JSONObject(jsonResponse)
                 val result = response.getBoolean("result")
                 val message = response.getString("message")
                 commonMethods.showToastLong(this,message)

                 if (result) callPurchaseHistory() else getInvoiceAndPaymentMethods()

             }
         }
     }*/

    private fun callPurchaseHistory() {

        val intent = Intent(this, PurchaseHistory::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right,
            R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun initRecyclerView() {
        binding.rvPayments.layoutManager = LinearLayoutManager(this)
        binding.rvPayments.adapter = PaymentListAdapter(this, paymentMethodsModel, this)
    }

    override fun onItemClick(position: Int) {

        for (i in paymentMethodsModel.indices) {
            paymentMethodsModel[i].isSelected = false
        }
        paymentMethodsModel[position].isSelected = true
        paymentType = paymentMethodsModel[position].paymentTypeKey!!
        binding.rvPayments.adapter!!.notifyDataSetChanged()
    }

}