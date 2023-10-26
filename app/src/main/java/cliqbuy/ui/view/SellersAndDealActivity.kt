package cliqbuy.ui.view

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivitySellersAndDealBinding
import cliqbuy.helper.Enums
import cliqbuy.model.CommonHomeModel
import cliqbuy.ui.adapter.FeaturedProductsAdapter

class SellersAndDealActivity : CommonActivity() {
    lateinit var binding : ActivitySellersAndDealBinding
    var type : Int = 0
    lateinit var topDealModel: CommonHomeModel
    lateinit var topSellerModel: CommonHomeModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellersAndDealBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        initApiResponseHandler()
        getIntentValues()
        getApiCall()
        binding.header.ivBack.visibility = View.VISIBLE
        binding.header.ivBack.setSafeOnClickListener { onBackPressed() }
    }

    private fun getApiCall() {
        commonMethods.showProgressDialog(this)
        if (type == 0)
            commonViewModel!!.apiRequest(Enums.REQ_TOP_SELLER,commonHashMap,this)
        else
            commonViewModel!!.apiRequest(Enums.REQ_TOP_DEAL,commonHashMap,this)
    }

    private fun getIntentValues() {
        if(intent.hasExtra("type")){
            type = intent.getIntExtra("type",0)

            if (type == 0)
                binding.header.tvTitle.text = resources.getString(R.string.top_selling_products)
            else
                binding.header.tvTitle.text = resources.getString(R.string.today_deal)
        }
    }

    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(this, androidx.lifecycle.Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {
                commonMethods.hideProgressDialog()
                when (commonViewModel!!.responseCode) {
                    Enums.REQ_TOP_DEAL -> {
                        topDealModel =commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (topDealModel.success){
                            if (topDealModel.data!!.size > 0) {
                                binding.rvTopDeals.visibility = View.VISIBLE
                                binding.tvNotAvailable.visibility = View.GONE
                                binding.rvTopSelling.visibility = View.GONE
                                initTopDealRecyclerView()
                            }else{
                                binding.rvTopSelling.visibility = View.GONE
                                binding.rvTopDeals.visibility = View.GONE
                                binding.tvNotAvailable.visibility = View.VISIBLE
                                binding.tvNotAvailable.text = resources.getString(R.string.no_today_deal)
                            }

                        }else{
                            commonMethods.showToast(this,topDealModel.statusMessage)
                        }
                    }
                    Enums.REQ_TOP_SELLER -> {
                        topSellerModel =commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (topSellerModel.success){
                            if (topSellerModel.data!!.size > 0) {
                                binding.rvTopSelling.visibility = View.VISIBLE
                                binding.rvTopDeals.visibility = View.GONE
                                binding.tvNotAvailable.visibility = View.GONE
                                initTopSellerRecyclerView()
                            }else{
                                binding.rvTopSelling.visibility = View.GONE
                                binding.rvTopDeals.visibility = View.GONE
                                binding.tvNotAvailable.visibility = View.VISIBLE
                                binding.tvNotAvailable.text = resources.getString(R.string.no_top_selling_products)
                            }
                        }else{
                            commonMethods.showToast(this,topSellerModel.statusMessage)
                        }
                    }
                }

            }else{
                commonMethods.hideProgressDialog()
            }

        })


    }

    private fun initTopSellerRecyclerView() {
        binding.rvTopSelling.layoutManager = GridLayoutManager(this, 2)
        val featuredProductsAdapter =
            FeaturedProductsAdapter(this,topSellerModel)
        binding.rvTopSelling.adapter = featuredProductsAdapter
    }

    private fun initTopDealRecyclerView() {
        binding.rvTopDeals.layoutManager = GridLayoutManager(this, 2)
        val featuredProductsAdapter =
            FeaturedProductsAdapter(this,topDealModel)
        binding.rvTopDeals.adapter = featuredProductsAdapter
    }
}