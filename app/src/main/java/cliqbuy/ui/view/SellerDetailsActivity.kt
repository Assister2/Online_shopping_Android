package cliqbuy.ui.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivitySellerDetailsBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.model.CommonHomeModel
import cliqbuy.model.StoreDetailsModel
import cliqbuy.ui.adapter.FeaturedCategoriesAdapter
import cliqbuy.ui.adapter.FeaturedProductsAdapter
import cliqbuy.ui.adapter.HomeTopBannerAdapter
import cliqbuy.ui.adapter.ShopSliderAdapter
import com.squareup.picasso.Picasso
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.util.*
import kotlin.collections.HashMap

class SellerDetailsActivity : CommonActivity() {

    lateinit var binding: ActivitySellerDetailsBinding
    lateinit var storeDetailsModel: StoreDetailsModel
    var id = 0

    lateinit var bannerAdapter: ShopSliderAdapter
    lateinit var viewPager: ViewPager
    lateinit var dotsIndicator: DotsIndicator
    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 500 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 2000

    lateinit var productsModel : CommonHomeModel
    lateinit var newArrivalModel : CommonHomeModel
    lateinit var topSellingModel : CommonHomeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = binding.vpTopBanner
        dotsIndicator = binding.diTopBanner

        binding.ivBack.setSafeOnClickListener { onBackPressed() }
        binding.rltProceed.setSafeOnClickListener { callViewProductsPage() }
        initViewModel()
        if (intent.hasExtra("id"))
            id = intent.getIntExtra("id",0)
        initApiResponseHandler()
        callShopDetailApi()
        callNewArrivalApi()
        callTopSellingApi()
        callFeatureProductApi()

