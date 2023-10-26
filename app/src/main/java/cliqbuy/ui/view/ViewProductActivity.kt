package cliqbuy.ui.view

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityProfileBinding
import cliqbuy.databinding.ActivityViewProductBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.model.CommonHomeModel
import cliqbuy.model.GenericModel
import cliqbuy.model.ViewProductModel
import cliqbuy.ui.adapter.FeaturedCategoriesAdapter
import cliqbuy.ui.adapter.ViewProductAdapter
import cliqbuy.ui.fragments.CategoriesFragment
import cliqbuy.utils.PaginationScrollListener
import cliqbuy.utils.gone
import cliqbuy.utils.visible
import java.util.ArrayList
import java.util.HashMap

class ViewProductActivity : CommonActivity() {
    lateinit var binding : ActivityViewProductBinding
    var name : String = ""
    var searchName : String =""
    var Id : String = ""
    lateinit var viewProducthashmap: HashMap<String, String>
    lateinit var productModel : ViewProductModel
    private var gridLayoutManager: GridLayoutManager? = null
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES = 0
    var currentPage = PAGE_START
    var type = 0

    private lateinit var ViewProductAdapter: ViewProductAdapter
    var product = ArrayList<ViewProductModel.Data>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentValues()
        initApiResponseHandler()
        viewProductApiCall()


       binding.header.ivBack.setOnClickListener { onBackPressed() }

       binding.header.edtSearch.hint=resources.getString(R.string.search_product)+" "+name.toString()


       binding.header.ivSearch.setOnClickListener {
           searchName=binding.header.edtSearch.text.toString()
           viewProductSearchApiCall()
       }
        binding.header.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchName=binding.header.edtSearch.text.toString()
                viewProductSearchApiCall()
            }
            true
        }



        binding.swipeToRefresh.setOnRefreshListener {
            currentPage = 1
            productModel.data.clear()
            isLoading=false
            isLastPage = false
            binding.swipeToRefresh.isRefreshing = false

            viewProductApiCall()
        }
    }

    private fun getIntentValues() {

        name = intent.getStringExtra("name").toString()
        Id = intent.getStringExtra("id").toString()

        if (intent.hasExtra("type")){
            type = intent.getIntExtra("type",0)
        }

    }

    private fun viewProductApiCall() {
        commonMethods.showProgressDialog(this)
        viewProducthashmap = HashMap()
        viewProducthashmap["id"] = Id
        viewProducthashmap["name"] = ""
        viewProducthashmap["page"] = currentPage.toString()
        if (type == 1) {
            commonViewModel!!.apiRequest(Enums.REQ_SELLER_VIEW_PRODUCT, viewProducthashmap, this)
        }else if (type == 2){
            commonViewModel!!.apiRequest(Enums.REQ_VIEW_BRAND, viewProducthashmap, this)
        }else{
            commonViewModel!!.apiRequest(Enums.REQ_VIEW_PRODUCT, viewProducthashmap, this)
        }
    }
    private fun viewProductSearchApiCall() {
        commonMethods.showProgressDialog(this)
        viewProducthashmap = HashMap()
        viewProducthashmap["id"] = Id
        viewProducthashmap["name"] = searchName
        viewProducthashmap["page"] = "1"
        if (type == 1) {
            commonViewModel!!.apiRequest(Enums.REQ_SELLER_VIEW_PRODUCT, viewProducthashmap, this)
        }else if (type == 2){
            commonViewModel!!.apiRequest(Enums.REQ_VIEW_BRAND, viewProducthashmap, this)
        }else{
            commonViewModel!!.apiRequest(Enums.REQ_VIEW_PRODUCT, viewProducthashmap, this)
        }
    }


    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(this, androidx.lifecycle.Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_VIEW_PRODUCT ->{
                        commonMethods.hideProgressDialog()
                        productModel = commonViewModel!!.liveDataResponse.value as ViewProductModel
                        if (productModel.success!!){

                          TOTAL_PAGES = productModel!!.meta!!.lastPage!!

                            if (productModel.data.isEmpty()) {
                                binding.rvProductList.gone()
                                binding.tvNodata.visible()
                            } else {
                                binding.rvProductList.visible()
                                binding.tvNodata.gone()
                                initCategoriesRecyclerView()
                            }



                        }else{
                            commonMethods.showToast(this, productModel.status.toString())
                        }
                    }

                    Enums.REQ_VIEW_BRAND ->{
                        commonMethods.hideProgressDialog()
                        productModel = commonViewModel!!.liveDataResponse.value as ViewProductModel
                        if (productModel.success!!){

                          TOTAL_PAGES = productModel!!.meta!!.lastPage!!

                            if (productModel.data.isEmpty()) {
                                binding.rvProductList.gone()
                                binding.tvNodata.visible()
                            } else {
                                binding.rvProductList.visible()
                                binding.tvNodata.gone()
                                initCategoriesRecyclerView()
                            }



                        }else{
                            commonMethods.showToast(this, productModel.status.toString())
                        }
                    }

                    Enums.REQ_SELLER_VIEW_PRODUCT ->{
                        commonMethods.hideProgressDialog()
                        productModel = commonViewModel!!.liveDataResponse.value as ViewProductModel
                        if (productModel.success!!){

                          TOTAL_PAGES = productModel!!.meta!!.lastPage!!

                            if (productModel.data.isEmpty()) {
                                binding.rvProductList.gone()
                                binding.tvNodata.visible()
                            } else {
                                binding.rvProductList.visible()
                                binding.tvNodata.gone()
                                initCategoriesRecyclerView()
                            }



                        }else{
                            commonMethods.showToast(this, productModel.status.toString())
                        }
                    }
                }

            }else{
                commonMethods.hideProgressDialog()
            }

        })


    }

    private fun initCategoriesRecyclerView(){

        if(currentPage>1){
            commonMethods.mProgressDialog!!.hide()
            ViewProductAdapter.addAll(productModel)
            ViewProductAdapter.notifyDataSetChanged()
        }else{
        gridLayoutManager=  GridLayoutManager(this, 2)
        binding.rvProductList.layoutManager=gridLayoutManager
        ViewProductAdapter =
            ViewProductAdapter(this,productModel)

        binding.rvProductList.adapter = ViewProductAdapter}
        if (currentPage <= TOTAL_PAGES && TOTAL_PAGES > 1){

        }
        else{
            isLastPage = true
        }
        binding.rvProductList.addOnScrollListener(object : PaginationScrollListener(gridLayoutManager){
            override fun loadMoreItems() {
                this@ViewProductActivity.isLoading = true
                currentPage += 1
                viewProductApiCall()
                commonMethods.mProgressDialog!!.show()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading

            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

        })

    }

    companion object {
        private val PAGE_START = 1
    }
}