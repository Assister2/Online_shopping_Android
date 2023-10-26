package cliqbuy.ui.view


import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivitySearchBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.model.CategoriesModel
import cliqbuy.model.SellerModels
import cliqbuy.model.ViewProductModel
import cliqbuy.ui.adapter.SellerProductsAdapter
import cliqbuy.ui.adapter.ViewProductAdapter
import cliqbuy.ui.fragments.DrawerFragment
import cliqbuy.utils.PaginationScrollListener
import cliqbuy.utils.gone
import cliqbuy.utils.visible
import kotlinx.android.synthetic.main.activity_search.*


class HomeSearchActivity : CommonActivity(),AdapterView.OnItemSelectedListener,DrawerFragment.ApplyFilterListener {

    lateinit var binding: ActivitySearchBinding
    var country = arrayOf("")
    private lateinit var drawerFragment: DrawerFragment
    lateinit var sellerModel: SellerModels
    lateinit var brandsModel: SellerModels
    lateinit var productModel : ViewProductModel
    private var gridLayoutManager: GridLayoutManager? = null
    private lateinit var ViewProductAdapter: ViewProductAdapter
    private lateinit var sellerProductsAdapter: SellerProductsAdapter
     var isChecked =""
    var brands : Boolean =false
    var seller : Boolean =false
    var product : Boolean =false
    var searchName : String =""
    var type : String =""
    var sortKey : String =""
    var brandsId : String =""
    var categoriesId : String =""
    var maxValue : String =""
    var minValue : String =""
    lateinit var filterCategoriesModel: CategoriesModel
    lateinit var filterBrandsModel: CategoriesModel
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES = 0
    var currentPage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initApiResponseHandler()
        type = intent.getStringExtra("type").toString()
       // getBrandsApiCall()
        filterCategoriesApiCall()
        filterBrandsApiCall()

        if (type == "brands"){
           country = arrayOf(getString(R.string.brands),getString(R.string.sellers), getString(R.string.products))
        }else if (type == "seller"){
            country = arrayOf(getString(R.string.sellers),getString(R.string.brands), getString(R.string.products))
        }else if(type =="product"){
            country = arrayOf(getString(R.string.products),getString(R.string.brands), getString(R.string.sellers))
            binding.sort.visibility = View.VISIBLE
            binding.filter.visibility = View.VISIBLE
        }
        filter()

        binding.swipeToRefresh.setOnRefreshListener {
            currentPage = 1
            isLoading = false
            searchName=binding.header.edtSearch.text.toString()
            if (brands){
                getBrandsApiCall(searchName,currentPage.toString())
            }else if(seller){
                getSellerApiCall(searchName)
            }else if(product){
                if(isChecked=="1"){
                    sortKey = ""
                }else if (isChecked=="2") {
                    sortKey = CommonKeys.HIGHTOLOW
                }else if(isChecked=="3"){
                    sortKey = CommonKeys.LOWTOHIGH
                }else if(isChecked=="4"){
                    sortKey = CommonKeys.NEWARRIVAL
                }else if(isChecked=="5"){
                    sortKey = CommonKeys.POPULARITY
                }else if(isChecked=="6"){
                    sortKey = CommonKeys.TOPRATED
                }else{
                    sortKey = ""
                }

                sortProductsApiCall(searchName)
            }
            binding.swipeToRefresh.isRefreshing = false
        }


        binding.header.edtSearch.hint= getString(R.string.search_here)
        binding.header.ivBack.setSafeOnClickListener { onBackPressed() }
        binding.filter.setSafeOnClickListener {
            if (filterCategoriesModel.data.size > 0 || filterBrandsModel.data.size > 0 ) {
                Drawer()
            }
            if(sessionManager.languageCode == "ar"){
                binding.drawerLayout.openDrawer(Gravity.LEFT);

            }else{
                binding.drawerLayout.openDrawer(Gravity.RIGHT);
            }

        }


        binding.sort.setSafeOnClickListener {
            showDialog()
        }

