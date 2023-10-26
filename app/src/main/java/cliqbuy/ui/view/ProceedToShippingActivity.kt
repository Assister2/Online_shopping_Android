package cliqbuy.ui.view

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityProceedToShippingBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.model.*
import cliqbuy.ui.adapter.*
import java.util.HashMap

class ProceedToShippingActivity : CommonActivity(), CityCountryAdapter.OnClickListener,
    PickupPointAdapter.PickupPointAdapterInterface,
    HomeAddressListAdapter.OnHomeAddressClickListener {

    lateinit var binding: ActivityProceedToShippingBinding

    lateinit var pickupPointModel: PickupPointModel
    lateinit var addressListModel: AddressListModel
    var shippingCostModel = ShippingCostModel()
    var cityListModel = CityListModel()
    var stateListModel = StateListModel()
    lateinit var countryListModel: CountryListModel


    lateinit var pickupPointHashMap: HashMap<String, String>
    lateinit var shippingCostHashMap: HashMap<String, String>
    lateinit var pickupPointAdapter: PickupPointAdapter
    lateinit var addressListAdapter: HomeAddressListAdapter

    var address: String = ""
    var country: String = ""
    var state: String = ""
    var city: String = ""
    var postalCode: String = ""
    var phone: String = ""
    var sellerId: String = ""
    var productId: String = ""
    var addressId: String = ""
    var staffId: String = ""
    var pickupPointId: String = ""
    var countryId: String = ""
    var stateId: String = ""
    var isPickup = false
    var onClickPickup = false
    var redirect = 0
    lateinit var tvDialogCity: TextView
    lateinit var tvDialogCountry: TextView
    lateinit var tvDialogState: TextView

    lateinit var cityCountryAdapter: CityCountryAdapter

    private lateinit var rvCityCountry: RecyclerView
    var cityCountryDialog: android.app.AlertDialog? = null

    var addressAlertDialog: AlertDialog? = null
    var confirmationAlertDialog: AlertDialog? = null
    var tvInvalideAddress: TextView? = null
    lateinit var genericModel: GenericModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProceedToShippingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.header.ivBack.visibility = View.VISIBLE

        if (isNeedDeliveryInfo()) {
            binding.header.tvTitle.text = resources.getString(R.string.shipping_address)
            binding.tvHomeSelect.setBackgroundResource(R.drawable.bg_unselected_tab)
            binding.tvHomeAddress.setTextColor(resources.getColor(R.color.txt_secondary))
            binding.tvPickupPoint.visibility = View.GONE
            binding.tvPickupSelect.visibility = View.GONE
            binding.rltHomeAddress.visibility = View.VISIBLE
            binding.rltPickupPoint.visibility = View.GONE
        } else {
            binding.header.tvTitle.text = resources.getString(R.string.shipping_cost)
        }

        binding.rltPickupPoint.visibility = View.GONE
        binding.rltHomeAddress.visibility = View.VISIBLE
        binding.tvGoToAddress.paintFlags =
            binding.tvGoToAddress.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvHomeAddress.setSafeOnClickListener { callHomeAddress() }
        binding.tvPickupPoint.setSafeOnClickListener { callPickUpPoint() }
        binding.tvGoToAddress.setSafeOnClickListener { goToAddressPage() }
        binding.btnAddAddress.setSafeOnClickListener { addAddress() }
        binding.rltProceed.setSafeOnClickListener {
            if (onClickPickup || addressListModel.data!!.size > 0)
                callProceedApi()
            else
                commonMethods.showToast(this, resources.getString(R.string.please_add_address))
        }
        binding.header.ivBack.setSafeOnClickListener { onBackPressed() }

        if (intent.hasExtra("seller_id")) {
            sellerId = intent.getStringExtra("seller_id")!!
            productId = intent.getStringExtra("id")!!
            isPickup = intent.getBooleanExtra("is_pickup", false)
            redirect = intent.getIntExtra("redirect", 0)
        }

        binding.swipeToRefresh.setOnRefreshListener {
            initAddressListApiCall()
            initPickupApiCall()
            initCountryApiCall()
            initStateApiCall()
            initCityApiCall()
            binding.swipeToRefresh.isRefreshing = false
        }

        initViewModel()
        initApiResponseHandler()
        //initAddressListApiCall()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (redirect == 1) {
            finish()
        } else {
            val intent = Intent(this, HomeActivity::class.java)
            intent.also { it.putExtra(CommonKeys.fragmentType, 3) }
            intent.also { it.putExtra("redirect", redirect) }
            intent.also { it.putExtra("id", productId) }
            val animation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.ub__slide_in_left, R.anim.ub__slide_out_right
            ).toBundle()
            startActivity(intent, animation)
        }
    }

    private fun goToAddressPage() {
        var intent = Intent(this, AddressDetailsActivity::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun initPickupApiCall() {
        pickupPointHashMap = HashMap()
        pickupPointHashMap["user_id"] = sessionManager.userId.toString()
        pickupPointHashMap["seller_id"] = sellerId
        commonViewModel!!.apiRequest(Enums.REQ_PICKUP_POINT, pickupPointHashMap, this)
    }

    private fun initAddressListApiCall() {
        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(Enums.REQ_ADDRESS_LIST, commonHashMap, this)
    }

    private fun initCountryApiCall() {
        commonViewModel!!.apiRequest(Enums.REQ_COUNTRY_LIST, commonHashMap, this)
    }

    private fun initStateApiCall() {
        commonMethods.showProgressDialog(this)
        var hashMap: HashMap<String, String> = HashMap()
        hashMap["country_id"] = countryId
        commonViewModel!!.apiRequest(Enums.REQ_STATE_LIST, hashMap, this)
    }

    private fun initCityApiCall() {
        commonMethods.showProgressDialog(this)
        var hashMap: HashMap<String, String> = HashMap()
        hashMap["state_id"] = stateId
        commonViewModel!!.apiRequest(Enums.REQ_CITY_LIST, hashMap, this)
    }

    private fun callProceedApi() {
        val commonHashMap: HashMap<String, String> = HashMap()
        commonHashMap["user_id"] = sessionManager.userId.toString()
        commonHashMap["address_id"] = addressId
        if (onClickPickup) {
            commonHashMap["staff_id"] = staffId
            commonHashMap["pickup_point_id"] = pickupPointId
        } else {
            commonHashMap["staff_id"] = "0"
            commonHashMap["pickup_point_id"] = "0"
        }

        commonViewModel!!.apiRequest(Enums.REQ_PROCEED_CHECKOUT, commonHashMap, this)
    }

    private fun initShippingCostApiCall(city: String) {
        commonMethods.showProgressDialog(this)
        shippingCostHashMap = HashMap()
        shippingCostHashMap["user_id"] = sessionManager.userId.toString()
        shippingCostHashMap["owner_id"] = sellerId
        shippingCostHashMap["city_name"] = city
        commonViewModel!!.apiRequest(Enums.REQ_SHIPPING_COST, shippingCostHashMap, this)
    }

    private fun callPickUpPoint() {
        onClickPickup = true
        binding.tvPickupSelect.setBackgroundResource(R.drawable.bg_selected_tab)
        binding.tvHomeSelect.setBackgroundResource(R.drawable.bg_unselected_tab)
        binding.tvPickupPoint.setTextColor(resources.getColor(R.color.txt_primary))
        binding.tvHomeAddress.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.rltPickupPoint.visibility = View.VISIBLE
        binding.rltHomeAddress.visibility = View.GONE
        binding.header.tvTitle.text =
            resources.getString(R.string.shipping_cost_title) + " " + sessionManager.currencySymbol + " " + "0.00"
    }

    private fun callHomeAddress() {
        onClickPickup = false
        binding.tvHomeSelect.setBackgroundResource(R.drawable.bg_selected_tab)
        binding.tvPickupSelect.setBackgroundResource(R.drawable.bg_unselected_tab)
        binding.tvHomeAddress.setTextColor(resources.getColor(R.color.txt_primary))
        binding.tvPickupPoint.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.rltPickupPoint.visibility = View.GONE
        binding.rltHomeAddress.visibility = View.VISIBLE
        if (addressListModel.data!!.size > 0)
            binding.header.tvTitle.text =
                resources.getString(R.string.shipping_cost_title) + " " + shippingCostModel.valueString
        else
            binding.header.tvTitle.text = resources.getString(R.string.shipping_cost)
    }

    private fun initHomeAddressRecyclerView() {
        binding.rvHomeAddress.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        addressListAdapter =
            HomeAddressListAdapter(this, addressListModel, this)
        binding.rvHomeAddress.adapter = addressListAdapter
    }

    private fun initPickupPointRecyclerview() {
        try {
            binding.rvPickupPoint.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL, false
            )
            pickupPointAdapter = PickupPointAdapter(this, pickupPointModel, this)
            binding.rvPickupPoint.adapter = pickupPointAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun addAddress() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_address_add, null)
        dialogBuilder.setView(dialogView)
        addressAlertDialog = dialogBuilder.create()
        addressAlertDialog?.setCancelable(true)
        val tvAddress = dialogView.findViewById<View>(R.id.edt_address) as EditText
        val tvPostalCode = dialogView.findViewById<View>(R.id.edt_postal_code) as EditText
        val tvPhone = dialogView.findViewById<View>(R.id.edt_phone) as EditText
        val btAdd = dialogView.findViewById<View>(R.id.btn_add) as Button
        val btClose = dialogView.findViewById<View>(R.id.btn_close) as Button
        tvDialogCity = dialogView.findViewById<View>(R.id.tv_city) as TextView
        tvDialogCountry = dialogView.findViewById<View>(R.id.tv_country) as TextView
        tvDialogState = dialogView.findViewById<View>(R.id.tv_state) as TextView
        val rltCity = dialogView.findViewById<View>(R.id.rlt_city) as RelativeLayout
        val rltCountry = dialogView.findViewById<View>(R.id.rlt_country) as RelativeLayout
        val rltState = dialogView.findViewById<View>(R.id.rlt_state) as RelativeLayout
        tvInvalideAddress = dialogView.findViewById<View>(R.id.tv_invalid_address) as TextView

        btAdd.text = resources.getString(R.string.add)


        rltCity.setSafeOnClickListener {
            if (tvDialogCountry.text.isEmpty()) {
                commonMethods.showToast(this, resources.getString(R.string.enter_country))
            } else if (tvDialogState.text.isEmpty()) {
                commonMethods.showToast(this, resources.getString(R.string.enter_state))
            } else {
                if (cityListModel.data!!.size > 0) {
                    cityCountryPopup(0)
                } else {
                    commonMethods.showToast(
                        this,
                        resources.getString(R.string.selected_state_has_no_city)
                    )
                }
            }
        }
        rltCountry.setSafeOnClickListener {
            tvDialogState.hint = resources.getString(R.string.select_state)
            tvDialogState.text = ""
            tvDialogCity.hint = resources.getString(R.string.select_city)
            tvDialogCity.text = ""
            cityCountryPopup(1)
        }
        rltState.setSafeOnClickListener {
            tvDialogCity.hint = resources.getString(R.string.select_city)
            tvDialogCity.text = ""
            if (tvDialogCountry.text.isEmpty()) {
                commonMethods.showToast(this, resources.getString(R.string.enter_country))
            } else {
                if (stateListModel.data!!.size > 0) {
                    cityCountryPopup(2)
                } else {
                    commonMethods.showToast(
                        this,
                        resources.getString(R.string.selected_country_has_no_state)
                    )
                }
            }
        }


        btClose.setSafeOnClickListener { addressAlertDialog?.dismiss() }
        btAdd.setSafeOnClickListener {
            address = tvAddress.text.trim().toString()
            city = tvDialogCity.text.trim().toString()
            country = tvDialogCountry.text.trim().toString()
            state = tvDialogState.text.trim().toString()
            phone = tvPhone.text.trim().toString()
            postalCode = tvPostalCode.text.trim().toString()
            if (validateCheck()) {
                initAddressAddApiCall("1")
                //addressAlertDialog?.dismiss()
            }
        }

        addressAlertDialog?.show()
    }

    fun showAddressConfirmationPopup() {
        tvInvalideAddress = null
        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_confirm_address, null)
        dialogBuilder.setView(dialogView)
        confirmationAlertDialog = dialogBuilder.create()
        confirmationAlertDialog!!.setCancelable(true)

        val btAdd = dialogView.findViewById<View>(R.id.btn_add) as Button
        val btClose = dialogView.findViewById<View>(R.id.btn_close) as Button
        val tvSuggestAddress = dialogView.findViewById<TextView>(R.id.tv_sugggest_address)
        val tvOriginalAddress = dialogView.findViewById<TextView>(R.id.tv_original_address)
        val rbOriginal = dialogView.findViewById<RadioButton>(R.id.radiobtn_orginal)
        val rbSuggest = dialogView.findViewById<RadioButton>(R.id.radiobtn_suggest)

        tvSuggestAddress.setOnClickListener {
            rbSuggest.isChecked = true
            rbOriginal.isChecked = false
        }

        tvOriginalAddress.setOnClickListener {
            rbOriginal.isChecked = true
            rbSuggest.isChecked = false

        }

        /*rbOriginal.setOnCheckedChangeListener { buttonView, isChecked -> rbOriginal.isChecked =  true
            rbSuggest.isChecked = false

        }

        rbSuggest.setOnCheckedChangeListener { buttonView, isChecked ->rbOriginal.isChecked =  false
            rbSuggest.isChecked = true  }*/

        rbSuggest.setOnClickListener {
            rbOriginal.isChecked = false
            rbSuggest.isChecked = true

        }
        rbOriginal.setOnClickListener {
            rbOriginal.isChecked = true
            rbSuggest.isChecked = false
        }

        tvSuggestAddress.text = genericModel.matchedAddress.addressLine1 + " , " +
                genericModel.matchedAddress.cityLocality + " , " +
                genericModel.matchedAddress.stateProvince + " , " +
                genericModel.matchedAddress.postalCode + " , " +
                genericModel.matchedAddress.countryCode

        tvOriginalAddress.text = genericModel.originalAddress.addressLine1 + " , " +
                genericModel.originalAddress.cityLocality + " , " +
                genericModel.originalAddress.stateProvince + " , " +
                genericModel.originalAddress.postalCode + " , " +
                genericModel.originalAddress.countryCode


        btClose.setSafeOnClickListener { confirmationAlertDialog?.dismiss() }
        btAdd.setSafeOnClickListener {
            val addressModel =
                if (rbOriginal.isChecked) genericModel.originalAddress else genericModel.matchedAddress
            Log.d("addressmodel", addressModel.toString())
            address = addressModel.addressLine1.toString()
            city = addressModel.cityLocality.toString()
            country = addressModel.countryCode.toString()
            state = addressModel.stateProvince.toString()
            phone = addressModel.phone.toString()
            postalCode = addressModel.postalCode.toString()
            if (validateCheck()) {
                initAddressAddApiCall("0")
                // confirmationAlertDialog?.dismiss()
            }
        }

        confirmationAlertDialog?.show()
    }

    fun cityCountryPopup(type: Int) {
        rvCityCountry = RecyclerView(this)
        rvCityCountry.overScrollMode = View.OVER_SCROLL_NEVER
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.header, null)
        val header = view.findViewById<TextView>(R.id.header)

        initCityCountryRv(type)

        if (type == 0) {
            header.text = getString(R.string.select_city)
        } else if (type == 1) {
            header.text = getString(R.string.select_country)
        } else if (type == 2) {
            header.text = getString(R.string.select_state)
        }




        cityCountryDialog = android.app.AlertDialog.Builder(this, R.style.customDialog)
            .setCustomTitle(view)
            .setView(rvCityCountry)
            .setCancelable(true)
            .show()


    }

    private fun initCityCountryRv(type: Int) {

        cityCountryAdapter =
            CityCountryAdapter(this, countryListModel, cityListModel, stateListModel, type, this)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCityCountry.layoutManager = linearLayoutManager
        rvCityCountry.adapter = cityCountryAdapter
    }

    private fun validateCheck(): Boolean {
        if (address.isEmpty()) {
            commonMethods.showToast(this, resources.getString(R.string.enter_address))
            return false
        } else if (city.isEmpty()) {
            commonMethods.showToast(this, resources.getString(R.string.enter_city))
            return false
        } else if (postalCode.isEmpty()) {
            commonMethods.showToast(this, resources.getString(R.string.enter_postalcode))
            return false
        } else if (country.isEmpty()) {
            commonMethods.showToast(this, resources.getString(R.string.enter_country))
            return false
        } else if (state.isEmpty()) {
            commonMethods.showToast(this, resources.getString(R.string.enter_state))
            return false
        } else if (phone.isEmpty()) {
            commonMethods.showToast(this, resources.getString(R.string.enter_phone))
            return false
        } else
            return true
    }

    private fun initAddressAddApiCall(isValidate: String) {
        commonMethods.showProgressDialog(this)

        var addAddressHashMap: HashMap<String, String> = HashMap()
        addAddressHashMap["user_id"] = sessionManager.userId.toString()
        addAddressHashMap["address"] = address
        addAddressHashMap["country"] = country
        addAddressHashMap["state"] = state
        addAddressHashMap["city"] = city
        addAddressHashMap["postal_code"] = postalCode
        addAddressHashMap["phone"] = phone
        addAddressHashMap["is_validate"] = isValidate

        commonViewModel!!.apiRequest(Enums.REQ_ADDRESS_CREATE, addAddressHashMap, this)


    }

    private fun initApiResponseHandler() {

        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel!!.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_PICKUP_POINT -> {
                        pickupPointModel =
                            commonViewModel?.liveDataResponse?.value as (PickupPointModel)

                        if (pickupPointModel.success) {
                            if (pickupPointModel.data!!.size > 0 && isPickup) {
                                pickupPointModel.data!![0].isSelected = true
                                pickupPointId = pickupPointModel.data!![0].id.toString()
                                staffId = pickupPointModel.data!![0].staffId.toString()
                                initPickupPointRecyclerview()
                            } else {
                                binding.tvHomeSelect.setBackgroundResource(R.drawable.bg_unselected_tab)
                                binding.tvHomeAddress.setTextColor(resources.getColor(R.color.txt_secondary))
                                binding.tvPickupPoint.visibility = View.GONE
                                binding.tvPickupSelect.visibility = View.GONE
                                binding.rltHomeAddress.visibility = View.VISIBLE
                                binding.rltPickupPoint.visibility = View.GONE
                            }


                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }

                    }

                    Enums.REQ_ADDRESS_LIST -> {
                        commonMethods.hideProgressDialog()
                        addressListModel =
                            commonViewModel?.liveDataResponse?.value as (AddressListModel)

                        if (addressListModel.success) {
                            if (addressListModel.data!!.size > 0) {

                                var pos = 0
                                for (i in 0..addressListModel.data!!.size - 1) {
                                    if (addressListModel.data!![i].setDefault == 1) {
                                        pos = i
                                        break
                                    }
                                }
                                addressListModel.data!![pos].isSelected = true
                                addressId = addressListModel.data!![0].id.toString()

                                if (isNeedDeliveryInfo().not())
                                    initShippingCostApiCall(addressListModel.data!![0].city)

                                initHomeAddressRecyclerView()
                            }
                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }

                    }

                    Enums.REQ_ADDRESS_CREATE -> {
                        commonMethods.hideProgressDialog()

                        /* if (genericModel.result) {
                             initAddressListApiCall()
                             commonMethods.showToast(this,genericModel.message)
                         }else{
                             commonMethods.showToast(this,genericModel.message)
                         }
 */

                        genericModel =
                            commonViewModel?.liveDataResponse?.value as (GenericModel)


                        if (genericModel.result) {
                            tvInvalideAddress?.visibility = View.GONE

                            if (genericModel.isShipEngineEnabled)
                                showAddressConfirmationPopup()
                            else {
                                confirmationAlertDialog?.dismiss()
                                addressAlertDialog?.dismiss()
                                initAddressListApiCall()
                            }
                        } else {
                            tvInvalideAddress?.visibility = View.VISIBLE
                            tvInvalideAddress?.startAnimation(shakeError())

                            commonMethods.showToast(this, genericModel.message)
                        }

                    }

                    Enums.REQ_PROCEED_CHECKOUT -> {
                        val commonDataModel =
                            commonViewModel?.liveDataResponse?.value as (CommonDataModel)

                        if (commonDataModel.result) {

                            if (isNeedDeliveryInfo()) {
                                moveDeliveryInfoPage()
                            } else {
                                callCheckOutPage()
                            }
                            //|
                            commonMethods.showToast(this, commonDataModel.message)
                        } else {
                            commonMethods.showToast(this, commonDataModel.message)
                        }

                    }

                    Enums.REQ_SHIPPING_COST -> {
                        commonMethods.hideProgressDialog()
                        shippingCostModel =
                            commonViewModel?.liveDataResponse?.value as (ShippingCostModel)

                        if (shippingCostModel.result) {
                            binding.header.tvTitle.text =
                                resources.getString(R.string.shipping_cost_title) + " " + shippingCostModel.valueString
                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }

                    }

                    Enums.REQ_CITY_LIST -> {
                        cityListModel = commonViewModel?.liveDataResponse?.value as (CityListModel)
                        commonMethods.hideProgressDialog()
                        if (cityListModel.success) {

                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }

                    }

                    Enums.REQ_STATE_LIST -> {
                        stateListModel =
                            commonViewModel?.liveDataResponse?.value as (StateListModel)
                        commonMethods.hideProgressDialog()
                        if (stateListModel.success) {

                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }

                    }

                    Enums.REQ_COUNTRY_LIST -> {
                        countryListModel =
                            commonViewModel?.liveDataResponse?.value as (CountryListModel)

                        if (countryListModel.success) {

                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }

                    }
                }
            }
        })
    }


    fun shakeError(): TranslateAnimation? {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.setDuration(500)
        shake.setInterpolator(CycleInterpolator(7f))
        return shake
    }

    private fun callCheckOutPage() {
        if (addressListModel.data!!.size > 0) {
            val intent = Intent(this, CheckoutActivity::class.java)
            val animation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
            ).toBundle()
            startActivity(intent, animation)
        } else {
            commonMethods.showToast(this, resources.getString(R.string.please_add_address))
        }
    }

    private fun moveDeliveryInfoPage() {
        if (addressListModel.data!!.size > 0) {
            val intent = Intent(this, DeliveryInfoActivity::class.java)
            val animation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
            ).toBundle()
            startActivity(intent, animation)
        } else {
            commonMethods.showToast(this, resources.getString(R.string.please_add_address))
        }
    }

    override fun onResume() {
        super.onResume()
        initAddressListApiCall()

        if (isNeedDeliveryInfo().not())
            initPickupApiCall()

        initCountryApiCall()

    }


    override fun onPickupPointClick(pos: Int) {
        for (i in pickupPointModel.data!!.indices) {
            pickupPointModel.data!![i].isSelected = false
        }
        pickupPointModel.data!![pos].isSelected = true
        pickupPointId = pickupPointModel.data!![pos].id.toString()
        staffId = pickupPointModel.data!![pos].staffId.toString()
        pickupPointAdapter.notifyDataSetChanged()
    }


    override fun onHomeAddressClick(pos: Int) {
        for (i in addressListModel.data!!.indices) {
            addressListModel.data!![i].isSelected = false
        }
        addressListModel.data!![pos].isSelected = true
        addressId = addressListModel.data!![pos].id.toString()
        if (isNeedDeliveryInfo().not())
            initShippingCostApiCall(addressListModel.data!![pos].city)
        addressListAdapter.notifyDataSetChanged()
    }

    override fun onClick(pos: Int, type: Int) {
        when (type) {
            0 -> {
                tvDialogCity.text = cityListModel.data!![pos].name
            }

            1 -> {
                countryId = countryListModel.data!![pos].id.toString()
                initStateApiCall()
                tvDialogCountry.text = countryListModel.data!![pos].name
            }

            else -> {
                stateId = stateListModel.data!![pos].id.toString()
                initCityApiCall()
                tvDialogState.text = stateListModel.data!![pos].name
            }
        }

        cityCountryDialog!!.dismiss()
    }

    fun isNeedDeliveryInfo() = intent.getBooleanExtra("is_need_delivery_infor", false)
}