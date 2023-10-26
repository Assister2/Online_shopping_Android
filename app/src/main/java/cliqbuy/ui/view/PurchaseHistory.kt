package cliqbuy.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityPurchaseHistoryBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.model.PurchaseHistoryModel
import cliqbuy.ui.adapter.PurchaseHistoryAdapter
import cliqbuy.utils.PaginationLinearLayoutScrollListener
import java.util.HashMap

class PurchaseHistory : CommonActivity(),AdapterView.OnItemSelectedListener {

    lateinit var binding: ActivityPurchaseHistoryBinding
    lateinit var purchaseHistoryModel: PurchaseHistoryModel
    lateinit var purchaseHistoryAdapter: PurchaseHistoryAdapter
    var currentPage = 1
    var deliveryStatus = ""
    var paymentStatus = ""
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES = 0
    lateinit var linearLayoutManager: LinearLayoutManager
    var purchase =ArrayList<PurchaseHistoryModel.Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        binding.header.ivBack.visibility = View.VISIBLE
        binding.header.ivBack.setSafeOnClickListener { onBackPressed() }
        binding.header.tvTitle.text = resources.getString(R.string.purchase_history)
        initApiResponseHandler()
        initPaymentSpinner()
        initDeliveryStatusSpinner()

        binding.swipeToRefresh.setOnRefreshListener {
            currentPage = 1
            deliveryStatus = ""
            paymentStatus = ""
            purchase.clear()
            isLoading=false
            isLastPage = false
            binding.swipeToRefresh.isRefreshing = false
            initPaymentSpinner()
            initDeliveryStatusSpinner()
            getPurchaseHistory(deliveryStatus,paymentStatus,currentPage)
        }
    }

    private fun getPurchaseHistory(deliveryStatus: String, paymentStatus: String,page:Int) {
        commonMethods.hideProgressDialog()
        commonMethods.showProgressDialog(this)
        val commonHashMap: HashMap<String, String> = HashMap()
        commonHashMap["delivery_status"] = deliveryStatus
        commonHashMap["payment_status"] = paymentStatus
        commonHashMap["page"] =page.toString()
        commonViewModel!!.apiRequest(Enums.REQ_PURCHASE_HISTORY,commonHashMap,this)
    }

    private fun initApiResponseHandler() {
        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_PURCHASE_HISTORY -> {
                        purchaseHistoryModel = commonViewModel!!.liveDataResponse.value as PurchaseHistoryModel
                        commonMethods.hideProgressDialog()
                        if (purchaseHistoryModel.success!!){
                            if (purchaseHistoryModel.data.isEmpty()){
                                binding.rltNoData.visibility = View.VISIBLE
                                binding.rvPurchaseHistory.visibility = View.GONE
                            }else{
                                binding.rltNoData.visibility = View.GONE
                                binding.rvPurchaseHistory.visibility = View.VISIBLE
                                TOTAL_PAGES = purchaseHistoryModel.meta!!.lastPage!!
                                intiPurchaseRecyclerView()
                            }


                        }else{
                            commonMethods.showToast(this,resources.getString(R.string.please_try_again))
                        }
                    }

                }
            }else{
                commonMethods.hideProgressDialog()
            }

        })
    }

    private fun intiPurchaseRecyclerView() {

        if(currentPage>1){
            purchase.addAll(purchaseHistoryModel.data)
            purchaseHistoryAdapter.notifyDataSetChanged()
        }else{
            purchase= purchaseHistoryModel.data
            linearLayoutManager =  LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
            binding.rvPurchaseHistory.layoutManager = linearLayoutManager
            purchaseHistoryAdapter =
                PurchaseHistoryAdapter(this,purchase)
            binding.rvPurchaseHistory.adapter = purchaseHistoryAdapter

        }




        binding.rvPurchaseHistory.addOnScrollListener(object : PaginationLinearLayoutScrollListener(linearLayoutManager){
            override fun loadMoreItems() {
                if (purchaseHistoryModel.meta!!.lastPage!! > 1){
                    this@PurchaseHistory.isLoading = true
                    currentPage += 1
                    getPurchaseHistory(deliveryStatus,paymentStatus,currentPage)
                }

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


    fun initPaymentSpinner(){
        val paymentList = arrayOf(getString(R.string.all),getString(R.string.paid_),getString(R.string.unpaid_))
        binding.paymentSpinner.onItemSelectedListener = this@PurchaseHistory
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.paymentSpinner.adapter = aa
    }

    fun initDeliveryStatusSpinner(){
        val deliveryList = arrayOf(getString(R.string.all), getString(R.string.confirmed), getString(R.string.on_the_way),getString(R.string.delivered))
        binding.deliverySpinner.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, deliveryList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.deliverySpinner.adapter = aa
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0!!.selectedItem.equals(getString(R.string.all))){
           if (p0 == binding.deliverySpinner)
               deliveryStatus = ""
            else
                paymentStatus = ""
        }else if (p0.selectedItem.equals(getString(R.string.paid_))){
            paymentStatus = "paid"
        }else if (p0.selectedItem.equals(getString(R.string.unpaid_))){
            paymentStatus = "unpaid"
        }else if (p0.selectedItem.equals(getString(R.string.confirmed))){
            deliveryStatus = "confirmed"
        }else if (p0.selectedItem.equals(getString(R.string.on_the_way))){
            deliveryStatus = "on_the_way"
        }else if (p0.selectedItem.equals(getString(R.string.delivered))){
            deliveryStatus = "delivered"
        }
        currentPage = 1
        purchase.clear()
        isLastPage = false
        isLoading = false
        getPurchaseHistory(deliveryStatus,paymentStatus,currentPage)


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType,4) }
        startActivity(intent)
        overridePendingTransition(R.anim.cb_fade_in, R.anim.cb_face_out)
    }
}