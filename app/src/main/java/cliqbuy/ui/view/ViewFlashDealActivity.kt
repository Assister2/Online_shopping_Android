package cliqbuy.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityViewFlashdealBinding
import cliqbuy.helper.CommonKeys.dealId
import cliqbuy.helper.CommonKeys.dealName
import cliqbuy.helper.Enums.REQ_FLASH_DETAILS
import cliqbuy.model.ViewProductModel
import cliqbuy.ui.adapter.ViewProductAdapter
import java.util.*

class ViewFlashDealActivity : CommonActivity() {

    lateinit var binding: ActivityViewFlashdealBinding
    var name: String = ""
    var searchName: String = ""
    private var id: String = ""
    lateinit var viewProductModel: ViewProductModel
    var filteredItems = ViewProductModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFlashdealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initApiResponseHandler()
        getIntentValues()
        viewFlashDealProducts()

        binding.header.ivBack.setOnClickListener { onBackPressed() }

        binding.header.edtSearch.hint = resources.getString(R.string.search_product) +" "+ name

        binding.header.edtSearch.addTextChangedListener(ProductItemWatcher(binding.header.edtSearch))

        binding.header.ivSearch.setOnClickListener {
            searchName = binding.header.edtSearch.text.toString()
            viewProductSearchApiCall()
        }
        /*
        binding.header.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchName = binding.header.edtSearch.text.toString()
                viewProductSearchApiCall()
            }
            true
        }
*/
    }

    private inner class ProductItemWatcher(private val view: View) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {

            if (s.toString() != "") filter(s.toString()) else viewFlashDealProducts()
        }

        override fun afterTextChanged(s: Editable) {
        }
    }

    fun filter(text: String) {
        try {
            filteredItems.data.clear()

            for (items in viewProductModel.data) {

                if (items.name != null && items.name!!.toLowerCase().contains(text.toLowerCase())) {
                    filteredItems.data.add(items)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (filteredItems.data.isNotEmpty()) {

            // viewProductModel =filteredItems
            initCategoriesRecyclerView(filteredItems)
        }
    }
    private fun getIntentValues() {

        name = intent.getStringExtra(dealName).toString()
        id = intent.getStringExtra(dealId).toString()

    }

    private fun viewFlashDealProducts() {
        val dealProducts: HashMap<String, String> = HashMap()
        dealProducts["id"]= id
        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_FLASH_DETAILS,dealProducts,this)

    }

    private fun viewProductSearchApiCall() {
    }

    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(this, Observer {

            if (commonViewModel?.apiResponseHandler!!.isSuccess)    commonMethods.hideProgressDialog()

            when (commonViewModel!!.responseCode) {

                REQ_FLASH_DETAILS -> {
                    viewProductModel = commonViewModel!!.liveDataResponse.value as ViewProductModel

                    if (viewProductModel.success!!) {
                        if (viewProductModel.data.size >0) {

                            initCategoriesRecyclerView(viewProductModel)
                            //filteredItems = viewProductModel
                        }
                        else {
                            binding.rvProductList.visibility = View.GONE
                            binding.tvNoProducts.visibility = View.VISIBLE
                        }

                    } else {
                        commonMethods.showToast(this, viewProductModel.status.toString())
                    }
                }
            }

        })


    }

    private fun initCategoriesRecyclerView(productModel: ViewProductModel) {

        binding.rvProductList.layoutManager = GridLayoutManager(this, 2)
        val viewProductAdapter = ViewProductAdapter(this, productModel)
        binding.rvProductList.adapter = viewProductAdapter

    }
}