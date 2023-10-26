package cliqbuy.ui.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import cliqbuy.R
import cliqbuy.common.CommonFragment
import cliqbuy.databinding.FragmentHomeBinding
import cliqbuy.helper.Enums
import cliqbuy.model.CommonHomeModel
import cliqbuy.ui.adapter.FeaturedCategoriesAdapter
import cliqbuy.ui.adapter.FeaturedProductsAdapter
import cliqbuy.ui.adapter.HomeTopBannerAdapter
import cliqbuy.ui.view.*
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.util.*

class HomeFragment : CommonFragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewPager: ViewPager
    lateinit var dotsIndicator: DotsIndicator
    lateinit var bannerAdapter: HomeTopBannerAdapter
    lateinit var mActivity : HomeActivity


    //Models
    lateinit var sliderModel : CommonHomeModel
    lateinit var categoryModel : CommonHomeModel
    lateinit var productsModel : CommonHomeModel

    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 500 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewPager = binding.vpTopBanner
        dotsIndicator = binding.diTopBanner
        initViewModel()
        initApiResponseHandler()
        sliderApiCall()
        categoriesApiCall()
        productsApiCall()
        binding.lltTopSellers.setSafeOnClickListener { callTopDealAndSeller(0) }
        binding.lltTopDeal.setSafeOnClickListener { callTopDealAndSeller(1) }
        binding.lltFlashDeals.setSafeOnClickListener { callFlashDealActivity() }
        binding.llCategory.setSafeOnClickListener { mActivity.callCategoriesFragemnt("0") }
        binding.lltBrands.setOnClickListener { callHomeSearchActivity("brands") }
        binding.rltSearch.setOnClickListener { callHomeSearchActivity("product") }
        return binding.root
    }

    private fun callFlashDealActivity() {
        val intent = Intent(requireContext(), FlashDealActivity::class.java)
        val animation = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.ub__slide_in_right, R.anim.ub__slide_out_left).toBundle()
        startActivity(intent, animation)
    }
    private fun callHomeSearchActivity(Name :String) {
        val intent = Intent(requireContext(), HomeSearchActivity::class.java)
        intent.putExtra("type",Name)
        val animation = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.ub__slide_in_right, R.anim.ub__slide_out_left).toBundle()
        startActivity(intent, animation)
    }

    private fun callTopDealAndSeller(type:Int){
        var intent = Intent(requireContext(), SellersAndDealActivity::class.java)
        intent.putExtra("type",type)
        val animation = ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun categoriesApiCall() {
        commonViewModel!!.apiRequest(Enums.REQ_HOME_CATEGORIES,commonHashMap,requireContext())
    }

    private fun productsApiCall() {
        commonMethods.hideProgressDialog()
        commonMethods.showProgressDialog(requireActivity())
        commonViewModel!!.apiRequest(Enums.REQ_HOME_PRODUCTS,commonHashMap,requireContext())
    }

    private fun sliderApiCall() {

        commonViewModel!!.apiRequest(Enums.REQ_SLIDERS,commonHashMap,requireContext())
    }

    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(requireActivity(), androidx.lifecycle.Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_SLIDERS -> {
                        sliderModel =commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (sliderModel.success){
                            initViewPager()
                            initViewPagerAnimation()
                        }else{
                            commonMethods.showToast(requireActivity(),sliderModel.statusMessage)
                        }
                    }

                    Enums.REQ_HOME_CATEGORIES -> {
                        categoryModel =commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (categoryModel.success){
                            initCategoriesRecyclerView()
                        }else{
                            commonMethods.showToast(requireActivity(),categoryModel.statusMessage)
                        }
                    }
                    Enums.REQ_HOME_PRODUCTS -> {
                        commonMethods.hideProgressDialog()
                        productsModel =commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (productsModel.success){
                            initProductsRecyclerView()
                        }else{
                            commonMethods.showToast(requireActivity(),productsModel.statusMessage)
                        }
                    }
                }

            }else{
                commonMethods.hideProgressDialog()
            }

        })


    }

    private fun initViewPager() {
        if(sliderModel.data!!.size > 0) {
            viewPager.visibility = View.VISIBLE
            bannerAdapter = HomeTopBannerAdapter(mActivity, sliderModel)
            viewPager.adapter = bannerAdapter
            bannerAdapter.notifyDataSetChanged()
            dotsIndicator.setViewPager(viewPager)
        }else{
            viewPager.visibility = View.GONE
        }
    }

    private fun initCategoriesRecyclerView(){
        binding.rvFeaturedCat.layoutManager =  LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL, false)
        val featuredCategoriesAdapter =
            FeaturedCategoriesAdapter(mActivity,categoryModel)
        binding.rvFeaturedCat.adapter = featuredCategoriesAdapter

    }

    private fun initProductsRecyclerView(){
        binding.rvFeaturedProducts.layoutManager =GridLayoutManager(mActivity, 2)
        val featuredProductsAdapter =
            FeaturedProductsAdapter(mActivity,productsModel)
        binding.rvFeaturedProducts.adapter = featuredProductsAdapter

    }

    private fun initViewPagerAnimation(){
        //Viewpager Slide animation
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == sliderModel.data!!.size) {
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

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity as HomeActivity
    }

    override fun onResume() {
        super.onResume()

        (activity as HomeActivity).homeFragmentchanges()
    }
}