package cliqbuy.ui.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityAddressDetailsBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.CommonKeys.deleteAddressId
import cliqbuy.helper.Enums
import cliqbuy.helper.Enums.REQ_MAKE_DEFAULT
import cliqbuy.model.*
import cliqbuy.ui.adapter.AddressListAdapter
import cliqbuy.ui.adapter.CityCountryAdapter
import cliqbuy.utils.gone
import cliqbuy.utils.visible
import java.util.HashMap

class AddressDetailsActivity : CommonActivity(), AddressListAdapter.ChangeOnClickListener,
    AddressListAdapter.DeleteOnClickListener, CityCountryAdapter.OnClickListener {
    lateinit var binding: ActivityAddressDetailsBinding
    private lateinit var rvCityCountry: RecyclerView


    lateinit var tvDialogCity: TextView
    lateinit var tvDialogCountry: TextView
    lateinit var tvDialogState: TextView

    var addressAlertDialog: AlertDialog? = null
    var confirmationAlertDialog: AlertDialog? = null
    var tvInvalideAddress: TextView? = null

    lateinit var genericModel: GenericModel

    var address: String = ""
    var country: String = ""
    var state: String = ""
    var city: String = ""
    var postalCode: String = ""
    var phone: String = ""
    var id: String = ""
    var countryId: String = ""
    var stateId: String = ""

    var redirect = 0

    //Model
    var cityListModel = CityListModel()
    var stateListModel = StateListModel()
    lateinit var countryListModel: CountryListModel
    lateinit var addressListModel: AddressListModel

    lateinit var cityCountryAdapter: CityCountryAdapter
    lateinit var addressListAdapter: AddressListAdapter

    var cityCountryDialog: android.app.AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getIntentValues()

        binding.commonHeader.ivBack.visibility =
            if (comeFrom().equals("productdetail")) View.GONE else View.VISIBLE

        binding.commonHeader.tvTitle.text = resources.getString(R.string.addresses_user)
        binding.commonHeader.ivBack.setSafeOnClickListener { onBackPressed() }
        binding.btnAddAddress.setSafeOnClickListener { addAddress() }

    }

    private fun getIntentValues() {
        if (intent.hasExtra("redirect")) {
            redirect = intent.getIntExtra("redirect", 0)
        }
    }

    override fun onResume() {
        super.onResume()
        initViewModel()
        initApiResponseHandler()
        initAddressListApiCall()
        initCountryApiCall()

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

    private fun initAddressAddApiCall(type: Int, isValidate: String = "1") {
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

        if (type == 0)
            commonViewModel!!.apiRequest(Enums.REQ_ADDRESS_CREATE, addAddressHashMap, this)
        else {
            addAddressHashMap["id"] = id
            commonViewModel!!.apiRequest(Enums.REQ_ADDRESS_UPDATE, addAddressHashMap, this)
        }

    }


    private fun initApiResponseHandler() {

        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel!!.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {

                    REQ_MAKE_DEFAULT -> {
                        Log.d("datasda", it.toString())
                        initAddressListApiCall()
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

                    Enums.REQ_ADDRESS_UPDATE -> {
                        commonMethods.hideProgressDialog()

                        genericModel =
                            commonViewModel?.liveDataResponse?.value as (GenericModel)


                        if (genericModel.result) {
                            tvInvalideAddress?.visibility = View.GONE

                            if (genericModel.isShipEngineEnabled)
                                showAddressConfirmationPopup(1)
                            else {
                                confirmationAlertDialog?.dismiss()
                                addressAlertDialog?.dismiss()
                                initAddressListApiCall()
                            }
                            commonMethods.showToast(this, genericModel.message)
                        } else {
                            tvInvalideAddress?.visibility = View.VISIBLE
                            tvInvalideAddress?.startAnimation(shakeError())

                            commonMethods.showToast(this, genericModel.message)
                        }

                    }

                    Enums.REQ_ADDRESS_CREATE -> {
                        commonMethods.hideProgressDialog()

                        genericModel =
                            commonViewModel?.liveDataResponse?.value as (GenericModel)

                        if (genericModel.result) {
                            tvInvalideAddress?.visibility = View.GONE

                            if (genericModel.isShipEngineEnabled)
                                showAddressConfirmationPopup(0)
                            else {
                                confirmationAlertDialog?.dismiss()
                                addressAlertDialog?.dismiss()

                                if (comeFrom().equals("productdetail")) {
                                    super.onBackPressed()
                                } else
                                    initAddressListApiCall()
                            }
                            commonMethods.showToast(this, genericModel.message)
                        } else {
                            tvInvalideAddress?.visibility = View.VISIBLE
                            tvInvalideAddress?.startAnimation(shakeError())
                            commonMethods.showToast(this, genericModel.message)
                        }

                    }

                    Enums.REQ_ADDRESS_DELETE -> {
                        val genericModel =
                            commonViewModel?.liveDataResponse?.value as (GenericModel)

                        if (genericModel.result) {
                            deleteAddressId = 0
                            initAddressListApiCall()
                            commonMethods.showToast(this, genericModel.message)
                        } else {
                            commonMethods.showToast(this, genericModel.message)
                        }

                    }

                    Enums.REQ_ADDRESS_LIST -> {
                        addressListModel =
                            commonViewModel?.liveDataResponse?.value as (AddressListModel)
                        commonMethods.hideProgressDialog()
                        if (addressListModel.success) {
                            if (addressListModel.data!!.size > 0) {
                                binding.rvAddressList.visible()
                                initRecyclerView()
                            } else {
                                binding.rvAddressList.gone()
                            }
                        } else {
                            commonMethods.showToast(this, addressListModel.message)
                        }

                    }
                }
            }
        })
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

    private fun addAddress() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_address_add, null)
        dialogBuilder.setView(dialogView)
        addressAlertDialog = dialogBuilder.create()
        addressAlertDialog!!.setCancelable(true)
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
                initAddressAddApiCall(0)
                //  addressAlertDialog?.dismiss()
            }
        }

        addressAlertDialog?.show()
    }

    fun showAddressConfirmationPopup(type : Int) {
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
                initAddressAddApiCall(type, "0")
                // confirmationAlertDialog?.dismiss()
            }
        }

        confirmationAlertDialog?.show()
    }


    fun shakeError(): TranslateAnimation? {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.setDuration(500)
        shake.setInterpolator(CycleInterpolator(7f))
        return shake
    }

    private fun initRecyclerView() {
        binding.rvAddressList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        addressListAdapter =
            AddressListAdapter(this, this, this, addressListModel, ::setDefault)
        binding.rvAddressList.adapter = addressListAdapter
        addressListAdapter.notifyDataSetChanged()
    }

    fun setDefault(id: Int) {

        commonViewModel!!.apiRequest(Enums.REQ_MAKE_DEFAULT, hashMapOf<String, String>().apply {
            put("user_id", sessionManager.userId.toString())
            put("id", id.toString())
        }, this)
    }

    override fun onChangeClick(
        pos: Int,
        changeAddress: String,
        changeCity: String,
        changeState: String,
        changePostalCode: String,
        changeCountry: String,
        changePhone: String,
        chnageId: Int,
    ) {

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

        btAdd.text = resources.getString(R.string.update)
        tvAddress.setText(changeAddress)
        tvPostalCode.setText(changePostalCode)
        tvPhone.setText(changePhone)
        tvDialogCity.text = changeCity
        tvDialogCountry.text = changeCountry
        tvDialogState.text = changeState

        rltCity.setSafeOnClickListener {
            if (tvDialogCountry.text.isEmpty()) {
                commonMethods.showToast(this, resources.getString(R.string.enter_country))
            } else if (tvDialogState.text.isEmpty()) {
                commonMethods.showToast(this, resources.getString(R.string.enter_state))
            } else {
                cityCountryPopup(0)
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
                cityCountryPopup(2)
            }
        }


        btClose.setSafeOnClickListener { addressAlertDialog?.dismiss() }
        btAdd.setSafeOnClickListener {
            address = tvAddress.text.toString()
            city = tvDialogCity.text.toString()
            country = tvDialogCountry.text.toString()
            state = tvDialogState.text.toString()
            phone = tvPhone.text.toString()
            postalCode = tvPostalCode.text.toString()
            id = chnageId.toString()
            if (validateCheck()) {
                initAddressAddApiCall(1)
                //alertDialog.dismiss()
            }
        }

        addressAlertDialog?.show()
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
            CityCountryAdapter(
                this,
                countryListModel,
                cityListModel,
                stateListModel,
                type,
                this
            )
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCityCountry.layoutManager = linearLayoutManager
        rvCityCountry.adapter = cityCountryAdapter
    }

    override fun onDeleteClick(pos: Int, id: Int) {

        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_common, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        val tvMessage = dialogView.findViewById<View>(R.id.tv_message) as TextView
        val btnOk = dialogView.findViewById<View>(R.id.btn_ok) as Button
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel) as Button

        tvMessage.text = resources.getString(R.string.remove_address)


        btnOk.setSafeOnClickListener {
            deleteAddressId = id
            callDeleteAddressApi()
            alertDialog.dismiss()
        }
        btnCancel.setSafeOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    private fun callDeleteAddressApi() {
        commonViewModel!!.apiRequest(Enums.REQ_ADDRESS_DELETE, hashMapOf<String, String>().apply {
            put("user_id", sessionManager.userId.toString())

        }, this)
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

    override fun onBackPressed() {

        if (comeFrom().equals("login")) {
            callHome()
        } else if (comeFrom().equals("productdetail")) {

        } else if (redirect == 1) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.also { it.putExtra(CommonKeys.fragmentType, 4) }
            val animation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.ub__slide_in_left, R.anim.ub__slide_out_right
            ).toBundle()
            startActivity(intent, animation)
        } else {
            super.onBackPressed()
        }

    }

    fun comeFrom() = intent.getStringExtra("comeFom") ?: ""
    private fun callHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType, 1) }
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.cb_fade_in,
            R.anim.cb_face_out
        ).toBundle()
        startActivity(intent, animation)
    }

}