package cliqbuy.ui.fragments

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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonFragment
import cliqbuy.databinding.FragmentCartBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.model.CartModel
import cliqbuy.model.CommonDataModel
import cliqbuy.model.CommonHomeModel
import cliqbuy.ui.adapter.AddressListAdapter
import cliqbuy.ui.adapter.CartAdapter
import cliqbuy.ui.adapter.CartItemAdapter
import cliqbuy.ui.view.HomeActivity
import cliqbuy.ui.view.ProceedToShippingActivity
import cliqbuy.ui.view.SigninSignupActivity
import java.util.Locale


class CartFragment : CommonFragment(), CartAdapter.totalAmountChangeListerner,
    CartAdapter.onDeleteItemListerner {
    private lateinit var binding: FragmentCartBinding
    private lateinit var cartModel: ArrayList<CartModel>
    private lateinit var cartAdapter: CartAdapter
    var cartIds = ""
    var cartQuantities = ""
    var isProceed = false
    var isPickup = false

    var isNeedCart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCartBinding.inflate(layoutInflater)

        binding.swipeToRefresh.setOnRefreshListener {
            if (!sessionManager.accessToken.isNullOrEmpty())
                callCartApi()
            binding.swipeToRefresh.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()
        callCartApi()
    }

    private fun changeView() = if (!sessionManager.accessToken.isNullOrEmpty()) {
        binding.rltBeforeSignIn.visibility = View.GONE
        binding.rltEmptyCart.visibility = View.VISIBLE
        //  callCartApi()
    } else {

        binding.rltEmptyCart.visibility = View.GONE
        binding.lltBottom.visibility = View.GONE
        binding.rltBeforeSignIn.visibility = View.VISIBLE
        commonMethods.rotateArrow(binding.ivBeforeLogin, requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
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
        binding.btnSignIn.setSafeOnClickListener { callSignInSignUp() }
        return binding.root
    }


    private fun callCartApi() {
        commonMethods.showProgressDialog(requireActivity())
        var cartHashMap: HashMap<String, String> = HashMap()
        cartHashMap["id"] = sessionManager.userId.toString()
        commonViewModel!!.apiRequest(Enums.REQ_CART, cartHashMap, requireActivity())
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
        commonViewModel!!.apiRequest(Enums.REQ_UPDATE_CART, cartHashMap, requireActivity())
    }

    private fun initApiResponseHandler() {
        commonViewModel!!.liveDataResponse.observe(requireActivity(), Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {
                commonMethods.hideProgressDialog()
                when (commonViewModel!!.responseCode) {
                    Enums.REQ_CART -> {
                        commonMethods.hideProgressDialog()
                        cartModel =
                            commonViewModel?.liveDataResponse?.value as (ArrayList<CartModel>)



                        isNeedCart =
                            cartModel.filter { it.cartItems.filter { it.shipengineFound }.size > 0 }.size > 0
                        Log.d("isNeedcart", isNeedCart.toString())
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
                            commonMethods.showToast(requireActivity(), commonDataModel.message)
                        } else {
                            commonMethods.showToast(requireActivity(), commonDataModel.message)
                        }

                    }

                    Enums.REQ_UPDATE_CART -> {
                        val commonDataModel =
                            commonViewModel?.liveDataResponse?.value as (CommonDataModel)

                        if (commonDataModel.result) {
                            if (isProceed)
                                (activity as HomeActivity).callProceedToShipping(
                                    cartModel[0].ownerId!!,
                                    isPickup,
                                    isNeedCart
                                )
                            else {
                                callCartApi()
                                commonMethods.showToast(requireActivity(), commonDataModel.message)
                            }

                        } else {
                            commonMethods.showToast(requireActivity(), commonDataModel.message)
                        }

                    }
                }
            } else {
                commonMethods.hideProgressDialog()
            }
        })
    }


    private fun initCartRecyclerView() {
        binding.rvCart.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )

        cartAdapter =
            CartAdapter(requireActivity(), cartModel, this, this)
        binding.rvCart.adapter = cartAdapter
        cartAdapter.notifyDataSetChanged()
    }

    private fun callSignInSignUp() {
        var intent = Intent(requireContext(), SigninSignupActivity::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.cb_fade_in,
            R.anim.cb_face_out
        ).toBundle()
        startActivity(intent, animation)
    }

    companion object {
        fun newInstance(): CartFragment {
            return CartFragment()
        }
    }

    override fun onTotalAmountChanged(pos: Int, amount: Float, currencySymbol: String) {
        val totalString = String.format(Locale.ENGLISH, "%.2f", amount)
        binding.tvTotalAmt.text = totalString
        binding.tvCurrencySymbol.text = currencySymbol
    }

    override fun onDeleteClick(pos: Int, id: Int) {
        val dialogBuilder = AlertDialog.Builder(requireActivity(), R.style.DialogStyle)
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
        commonViewModel!!.apiRequest(Enums.REQ_DELETE_CART, cartHashMap, requireActivity())
    }

}