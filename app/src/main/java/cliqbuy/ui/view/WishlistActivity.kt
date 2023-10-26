package cliqbuy.ui.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityWishListBinding
import cliqbuy.helper.Enums.REQ_DELETE_WISHLIST_ITEM
import cliqbuy.helper.Enums.REQ_USER_WISHLIST
import cliqbuy.model.WishlistModel
import cliqbuy.ui.adapter.WishListAdapter
import cliqbuy.utils.showToast
import java.util.HashMap

class WishlistActivity : CommonActivity(),WishListAdapter.DeleteItemOnClickListener,WishListAdapter.OnClickListener {


    lateinit var binding: ActivityWishListBinding
    lateinit var wishlistModel:WishlistModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.commonHeader.ivBack.visibility = View.VISIBLE
        binding.commonHeader.tvTitle.text = resources.getString(R.string.my_wishlist)
        binding.commonHeader.ivBack.setSafeOnClickListener { onBackPressed() }
        binding.swipeToRefresh.setOnRefreshListener {
            getUserWishlistData()
            binding.swipeToRefresh.isRefreshing = false
        }

        initViewModel()

        initApiResponseHandler()

        if (sessionManager.accessToken!!.isNotEmpty()) getUserWishlistData()

    }

    private fun initApiResponseHandler() {
        commonViewModel!!.liveDataResponse.observe(this, Observer {

            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                commonMethods.hideProgressDialog()
                when (commonViewModel!!.responseCode) {
                    REQ_USER_WISHLIST->{ wishlistModel = commonViewModel!!.liveDataResponse.value as WishlistModel

                        if (wishlistModel.success){
                            if (wishlistModel.data!!.size>0) initRecyclerView() else binding.rltEmptyWishlist.visibility =View.VISIBLE }
                    }
                    REQ_DELETE_WISHLIST_ITEM->{wishlistModel= commonViewModel!!.liveDataResponse.value as WishlistModel

                        if (wishlistModel.result) {
                            this.showToast(wishlistModel.successMsg!!)
                            getUserWishlistData()
                        }


                    }
                }
            }else{
                commonMethods.hideProgressDialog()
            }

            })
    }

    private fun getUserWishlistData() {
        commonMethods.showProgressDialog(this)
        commonViewModel!!.apiRequest(REQ_USER_WISHLIST,commonHashMap,this)
    }

    override fun onResume() {
        super.onResume()
    }
    private fun initRecyclerView() {
        binding.lltWishlist.visibility = View.VISIBLE
        binding.rvWishlistItems.layoutManager =  LinearLayoutManager(this)
        val wishListAdapter = WishListAdapter(this,wishlistModel,this,this)
        binding.rvWishlistItems.adapter = wishListAdapter
    }


    override fun onItemDelete(pos: Int) {

        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_common, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)

        val tvMessage = dialogView.findViewById<View>(R.id.tv_message) as TextView
        val btnOk = dialogView.findViewById<View>(R.id.btn_ok) as Button
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel) as Button

        tvMessage.text = resources.getString(R.string.remove_wishlist)


        btnOk.setSafeOnClickListener {
            val selectItem =wishlistModel.data[pos].id.toString()
            removeWishlistItem(selectItem)
            alertDialog.dismiss() }

        btnCancel.setSafeOnClickListener { alertDialog.dismiss() }

        alertDialog.show()


    }

    private fun removeWishlistItem(selectItem: String) {

        val deleteItemHash: HashMap<String, String> = HashMap()
        deleteItemHash["id"]=selectItem

        commonViewModel!!.apiRequest(REQ_DELETE_WISHLIST_ITEM,deleteItemHash,this)
    }

    override fun onItemClick(pos: Int) {

        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("id", wishlistModel.data!![pos].product!!.id.toString())
        val animation = ActivityOptions.makeCustomAnimation(this, R.anim.ub__slide_in_right, R.anim.ub__slide_out_left).toBundle()
        this.startActivity(intent, animation)
    }

}