package cliqbuy.ui.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityFlashDealBinding
import cliqbuy.helper.CommonKeys.dealId
import cliqbuy.helper.CommonKeys.dealName
import cliqbuy.helper.CommonKeys.timeAdder
import cliqbuy.helper.Enums.REQ_FLASH_DEAL
import cliqbuy.model.CommonHomeModel
import cliqbuy.ui.adapter.FlashDealAdapter
import cliqbuy.utils.showToast

class FlashDealActivity : CommonActivity(),FlashDealAdapter.ItemOnClickListener {


    lateinit var binding: ActivityFlashDealBinding
    lateinit var flashModel:CommonHomeModel
    private var selectedDealId = 1
    private var selectedDealName = ""
    private val currentMilliSec = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashDealBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.commonHeader.ivBack.visibility = View.VISIBLE
        binding.commonHeader.tvTitle.text = resources.getString(R.string.flash_deals)
        binding.commonHeader.ivBack.setSafeOnClickListener { onBackPressed() }
        initViewModel()
        initApiResponseHandler()
        getFlashDeal()
    }

    private fun getFlashDeal() {

        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_FLASH_DEAL,commonHashMap,this)
    }


    private fun initRecyclerView() {

        binding.rvFlashDeal.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val flashDealAdapter = FlashDealAdapter(this,flashModel,this)
        binding.rvFlashDeal.adapter = flashDealAdapter

    }

    override fun onItemClick(pos: Int) {
         selectedDealId = flashModel.data!![pos].id
         selectedDealName= flashModel.data!![pos].title

        if ((flashModel.data!![pos].flashDealTimer.toString()+timeAdder).toLong() <= currentMilliSec) this.showToast(resources.getString(R.string.flash_ended))
        else callViewDeal()


      }

    private fun callViewDeal() {

        val intent = Intent(this, ViewFlashDealActivity::class.java)
        intent.also {
            it.putExtra(dealId,selectedDealId.toString())
            it.putExtra(dealName,selectedDealName)}
        val animation = ActivityOptions.makeCustomAnimation(this, R.anim.ub__slide_in_right, R.anim.ub__slide_out_left).toBundle()
        startActivity(intent, animation)
    }

    private fun initApiResponseHandler() {

        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel!!.apiResponseHandler!!.isSuccess) {
                commonMethods.hideProgressDialog()
                when (commonViewModel!!.responseCode) {

                    REQ_FLASH_DEAL->{
                        flashModel = commonViewModel!!.liveDataResponse.value as CommonHomeModel
                        if (flashModel.data!!.size>0) initRecyclerView() else binding.tvNoDeals.visibility =View.VISIBLE
                    }
                }
            }


            })
    }

}