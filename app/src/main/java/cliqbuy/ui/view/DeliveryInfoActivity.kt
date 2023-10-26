package cliqbuy.ui.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.common.CommonFragment
import cliqbuy.databinding.ActivityDeliveryInfoBinding
import cliqbuy.helper.Enums
import cliqbuy.model.CartModel
import cliqbuy.databinding.FragmentCartBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.model.CommonDataModel
import cliqbuy.model.UpdateQuantityModel
import cliqbuy.ui.adapter.CartAdapter
import java.util.Locale

class DeliveryInfoActivity : CommonActivity(), CartAdapter.totalAmountChangeListerner,
    CartAdapter.onDeleteItemListerner {
    private lateinit var binding: ActivityDeliveryInfoBinding
    private lateinit var cartModel: ArrayList<CartModel>
    private lateinit var cartAdapter: CartAdapter
    var cartIds = ""
    var cartQuantities = ""
    var isProceed = false
    var isPickup = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

       /* binding.swipeToRefresh.isEnabled = false
        binding.swipeToRefresh.setOnRefreshListener {
            if (!sessionManager.accessToken.isNullOrEmpty())
                callCartApi()
            binding.swipeToRefresh.isRefreshing = false
        }
*/
         binding.commonHeader.ivBack.visibility  = View.VISIBLE
        binding.commonHeader.ivBack.setSafeOnClickListener {
            onBackPressed()
        }
         binding.commonHeader.tvTitle.text = getString(R.string.delviery_info)

        initViewModel()
        initApiResponseHandler()
        changeView()
        binding.tvUpdateCart.setSafeOnClickListener {
            isProceed = false
            callUpdateCartApi()
        }
        binding.tvProceed.setSafeOnClickListener {
            isProceed = true
            callUpdateCartApi()
        }
    }

    private fun changeView() = if (!sessionManager.accessToken.isNullOrEmpty()) {
        binding.rltBeforeSignIn.visibility = View.GONE
        binding.rltEmptyCart.visibility = View.VISIBLE
        callCartApi()
    } else {

        binding.rltEmptyCart.visibility = View.GONE
        binding.lltBottom.visibility = View.GONE
        binding.rltBeforeSignIn.visibility = View.VISIBLE
        commonMethods.rotateArrow(binding.ivBeforeLogin, this@DeliveryInfoActivity)

    }

    private fun callCartApi() {
        commonMethods.showProgressDialog(this@DeliveryInfoActivity)
        var cartHashMap: HashMap<String, String> = HashMap()
        cartHashMap["id"] = sessionManager.userId.toString()
        commonViewModel!!.apiRequest(
            Enums.REQ_DELIVERY_INFO,
            cartHashMap,
            this@DeliveryInfoActivity
        )
    }

    fun gobacktocart() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType, 3) }
        intent.also { it.putExtra("redirect", 0) }
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
        callCartApi()
    }

    private fun callUpdateCartApi() {
        cartIds = ""
        cartQuantities = ""

        for (_cartmodel in cartModel) {
            for (cartItems in _cartmodel.cartItems) {
                cartIds += cartItems.id!!.toString() + ","
                cartQuantities += cartItems.quantity!!.toString() + ","
            }
        }
        cartIds = cartIds.substring(0, cartIds.length - 1)
        cartQuantities = cartQuantities.substring(0, cartQuantities.length - 1)

        var cartHashMap: HashMap<String, String> = HashMap()
        cartHashMap["cart_ids"] = cartIds
        cartHashMap["user_id"] = sessionManager.userId.toString()
        cartHashMap["cart_quantities"] = cartQuantities
        commonViewModel!!.apiRequest(Enums.REQ_UPDATE_CART, cartHashMap, this@DeliveryInfoActivity)
    }

    private fun initApiResponseHandler() {
        commonViewModel!!.liveDataResponse.observe(this@DeliveryInfoActivity, Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {
                commonMethods.hideProgressDialog()
                when (commonViewModel!!.responseCode) {
                    Enums.REQ_DELIVERY_INFO -> {
                        commonMethods.hideProgressDialog()
                        cartModel =
                            commonViewModel?.liveDataResponse?.value as (ArrayList<CartModel>)


                        if (cartModel.size > 0) {
                            isPickup = cartModel.size <= 1
                            binding.rltShowCart.visibility = View.VISIBLE
                            binding.rltBeforeSignIn.visibility = View.GONE
                            binding.rltEmptyCart.visibility = View.GONE
                            binding.lltBottom.visibility = View.VISIBLE
                            initCartRecyclerView()
                        } else {
                            binding.rltEmptyCart.visibility = View.VISIBLE
                            binding.lltBottom.visibility = View.GONE
                            binding.rltShowCart.visibility = View.GONE
                            binding.rltBeforeSignIn.visibility = View.GONE
                        }
                    }

                    Enums.REQ_DELETE_CART -> {
                        val commonDataModel =
                            commonViewModel?.liveDataResponse?.value as (CommonDataModel)

                        if (commonDataModel.result) {
                            callCartApi()
                            commonMethods.showToast(
                                this@DeliveryInfoActivity,
                                commonDataModel.message
                            )
                        } else {
                            commonMethods.showToast(
                                this@DeliveryInfoActivity,
                                commonDataModel.message
                            )
                        }
                    }

                    Enums.REQ_UPDATE_CART -> {
                        val commonDataModel =
                            commonViewModel?.liveDataResponse?.value as (CommonDataModel)

                        if (commonDataModel.result) {
                            if (isProceed)
                                callCheckOutPage()
                            else {
                                callCartApi()
                                commonMethods.showToast(
                                    this@DeliveryInfoActivity,
                                    commonDataModel.message
                                )
                            }

                        } else {
                            commonMethods.showToast(
                                this@DeliveryInfoActivity,
                                commonDataModel.message
                            )
                        }

                    }

                    Enums.REQ_UPDATE_QUANTITY -> {
                        commonMethods.hideProgressDialog()
                        var model =
                            commonViewModel?.liveDataResponse?.value as UpdateQuantityModel

                        if (model.status) {
                            callCartApi()
                        } else {
                            commonMethods.showToast(
                                this@DeliveryInfoActivity,
                                model.message.toString()
                            )
                        }
                    }

                    Enums.REQ_UPDATE_SHIPENGINE -> {
                        commonMethods.hideProgressDialog()
                        var model =
                            commonViewModel?.liveDataResponse?.value as UpdateQuantityModel

                        if (model.status) {
                            callCartApi()
                        } else {
                            commonMethods.showToast(
                                this@DeliveryInfoActivity,
                                model.message.toString()
                            )
                        }
                    }

                }
            } else {
                commonMethods.hideProgressDialog()
            }
        })
    }

    private fun callCheckOutPage() {
        // if (addressListModel.data!!.size > 0) {
        val intent = Intent(this, CheckoutActivity::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
        /* } else {
             commonMethods.showToast(this, resources.getString(R.string.please_add_address))
         }*/
    }

    private fun initCartRecyclerView() {
        var layoutManager = LinearLayoutManager(
            this@DeliveryInfoActivity,
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvCart.layoutManager = layoutManager

        cartModel.forEach {
            it.cartItems.forEach {
                for (i in 0..((it.shipengineData?.estimateData?.size ?: 1) - 1)) {
                    if (it.serviceCode?.equals(it.shipengineData?.estimateData?.get(i)?.serviceCode) ?: false &&
                        it.packageType?.equals(it.shipengineData?.estimateData?.get(i)?.packageType) ?: false
                    ) {
                        it.selectedPos = i
                        break
                    }

                }
            }
        }

        cartAdapter =
            CartAdapter(
                this@DeliveryInfoActivity,
                cartModel,
                this,
                this,
                ::updateQunatity,
                ::udpateShipEngine,
                true
            )
        binding.rvCart.adapter = cartAdapter
        cartAdapter.notifyDataSetChanged()
    }

    private fun callSignInSignUp() {
        var intent = Intent(this@DeliveryInfoActivity, SigninSignupActivity::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            this@DeliveryInfoActivity,
            R.anim.cb_fade_in,
            R.anim.cb_face_out
        ).toBundle()
        startActivity(intent, animation)
    }


    override fun onTotalAmountChanged(pos: Int, amount: Float, currencySymbol: String) {
        val totalString = String.format(Locale.ENGLISH, "%.2f", amount)
        binding.tvTotalAmt.text = totalString
        binding.tvCurrencySymbol.text = currencySymbol
    }

    override fun onDeleteClick(pos: Int, id: Int) {
        val dialogBuilder = AlertDialog.Builder(this@DeliveryInfoActivity, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_common, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        val tvMessage = dialogView.findViewById<View>(R.id.tv_message) as TextView
        val btnOk = dialogView.findViewById<View>(R.id.btn_ok) as Button
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel) as Button

        tvMessage.text = resources.getString(R.string.remove_cart)


        btnOk.setSafeOnClickListener {
            callDeleteCartApi(id)
            alertDialog.dismiss()
        }
        btnCancel.setSafeOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    private fun callDeleteCartApi(id: Int) {
        var cartHashMap: HashMap<String, String> = HashMap()
        cartHashMap["id"] = id.toString()
        commonViewModel!!.apiRequest(Enums.REQ_DELETE_CART, cartHashMap, this@DeliveryInfoActivity)
    }


    fun updateQunatity(id: Int, quantity: Int) {
        commonMethods.showProgressDialog(this@DeliveryInfoActivity)
        commonViewModel?.apiRequest(Enums.REQ_UPDATE_QUANTITY, hashMapOf<String, String>().apply {
            put("id", id.toString())
            put("user_id", sessionManager.userId.toString())
            put("quantity", quantity.toString())
        }, this)
    }


    fun udpateShipEngine(id: Int, rateId: String) {
        commonMethods.showProgressDialog(this@DeliveryInfoActivity)
        commonViewModel?.apiRequest(Enums.REQ_UPDATE_SHIPENGINE, hashMapOf<String, String>().apply {
            put("rate_id", rateId)
            put("id", id.toString())
        }, this)
    }

}