        binding.swipeToRefresh.setOnRefreshListener {
            callShopDetailApi()
            callNewArrivalApi()
            callTopSellingApi()
            callFeatureProductApi()
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    private fun callViewProductsPage() {
        var intent = Intent(this, ViewProductActivity::class.java)
        intent.putExtra("name", storeDetailsModel.data[0].name)
        intent.putExtra("id", storeDetailsModel.data[0].id.toString())
        intent.putExtra("type",1)
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun callShopDetailApi() {
        commonMethods.showProgressDialog(this)
        var hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = id.toString()
        commonViewModel!!.apiRequest(Enums.REQ_SHOP_DETAILS,hashMap,this)
    }

    private fun callNewArrivalApi() {
        var hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = id.toString()
        commonViewModel!!.apiRequest(Enums.REQ_SELLER_NEW_ARRIVALS,hashMap,this)
    }

    private fun callTopSellingApi() {
        var hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = id.toString()
        commonViewModel!!.apiRequest(Enums.REQ_SELLER_TOP_SELLING,hashMap,this)
    }

    private fun callFeatureProductApi() {
        var hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = id.toString()
        commonViewModel!!.apiRequest(Enums.REQ_SELLER_FEATURED,hashMap,this)
    }

    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(this, androidx.lifecycle.Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_SHOP_DETAILS -> {

                        storeDetailsModel = commonViewModel?.liveDataResponse?.value as (StoreDetailsModel)

                        if (storeDetailsModel.success!!){
                            commonMethods.hideProgressDialog()
                            onSuccessStoreDetails()
                        }else{
                            commonMethods.showToast(this,resources.getString(R.string.please_try_again))
                        }
                    }
                    Enums.REQ_SELLER_FEATURED -> {
                        productsModel =commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (productsModel.success){
                            if (productsModel.data!!.size > 0){
                                binding.rvFeaturedProducts.visibility = View.VISIBLE
                                binding.tvNoFeatuedProduct.visibility = View.GONE
                                initProductsRecyclerView()
                            }else{
                                binding.rvFeaturedProducts.visibility = View.GONE
                                binding.tvNoFeatuedProduct.visibility = View.VISIBLE
                            }

                        }else{
                            commonMethods.showToast(this,productsModel.statusMessage)
                        }
                    }
                    Enums.REQ_SELLER_TOP_SELLING -> {

                        topSellingModel =commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (topSellingModel.success){
                            if (topSellingModel.data!!.size > 0){
                                topSellingModel.type = "top_selling"
                                binding.rvTopSelling.visibility = View.VISIBLE
                                binding.tvNoTopSelling.visibility = View.GONE
                                initTopSellingRecyclerView()
                            }else{
                                binding.rvTopSelling.visibility = View.GONE
                                binding.tvNoTopSelling.visibility = View.VISIBLE
                            }

                        }else{
                            commonMethods.showToast(this,topSellingModel.statusMessage)
                        }
                    }
                    Enums.REQ_SELLER_NEW_ARRIVALS -> {

                        newArrivalModel =commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (newArrivalModel.success){
                            if (newArrivalModel.data!!.size > 0){
                                newArrivalModel.type = "new_arrival"
                                binding.rvNewArrival.visibility = View.VISIBLE
                                binding.tvNoNewArrival.visibility = View.GONE
                                initNewArrivalRecyclerView()
                            }else{
                                binding.rvNewArrival.visibility = View.GONE
                                binding.tvNoNewArrival.visibility = View.VISIBLE
                            }

                        }else{
                            commonMethods.showToast(this,newArrivalModel.statusMessage)
                        }
                    }

                }
            }else{
                commonMethods.hideProgressDialog()
            }
        })
    }

    private fun onSuccessStoreDetails() {
        binding.ivAddress.setSafeOnClickListener {
            if (storeDetailsModel.data[0].address!! != "")
                showAddressDialog(storeDetailsModel.data[0].address!!)
        }
        binding.tvShopName.text = storeDetailsModel.data[0].name

        if ( storeDetailsModel.data[0].logo!!.isEmpty()) {
            Picasso.get().load(R.drawable.ic_empty_cart).error(R.drawable.ic_empty_cart).placeholder(R.drawable.ic_empty_cart).into(binding.ivShopImage)
        }
        else Picasso.get().load(resources.getString(R.string.imageUrl)+storeDetailsModel.data[0].logo!!).placeholder(
            R.drawable.ic_empty_cart).error(R.drawable.ic_empty_cart).into(binding.ivShopImage)

        if (storeDetailsModel.data[0].sliders.isNotEmpty()){
            binding.rltViewPager.visibility = View.VISIBLE
            initViewPager()
            initViewPagerAnimation()
        }else{
            binding.rltViewPager.visibility = View.GONE
        }


    }

    private fun showAddressDialog(address:String) {
        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_common, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        val tvMessage = dialogView.findViewById<View>(R.id.tv_message) as TextView
        val btnOk = dialogView.findViewById<View>(R.id.btn_ok) as Button
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel) as Button

        tvMessage.text = address

        btnCancel.visibility = View.GONE
        btnOk.text = resources.getString(R.string.close)

        btnOk.setSafeOnClickListener {
            alertDialog.dismiss() }


        alertDialog.show()
    }

    private fun initViewPager() {
        bannerAdapter = ShopSliderAdapter(this,storeDetailsModel)
        viewPager.adapter = bannerAdapter
        bannerAdapter.notifyDataSetChanged()
        dotsIndicator.setViewPager(viewPager)
    }

    private fun initViewPagerAnimation(){
        //Viewpager Slide animation
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == storeDetailsModel.data[0].sliders!!.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer() // This will create a new Thread

        timer!!.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    private fun initProductsRecyclerView(){
        binding.rvFeaturedProducts.layoutManager = GridLayoutManager(this, 2)
        val featuredProductsAdapter =
            FeaturedProductsAdapter(this,productsModel)
        binding.rvFeaturedProducts.adapter = featuredProductsAdapter

    }
    private fun initNewArrivalRecyclerView(){
        binding.rvNewArrival.layoutManager =  LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)
        val featuredCategoriesAdapter =
            FeaturedProductsAdapter(this,newArrivalModel)
        binding.rvNewArrival.adapter = featuredCategoriesAdapter

    }

    private fun initTopSellingRecyclerView(){
        binding.rvTopSelling.layoutManager =  LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        val featuredCategoriesAdapter =
            FeaturedProductsAdapter(this,topSellingModel)
        binding.rvTopSelling.adapter = featuredCategoriesAdapter

    }

}