        binding.header.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchName=binding.header.edtSearch.text.toString()
                if (brands){
                    getBrandsApiCall(searchName,currentPage.toString())
                }else if(seller){
                    getSellerApiCall(searchName)
                }else if(product){
                    if(isChecked=="1"){
                        sortKey = ""
                    }else if (isChecked=="2") {
                        sortKey = CommonKeys.HIGHTOLOW
                    }else if(isChecked=="3"){
                        sortKey = CommonKeys.LOWTOHIGH
                    }else if(isChecked=="4"){
                        sortKey = CommonKeys.NEWARRIVAL
                    }else if(isChecked=="5"){
                        sortKey = CommonKeys.POPULARITY
                    }else if(isChecked=="6"){
                        sortKey = CommonKeys.TOPRATED
                    }else{
                        sortKey = ""
                    }

                    sortProductsApiCall(searchName)
                }

            }
            true
        }
    }

    private fun Drawer() {
        val bundle = Bundle()
        bundle.putSerializable("categoriesModel",filterCategoriesModel)
        bundle.putSerializable("brandModel",filterBrandsModel)
        drawerFragment = supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as DrawerFragment
        drawerFragment.arguments = bundle
        drawerFragment.init(R.id.fragment_navigation_drawer, drawer_layout)

    }

    fun filter(){
        binding.cardSpinner.onItemSelectedListener = this@HomeSearchActivity
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, country)
        aa.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item)
        binding.cardSpinner.adapter = aa

    }
    private fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialog = inflater.inflate(R.layout.dialog_sort_item, null)
        dialogBuilder.setView(dialog)
        dialogBuilder.setCancelable(false)
        val alertDialog = dialogBuilder.create()
        val close: TextView = dialog.findViewById(R.id.tv_close)!!
        val default: RadioButton = dialog.findViewById(R.id.rd_default)!!
        val high_To_Low: RadioButton = dialog.findViewById(R.id.rd_price_high_to_low)!!
        val low_To_High: RadioButton = dialog.findViewById(R.id.rd_price_low_to_high)!!
        val new_Arrival: RadioButton = dialog.findViewById(R.id.rd_newarival)!!
        val popularity: RadioButton = dialog.findViewById(R.id.rd_popularity)!!
        val top_Rated: RadioButton = dialog.findViewById(R.id.rd_toprated)!!

        if(isChecked=="1"){
            default.isChecked=true
        }else if (isChecked=="2"){
            high_To_Low.isChecked=true
        }else if(isChecked=="3"){
            low_To_High.isChecked=true
        }else if(isChecked=="4"){
            new_Arrival.isChecked=true
        }else if(isChecked=="5"){
            popularity.isChecked=true
        }else if(isChecked=="6"){
            top_Rated.isChecked=true
        }

       close.setSafeOnClickListener {
           alertDialog.dismiss()
       }

       default.setSafeOnClickListener {
           sortKey = ""
           sortProductsApiCall(searchName)
           isChecked="1"
           currentPage = 1
           productModel.data.clear()
           alertDialog.dismiss()
       }
        high_To_Low.setSafeOnClickListener {
            currentPage = 1
            productModel.data.clear()
            sortKey = CommonKeys.HIGHTOLOW
            sortProductsApiCall(searchName)
            isChecked="2"
            alertDialog.dismiss()
        }
        low_To_High.setSafeOnClickListener {
            currentPage = 1
            productModel.data.clear()
            sortKey = CommonKeys.LOWTOHIGH
            sortProductsApiCall(searchName)
            isChecked="3"
            alertDialog.dismiss()
        }
        new_Arrival.setSafeOnClickListener {
            currentPage = 1
            productModel.data.clear()
            sortKey = CommonKeys.NEWARRIVAL
            sortProductsApiCall(searchName)
            isChecked="4"
            alertDialog.dismiss()
        }
        popularity.setSafeOnClickListener {
            currentPage = 1
            productModel.data.clear()
            sortKey = CommonKeys.POPULARITY
            sortProductsApiCall(searchName)
            isChecked="5"
            alertDialog.dismiss()
        }
        top_Rated.setSafeOnClickListener {
            currentPage = 1
            productModel.data.clear()
            sortKey = CommonKeys.TOPRATED
            sortProductsApiCall(searchName)
            isChecked="6"
            alertDialog.dismiss()
        }


        alertDialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        val face = Typeface.createFromAsset(assets, "fonts/amazon_ember_bold.ttf")
        (p1 as TextView).typeface = face
        (p1 as TextView).textSize = 14F
        currentPage = 1
        searchName=binding.header.edtSearch.text.toString()
        if(p0!!.selectedItem==getString(R.string.brands)){
            brands=true
            product=false
            seller=false
            binding.sort.gone()
            binding.filter.gone()
           /* if(sellerModel.data.size>1){
                sellerModel.data.clear()
            }*/

            getBrandsApiCall(searchName,currentPage.toString())
        }else if (p0!!.selectedItem==getString(R.string.sellers)) {
            seller=true
            brands=false
            product=false
            binding.sort.gone()
            binding.filter.gone()
            /*if(brandsModel.data.size>1){
                brandsModel.data.clear()
            }*/

            getSellerApiCall(searchName)
        }else if (p0!!.selectedItem==getString(R.string.products)){
            product=true
            seller=false
            brands=false
            binding.sort.visibility = View.VISIBLE
            binding.filter.visibility = View.VISIBLE
            sortKey = ""
            sortProductsApiCall(searchName)
        }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }



    private fun getSellerApiCall(name : String) {
        commonMethods.showProgressDialog(this)
        var sellerHashMap: HashMap<String, String> = HashMap()
        sellerHashMap["name"] = name
        sellerHashMap["page"] = currentPage.toString()
        commonViewModel!!.apiRequest(Enums.REQ_SELLER,sellerHashMap,this)

    }
    private fun getBrandsApiCall(name :String,page : String) {
        commonMethods.showProgressDialog(this)
        var brandHashMap: HashMap<String, String> = HashMap()
        brandHashMap["name"] = name
        brandHashMap["page"] = page
        commonViewModel!!.apiRequest(Enums.REQ_BRANDS,brandHashMap,this)

    }
    private fun sortProductsApiCall( name :String) {
        commonMethods.showProgressDialog(this)
        var sortProductHashMap: HashMap<String, String> = HashMap()
        sortProductHashMap["brands"] = brandsId
        sortProductHashMap["categories"] = categoriesId
        sortProductHashMap["max"] = maxValue
        sortProductHashMap["min"] = minValue
        sortProductHashMap["name"] = name
        sortProductHashMap["page"] = currentPage.toString()
        sortProductHashMap["sort_key"] =sortKey
        commonViewModel!!.apiRequest(Enums.REQ_SORT,sortProductHashMap,this).toString()

    }
    private fun filterCategoriesApiCall() {

        commonViewModel!!.apiRequest(Enums.REQ_FILTER_CATEGORIES,commonHashMap,this)

    }

    private fun filterBrandsApiCall() {

        commonViewModel!!.apiRequest(Enums.REQ_FILTER_BRANDS,commonHashMap,this)

    }
    /*private fun getProductsApiCall() {
        commonMethods.showProgressDialog(this)

        commonViewModel!!.apiRequest(Enums.REQ_SEARCH_PRODUCT,commonHashMap,this)

    }*/
    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(this, androidx.lifecycle.Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_SELLER -> {

                        sellerModel = commonViewModel?.liveDataResponse?.value as (SellerModels)

                        if (sellerModel.success!!){

                            commonMethods.hideProgressDialog()

                            if (sellerModel.data!!.size > 0) {
                                binding.rvSearchItems.visible()
                                binding.tvNoData.gone()
                                TOTAL_PAGES = sellerModel!!.meta!!.lastPage!!
                                initCategoriesRecyclerView(true)
                            }else{
                                if (currentPage ==1 ) {
                                    binding.tvNoData.text =
                                        resources.getString(R.string.no_shop_available)
                                    binding.rvSearchItems.gone()
                                    binding.tvNoData.visible()
                                }
                            }

                        }else{
                            commonMethods.showToast(this,sellerModel.status.toString())
                        }
                    }Enums.REQ_BRANDS -> {
                        commonMethods.hideProgressDialog()
                    brandsModel = commonViewModel?.liveDataResponse?.value as (SellerModels)

                        if (brandsModel.success!!){

                            commonMethods.hideProgressDialog()
                            TOTAL_PAGES = brandsModel!!.meta!!.lastPage!!
                            if (brandsModel.data!!.size > 0) {
                                binding.rvSearchItems.visible()
                                binding.tvNoData.gone()
                                initCategoriesRecyclerView(false)
                            }else{
                                if (currentPage ==1 ) {
                                    binding.tvNoData.text =
                                        resources.getString(R.string.no_brand_available)
                                    binding.rvSearchItems.gone()
                                    binding.tvNoData.visible()
                                }
                            }

                        }else{
                           commonMethods.showToast(this,brandsModel.status.toString())
                        }
                    }Enums.REQ_FILTER_CATEGORIES -> {
                       commonMethods.hideProgressDialog()
                    filterCategoriesModel = commonViewModel!!.liveDataResponse.value as CategoriesModel

                        if (filterCategoriesModel.success!!){


                        }else{

                        }
                    }
                    Enums.REQ_FILTER_BRANDS -> {
                        commonMethods.hideProgressDialog()
                    filterBrandsModel = commonViewModel!!.liveDataResponse.value as CategoriesModel

                        if (filterBrandsModel.success!!){


                        }else{

                        }
                    }
                    Enums.REQ_SORT -> {

                       commonMethods.hideProgressDialog()
                        productModel = commonViewModel!!.liveDataResponse.value as ViewProductModel

                        if (productModel.success!!){
                            TOTAL_PAGES = productModel!!.meta!!.lastPage!!
                            if (productModel.data!!.size > 0) {
                                binding.rvSearchItems.visible()
                                binding.tvNoData.gone()
                                initProductsRecyclerView()
                            }else{
                                if (currentPage ==1 ) {
                                    binding.tvNoData.text =
                                        resources.getString(R.string.no_product_available)
                                    binding.rvSearchItems.gone()
                                    binding.tvNoData.visible()
                                }
                            }

                        }else{

                        }
                    }
                }
            }else{
                commonMethods.hideProgressDialog()
            }
        })
    }


    private fun initCategoriesRecyclerView(isSeller : Boolean){

        if(currentPage>1){
            commonMethods.mProgressDialog!!.hide()
            if(brands){
                //sellerProductsAdapter.clearAll()
                sellerProductsAdapter.addAll(brandsModel)

            }else{
                //sellerProductsAdapter.clearAll()
                sellerProductsAdapter.addAll(sellerModel)

            }

            sellerProductsAdapter.notifyDataSetChanged()
        }else{
            gridLayoutManager=  GridLayoutManager(this, 2)
            binding.rvSearchItems.layoutManager=gridLayoutManager
            if (brands){
                sellerProductsAdapter =
                    SellerProductsAdapter(this,brandsModel,isSeller)
            }else {
                sellerProductsAdapter =
                    SellerProductsAdapter(this, sellerModel, isSeller)
            }
            binding.rvSearchItems.adapter = sellerProductsAdapter}
        isLoading = currentPage == TOTAL_PAGES

        binding.rvSearchItems.addOnScrollListener(object : PaginationScrollListener(gridLayoutManager){
            override fun loadMoreItems() {
                this@HomeSearchActivity.isLoading = true
                currentPage += 1
                if(brands){
                    getBrandsApiCall(searchName,currentPage.toString())
                }else if(product){
                    sortProductsApiCall(searchName)
                }else{
                    getSellerApiCall(searchName)
                }

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

    private fun initProductsRecyclerView(){
        if(currentPage>1){
            ViewProductAdapter.addAll(productModel)
            ViewProductAdapter.notifyDataSetChanged()
            isLoading = currentPage == TOTAL_PAGES

        }else {
            gridLayoutManager = GridLayoutManager(this, 2)
            binding.rvSearchItems.layoutManager = gridLayoutManager
            ViewProductAdapter =
                ViewProductAdapter(this, productModel)
            binding.rvSearchItems.adapter = ViewProductAdapter
        }

        isLoading = currentPage == TOTAL_PAGES

        binding.rvSearchItems.addOnScrollListener(object : PaginationScrollListener(gridLayoutManager){
            override fun loadMoreItems() {
                this@HomeSearchActivity.isLoading = true
                currentPage += 1
                if(brands){
                    getBrandsApiCall(searchName,currentPage.toString())
                }else if(product){
                    sortProductsApiCall(searchName)
                }else{
                    getSellerApiCall(searchName)
                }

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


    override fun onApplyFilter(max: String, min: String, brandId: String, categoryId: String) {
        commonMethods.showProgressDialog(this)
        currentPage = 1
        brandsId = brandId
        categoriesId = categoryId
        maxValue = max
        minValue = min
        var sortProductHashMap: HashMap<String, String> = HashMap()
        sortProductHashMap["brands"] = brandId
        sortProductHashMap["categories"] = categoryId
        sortProductHashMap["max"] = max
        sortProductHashMap["min"] = min
        sortProductHashMap["name"] = searchName
        sortProductHashMap["page"] = currentPage.toString()
        sortProductHashMap["sort_key"] =sortKey
        commonViewModel!!.apiRequest(Enums.REQ_SORT,sortProductHashMap,this).toString()
    }

}
