package cliqbuy.ui.view

import android.app.ActivityOptions
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.icu.lang.UProperty.INT_START
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityProductDetailBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.Enums
import cliqbuy.model.CommonDataModel
import cliqbuy.model.CommonHomeModel
import cliqbuy.model.ProductDataModel
import cliqbuy.model.ShipEngineServiceModel
import cliqbuy.ui.adapter.FeaturedProductsAdapter
import cliqbuy.ui.adapter.ProductPreviewAdapter
import cliqbuy.utils.gone
import cliqbuy.utils.showToast
import cliqbuy.utils.visible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ProductDetailActivity : CommonActivity(), ProductPreviewAdapter.onProductClickListerner {

    lateinit var binding: ActivityProductDetailBinding
    private var showMoreCliked = false
    private var chatEnabled = false
    private var isWishList = false
    private var isBuyNowClick = false

    lateinit var productDataModel: ProductDataModel.Data
    lateinit var productsRelatedModel: CommonHomeModel
    lateinit var topSellingModel: CommonHomeModel
    lateinit var wishListModel: CommonHomeModel
    var productId: String = ""
    lateinit var productPreviewAdapter: ProductPreviewAdapter
    var shipEstimatorDialog: AlertDialog? = null
    var shipServiceDialog: AlertDialog? = null

    var etZipcode: EditText? = null
    var etQuantity: EditText? = null


    override fun onStart() {
        super.onStart()
        callProductsApi()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initApiResponseHandler()
        getIntentValues()

        //  callProductsApi()
        callProductRelatedApi()

        if (!sessionManager.accessToken.isNullOrEmpty() && sessionManager.userId != "0" && sessionManager.userId != "")
            callWishListCheckApi()

        binding.header.ivBack.setSafeOnClickListener { onBackPressed() }
        binding.header.ivShare.setSafeOnClickListener { shareAndCopyDialog() }

        binding.swipeToRefresh.setOnRefreshListener {
            callProductsApi()
            callProductRelatedApi()

            if (!sessionManager.accessToken.isNullOrEmpty() && sessionManager.userId != "0" && sessionManager.userId != "")
                callWishListCheckApi()
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.ivFavourite.setSafeOnClickListener {
            if (!sessionManager.accessToken.isNullOrEmpty() && sessionManager.userId != "0" && sessionManager.userId != "")
                callWishListAddRemoveApi(isWishList)
            else
                commonMethods.showToast(this, resources.getString(R.string.you_need_login))
        }

        binding.btnAddToCart.setSafeOnClickListener {
            if (!sessionManager.accessToken.isNullOrEmpty() && sessionManager.userId != "0" && sessionManager.userId != "") {
                isBuyNowClick = false
                callAddtoCartApi()
            } else
                commonMethods.showToast(this, resources.getString(R.string.you_need_login))
        }

        binding.btnBuyNow.setSafeOnClickListener {
            if (!sessionManager.accessToken.isNullOrEmpty() && sessionManager.userId != "0" && sessionManager.userId != "") {
                isBuyNowClick = true
                callAddtoCartApi()
            } else
                commonMethods.showToast(this, resources.getString(R.string.you_need_login))
        }



        binding.rltShipEstimator.setOnClickListener {
            showShipEstimatorPopup()
        }

        binding.tvDownload.setSafeOnClickListener { initDownloadPDF() }

    }

    fun showShipEstimatorPopup() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_shipestimator, null)
        dialogBuilder.setView(dialogView)
        shipEstimatorDialog = dialogBuilder.create()
        shipEstimatorDialog!!.setCancelable(true)
        val btClose = dialogView.findViewById<View>(R.id.btn_close) as Button
        val btnADd = dialogView.findViewById<View>(R.id.btn_add) as Button
        etQuantity = dialogView.findViewById<View>(R.id.edt_quantity) as EditText
        etZipcode = dialogView.findViewById<View>(R.id.edt_zipcode) as EditText

        btnADd.setOnClickListener {

            when {
                etQuantity?.text.toString().trim().isEmpty() -> {
                    baseContext.showToast(getString(R.string.quantity_error))
                    return@setOnClickListener
                }

                etZipcode?.text.toString().trim().isEmpty() -> {
                    baseContext.showToast(getString(R.string.zipcode_error))
                    return@setOnClickListener
                }
            }

            getShipEngineServices(
                etQuantity?.text.toString().trim(),
                etZipcode?.text.toString().trim()
            )


        }

        btClose.setSafeOnClickListener { shipEstimatorDialog?.dismiss() }

        shipEstimatorDialog?.show()
    }

    fun getShipEngineServices(quantity: String, zipcode: String) {
        commonMethods.showProgressDialog(this)

        commonViewModel!!.apiRequest(
            Enums.REQ_SHIPENGINE_SERVICES,
            hashMapOf<String, String>().apply {
                put("ship_quantity", quantity)
                put("from_zip_code", zipcode)
                put("product_id", productDataModel.id.toString())
                put("user_id", sessionManager.userId.toString())
            },
            this
        )
    }


    private fun getIntentValues() {
        if (intent.hasExtra("id")) {
            productId = intent.getStringExtra("id")!!
        }
    }

    private fun callProductsApi() {
        commonMethods.showProgressDialog(this)
        var productHashMap: HashMap<String, String> = HashMap()
        productHashMap["id"] = productId
        productHashMap["user_id"] = sessionManager.userId.toString()
        commonViewModel!!.apiRequest(Enums.REQ_PRODUCTS, productHashMap, this)
    }

    private fun callProductRelatedApi() {
        var productHashMap: HashMap<String, String> = HashMap()
        productHashMap["id"] = productId
        commonViewModel!!.apiRequest(Enums.REQ_PRODUCT_RELATED, productHashMap, this)
    }

    private fun callTopSellingApi() {
        var productHashMap: HashMap<String, String> = HashMap()
        productHashMap["id"] = productDataModel.shopId.toString()
        commonViewModel!!.apiRequest(Enums.REQ_SELLER_TOP_SELLING, productHashMap, this)
    }

    private fun callWishListCheckApi() {
        var productHashMap: HashMap<String, String> = HashMap()
        productHashMap["product_id"] = productId
        productHashMap["user_id"] = sessionManager.userId.toString()
        commonViewModel!!.apiRequest(Enums.REQ_WISHLIST_CHECK, productHashMap, this)
    }

    private fun callWishListAddRemoveApi(isWhislist: Boolean) {
        var productHashMap: HashMap<String, String> = HashMap()
        productHashMap["product_id"] = productId
        productHashMap["user_id"] = sessionManager.userId.toString()
        if (isWhislist)
            commonViewModel!!.apiRequest(Enums.REQ_WISHLIST_REMOVE, productHashMap, this)
        else
            commonViewModel!!.apiRequest(Enums.REQ_WISHLIST_ADD, productHashMap, this)
    }

    private fun callAddtoCartApi() {
        var productHashMap: HashMap<String, String> = HashMap()
        productHashMap["id"] = productId
        productHashMap["variant"] = ""
        productHashMap["cost_matrix"] = CommonKeys.identifyMatrix
        productHashMap["user_id"] = sessionManager.userId.toString()
        productHashMap["quantity"] = binding.productprice.tvQuantityValue.text.toString()
        commonViewModel!!.apiRequest(Enums.REQ_ADD_TO_CART, productHashMap, this)
    }

    override fun moveAddressScreen() {
        Intent(this, AddressDetailsActivity::class.java).also {
            it.putExtra("comeFom", "productdetail")
            startActivity(it)
        }
    }

    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(this, Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {

                when (commonViewModel!!.responseCode) {
                    Enums.REQ_PRODUCTS -> {
                        commonMethods.hideProgressDialog()
                        val productsDataModel =
                            commonViewModel?.liveDataResponse?.value as (ProductDataModel)

                        if (productsDataModel.success) {
                            chatEnabled = productsDataModel.isChatEnabled
                            if (productsDataModel.data.isNotEmpty()) productDataModel =
                                productsDataModel.data[0]

                            if (productDataModel.isMerchantEnabledShipEngine
                                && productDataModel.isShipEngineEnabled.equals("1")
                                && productDataModel.isUserNeedAddress.not()
                            )
                                moveAddressScreen()
                            else
                                onSuccesssProducts()
                        } else {
                            commonMethods.showToast(
                                this,
                                resources.getString(R.string.please_try_again)
                            )
                        }
                    }

                    Enums.REQ_PRODUCT_RELATED -> {
                        productsRelatedModel =
                            commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (productsRelatedModel.success) {
                            if (productsRelatedModel.data!!.size > 0) {
                                binding.rvSimiliarProducts.visibility = View.VISIBLE
                                binding.tvNorelatedProducts.visibility = View.GONE
                                initProductsRecyclerView()
                            } else {
                                binding.rvSimiliarProducts.visibility = View.GONE
                                binding.tvNorelatedProducts.visibility = View.VISIBLE
                            }
                        } else {
                            binding.rvSimiliarProducts.visibility = View.GONE
                            binding.tvNorelatedProducts.visibility = View.VISIBLE
                            //commonMethods.showToast(this,resources.getString(R.string.please_try_again))
                        }
                    }

                    Enums.REQ_SELLER_TOP_SELLING -> {
                        topSellingModel =
                            commonViewModel?.liveDataResponse?.value as (CommonHomeModel)

                        if (topSellingModel.success) {
                            if (topSellingModel.data!!.size > 0) {
                                binding.rvTopselling.visibility = View.VISIBLE
                                binding.tvNotopselling.visibility = View.GONE
                                initTopSellingRecyclerView()
                            } else {
                                binding.rvTopselling.visibility = View.GONE
                                binding.tvNotopselling.visibility = View.VISIBLE
                            }
                        } else {
                            binding.rvTopselling.visibility = View.GONE
                            binding.tvNotopselling.visibility = View.VISIBLE
                            //commonMethods.showToast(this,resources.getString(R.string.please_try_again))
                        }
                    }

                    Enums.REQ_WISHLIST_ADD -> {
                        wishListModel =
                            commonViewModel?.liveDataResponse?.value as (CommonHomeModel)
                        binding.ivFavourite.setImageResource(R.drawable.ic_wishlist_selected)
                        isWishList = wishListModel.isInWishlist
                        commonMethods.showToast(this, wishListModel.statusMessage)
                    }

                    Enums.REQ_WISHLIST_CHECK -> {
                        wishListModel =
                            commonViewModel?.liveDataResponse?.value as (CommonHomeModel)
                        if (wishListModel.isInWishlist) {
                            isWishList = wishListModel.isInWishlist
                            binding.ivFavourite.setImageResource(R.drawable.ic_wishlist_selected)
                        } else {
                            isWishList = wishListModel.isInWishlist
                            binding.ivFavourite.setImageResource(R.drawable.ic_wishlist_unselected)
                        }
                    }

                    Enums.REQ_WISHLIST_REMOVE -> {
                        wishListModel =
                            commonViewModel?.liveDataResponse?.value as (CommonHomeModel)
                        binding.ivFavourite.setImageResource(R.drawable.ic_wishlist_unselected)
                        isWishList = wishListModel.isInWishlist
                        commonMethods.showToast(this, wishListModel.statusMessage)
                    }

                    Enums.REQ_ADD_TO_CART -> {
                        val addToCartModel =
                            commonViewModel?.liveDataResponse?.value as (CommonDataModel)

                        if (addToCartModel.result) {
                            if (isBuyNowClick) {
                                isBuyNowClick = false
                                callCartPage()
                            } else
                                showSnackBar()
                        } else {
                            commonMethods.showToast(this, addToCartModel.message)
                        }

                    }

                    Enums.REQ_SHIPENGINE_SERVICES -> {
                        commonMethods.hideProgressDialog()

                        val shipEngineServiceModel =
                            commonViewModel?.liveDataResponse?.value as (ShipEngineServiceModel)

                        if (shipEngineServiceModel.status ?: false)
                            showShipServicePopup(shipEngineServiceModel)
                        else {
                            commonMethods.showToast(this, resources.getString(R.string.invalid_zip))
                        }

                    }
                }

            } else {
                commonMethods.hideProgressDialog()
            }

        })


    }

    private fun callCartPage() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType, 3) }
        intent.also { it.putExtra("redirect", 1) }
        intent.also { it.putExtra("id", productId) }
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun showSnackBar() {
        val snackbar = Snackbar.make(
            binding.lltCart,
            resources.getString(R.string.added_cart),
            Snackbar.LENGTH_LONG
        )
            .setBackgroundTint(resources.getColor(R.color.colorPrimary))
            .setTextColor(resources.getColor(R.color.bg_window))
            .setActionTextColor(resources.getColor(R.color.txt_sixth))
            .setAction(resources.getString(R.string.show_cart))
            {
                callCartPage()
            }

        snackbar.show()
    }

    private fun initProductsRecyclerView() {
        binding.rvSimiliarProducts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val featuredProductsAdapter =
            FeaturedProductsAdapter(this, productsRelatedModel)
        binding.rvSimiliarProducts.adapter = featuredProductsAdapter

    }

    private fun initTopSellingRecyclerView() {
        binding.rvTopselling.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val featuredProductsAdapter =
            FeaturedProductsAdapter(this, topSellingModel)
        binding.rvTopselling.adapter = featuredProductsAdapter

    }


    private fun onSuccesssProducts() {

        try {
            commonMethods.hideProgressDialog()

            if (productDataModel.isMerchantEnabledShipEngine && productDataModel.shippingType && productDataModel.isShipEngineEnabled.equals("1")) {
                binding.groupShipengine.visibility = View.VISIBLE
                binding.tvShippingFee.text =
                    getString(R.string.shipping) + " " + productDataModel.shippingEngineLowestPrice

                val calendar: Calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, productDataModel.deliveryDays)
                val format = SimpleDateFormat("E , LLL dd")
                var dateStr = format.format(calendar.getTime()) + "th"
                binding.tvShipdate.text = dateStr

                Glide.with(this@ProductDetailActivity).load(productDataModel.shipEngineIamge)
                    .into(binding.icShipengine)

            } else {
                binding.groupShipengine.visibility = View.GONE

            }


            callTopSellingApi()

            binding.header.tvTitle.text = productDataModel.mainPrice
            binding.tvProductsName.text = productDataModel.name

            if (productDataModel.photos[0].path.isEmpty()) Picasso.get()
                .load(R.drawable.ic_empty_cart_200).into(binding.ivProductsImage)
            else Picasso.get()
                .load(resources.getString(R.string.imageUrl) + productDataModel.photos[0].path)
                .placeholder(
                    R.drawable.ic_empty_cart_200
                ).into(binding.ivProductsImage)
            initPreviewProducts()

            if (productDataModel.pdfUrl.isNotEmpty())
                binding.tvDownload.visible()
            else
                binding.tvDownload.gone()

            if (productDataModel.rating != 0) {
                binding.rbProductRating.visibility = View.VISIBLE
                binding.tvRating.visibility = View.VISIBLE
                binding.rbProductRating.rating = productDataModel.rating.toFloat()
                binding.tvRating.text = "(" + productDataModel.ratingCount.toString() + ")"

            } else {
                binding.rbProductRating.visibility = View.GONE
                binding.tvRating.visibility = View.GONE
            }
            if (productDataModel.hasDiscount) {
                binding.productprice.tvOfferPrice.visibility = View.VISIBLE
                binding.productprice.tvOfferPrice.paintFlags =
                    binding.productprice.tvOfferPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.productprice.tvOfferPrice.text = productDataModel.strokedPrice
            } else {
                binding.productprice.tvOfferPrice.visibility = View.GONE
            }
            binding.productprice.tvPriceValue.text =
                productDataModel.mainPrice + " /" + productDataModel.unit


            if (productDataModel.currentStock > 0) {
                binding.productprice.cgGroup.visibility = View.VISIBLE
                binding.lltCart.visibility = View.VISIBLE
                binding.productprice.tvOutOfStock.visibility = View.GONE
                if (productDataModel.isShowStockQty) {
                    binding.productprice.available.visibility = View.VISIBLE
                    binding.productprice.available.text =
                        "( " + productDataModel.currentStock.toString() + " " + resources.getString(
                            R.string.available
                        ) + " )"
                } else if (productDataModel.isShowStockWithText) {
                    binding.productprice.available.visibility = View.VISIBLE
                    binding.productprice.available.text =
                        "( " + resources.getString(R.string.in_stock) + " )"
                } else if (productDataModel.isHideStock) {
                    binding.productprice.available.visibility = View.GONE
                }
            } else {
                binding.productprice.cgGroup.visibility = View.GONE
                binding.lltCart.visibility = View.GONE
                binding.productprice.tvOutOfStock.visibility = View.VISIBLE
            }


            binding.productprice.tvQuantityValue.text =
                productDataModel.minimumPurchaseQty.toString()

            binding.tvShopName.text = productDataModel.shopName

            if (chatEnabled) {
                binding.tvChat.visibility = View.GONE
            } else {
                binding.tvChat.visibility = View.GONE
            }

            binding.tvChat.setOnClickListener {
                if (!sessionManager.accessToken.isNullOrEmpty() && sessionManager.userId != "0" && sessionManager.userId != "")
                    chatDialog()
                else
                    commonMethods.showToast(this, resources.getString(R.string.you_need_login))
            }
            val calculablePrice =
                productDataModel.calculablePrice * productDataModel.minimumPurchaseQty
            val totalString = String.format(Locale.ENGLISH, "%.2f", calculablePrice)
            println("### -> language  $totalString")
            binding.productprice.tvTotalPrice.text = totalString
            binding.productprice.tvCurrencySymbol.text = productDataModel.currencySymbol
            if (binding.productprice.tvQuantityValue.text.toString()
                    .toInt() == productDataModel.minimumPurchaseQty
            ) {
                binding.productprice.tvMinus.setTextColor(resources.getColor(R.color.txt_four))
                binding.productprice.tvMinus.isEnabled = false
            }
            binding.productprice.tvMinus.setOnClickListener {
                if (binding.productprice.tvQuantityValue.text.toString()
                        .toInt() == productDataModel.minimumPurchaseQty
                ) {
                    binding.productprice.tvMinus.setTextColor(resources.getColor(R.color.txt_four))
                    binding.productprice.tvMinus.isEnabled = false
                } else
                    decrement()
            }
            binding.productprice.tvPlus.setOnClickListener { increment() }

            if (productDataModel.description!!.isEmpty() || productDataModel.description == null || productDataModel.description == "null") {
                binding.tvDescriptionValue.text =
                    resources.getString(R.string.no_description_available)
            } else {
                makeTextViewResizable(binding.tvDescriptionValue, 2, productDataModel.description!!)
            }

            binding.tvShowMoreLess.setOnClickListener {
                showMoreCliked = !showMoreCliked

                when {
                    showMoreCliked -> {
                        binding.tvShowMoreLess.text = getString(R.string.show_less)
                        makeTextViewResizable(
                            binding.tvDescriptionValue,
                            -1,
                            productDataModel.description!!
                        )
                    }

                    else -> {
                        binding.tvShowMoreLess.text = getString(R.string.view_more)
                        makeTextViewResizable(
                            binding.tvDescriptionValue,
                            2,
                            productDataModel.description!!
                        )
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun increment() {
        if (binding.productprice.tvTotalPrice.text.toString()
                .toFloat() >= productDataModel.calculablePrice.toFloat() && binding.productprice.tvQuantityValue.text.toString()
                .toInt() < productDataModel.currentStock
        ) {
            val incrementString = String.format(
                Locale.ENGLISH,
                "%.2f",
                binding.productprice.tvTotalPrice.text.toString()
                    .toFloat() + productDataModel.calculablePrice.toFloat()
            )
            binding.productprice.tvTotalPrice.text = incrementString
            binding.productprice.tvMinus.setTextColor(resources.getColor(R.color.txt_primary))
            binding.productprice.tvMinus.isEnabled = true
            binding.productprice.tvQuantityValue.text =
                (binding.productprice.tvQuantityValue.text.toString().toInt() + 1).toString()
        } else {
            binding.productprice.tvPlus.setTextColor(resources.getColor(R.color.txt_four))
            binding.productprice.tvPlus.isEnabled = false
        }
    }

    fun decrement() {
        if (binding.productprice.tvTotalPrice.text.toString()
                .toFloat() > productDataModel.calculablePrice
        ) {
            val decrementString = String.format(
                Locale.ENGLISH,
                "%.2f",
                binding.productprice.tvTotalPrice.text.toString()
                    .toFloat() - productDataModel.calculablePrice.toFloat()
            )
            binding.productprice.tvTotalPrice.text = decrementString
            binding.productprice.tvPlus.setTextColor(resources.getColor(R.color.txt_primary))
            binding.productprice.tvPlus.isEnabled = true
            binding.productprice.tvQuantityValue.text =
                (binding.productprice.tvQuantityValue.text.toString().toInt() - 1).toString()
            if (binding.productprice.tvQuantityValue.text.toString()
                    .toInt() == productDataModel.minimumPurchaseQty
            ) {
                binding.productprice.tvMinus.setTextColor(resources.getColor(R.color.txt_four))
                binding.productprice.tvMinus.isEnabled = false
            }
        } else {
            binding.productprice.tvMinus.setTextColor(resources.getColor(R.color.txt_four))
            binding.productprice.tvMinus.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()


        //commonMethods.showToast(this,resources.getString(R.string.you_need_login))

    }


    fun initPreviewProducts() {
        productDataModel.photos[0].isSelected = true
        binding.rvPreviewProducts.layoutManager = LinearLayoutManager(this)
        binding.rvPreviewProducts.isNestedScrollingEnabled = true
        productPreviewAdapter = ProductPreviewAdapter(this, productDataModel.photos, this)
        binding.rvPreviewProducts.adapter = productPreviewAdapter
    }

    fun makeTextViewResizable(tv: TextView, maxLine: Int, content: String) {
        tv.text = content
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs = tv.viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (maxLine > 0 && tv.lineCount >= maxLine) {
                    lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    text = tv.text.subSequence(0, lineEndIndex)
                        .toString()
                } else {
                    lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    text = tv.text.subSequence(0, lineEndIndex).toString()
                }
                if (tv.lineCount > 2) {
                    tv.text = text
                    binding.tvShowMoreLess.visibility = View.VISIBLE
                } else {
                    binding.tvShowMoreLess.visibility = View.INVISIBLE
                }
            }
        })
    }


    fun shareAndCopyDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.share_copy_dialog, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        val btnOk = dialogView.findViewById<View>(R.id.btn_ok) as Button
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel) as Button
        val ivClose = dialogView.findViewById<View>(R.id.iv_close) as ImageView

        btnOk.setSafeOnClickListener {
            shareLink()
        }
        ivClose.setSafeOnClickListener {
            alertDialog.dismiss()
        }
        btnCancel.setSafeOnClickListener {
            commonMethods.copyContentToClipboard(this, productDataModel.link)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun chatDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_chat, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        val btnClose = dialogView.findViewById<View>(R.id.btn_close) as Button
        val btnSend = dialogView.findViewById<View>(R.id.btn_send) as Button
        val ivClose = dialogView.findViewById<View>(R.id.iv_close) as ImageView
        val edtTitle = dialogView.findViewById<View>(R.id.tv_title_text) as TextView
        val edtMessage = dialogView.findViewById<View>(R.id.edt_message) as EditText

        edtTitle.text = productDataModel.name


        btnSend.setSafeOnClickListener {
            if (edtMessage.text.isEmpty()) {
                commonMethods.showToast(this, resources.getString(R.string.enter_message))
            } else {
                alertDialog.dismiss()
            }
        }
        ivClose.setSafeOnClickListener {
            alertDialog.dismiss()
        }
        btnClose.setSafeOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun shareLink() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        share.putExtra(Intent.EXTRA_TEXT, productDataModel.link)
        startActivity(Intent.createChooser(share, resources.getString(R.string.share_my_link)))
    }

    private fun download(
        context: Context,
        filename: String,
        fileExtension: String,
        designationDirectory: String,
        url: String,
    ) {
        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(
            context,
            designationDirectory,
            filename + fileExtension
        )
        assert(downloadManager != null)
        downloadManager.enqueue(request)
        Snackbar.make(findViewById(android.R.id.content), "Downloading...", Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(R.color.colorPrimary))
            .setTextColor(resources.getColor(R.color.bg_window)).show()
        Snackbar.make(
            findViewById(android.R.id.content),
            "Download Successfully",
            Snackbar.LENGTH_LONG
        )
            .setBackgroundTint(resources.getColor(R.color.colorPrimary))
            .setTextColor(resources.getColor(R.color.bg_window))
            .show()
    }

    fun initDownloadPDF() {
        val uri = productDataModel.pdfUrl
        download(
            applicationContext,
            "invoice",
            ".pdf",
            "Downloads",
            uri.trim { it <= ' ' })
    }

    override fun onClickProduct(pos: Int) {
        for (i in productDataModel.photos.indices) {
            productDataModel.photos[i].isSelected = false
        }
        productDataModel.photos[pos].isSelected = true
        if (productDataModel.photos[pos].path.isEmpty()) Picasso.get()
            .load(R.drawable.ic_empty_cart_200).into(binding.ivProductsImage)
        else Picasso.get()
            .load(resources.getString(R.string.imageUrl) + productDataModel.photos[pos].path)
            .placeholder(
                R.drawable.ic_empty_cart_200
            ).into(binding.ivProductsImage)
        productPreviewAdapter.notifyDataSetChanged()
    }

    fun showShipServicePopup(shipEngineServiceModel: ShipEngineServiceModel) {
        val dialogBuilder = AlertDialog.Builder(this, cliqbuy.R.style.DialogStyle)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(cliqbuy.R.layout.shipengine_service_layout, null)
        generateTable(dialogView, shipEngineServiceModel)
        dialogView.findViewById<TextView>(R.id.tv_pricing_disclaimer).text =
            boldText(getString(R.string.pricing), getString(R.string.pricing_disclaimer))
        dialogView.findViewById<TextView>(R.id.tv_pricebreakdown_disclaimer).text =
            boldText(
                getString(R.string.pricing_breakdown),
                getString(R.string.pricing_breakdown_disclaimer)
            )
        dialogView.findViewById<TextView>(R.id.tv_quantity).text =
            getString(R.string.quantity_title) + " : " + shipEngineServiceModel.shipQuantity.toString()
        dialogView.findViewById<TextView>(R.id.tv_zipcode).text =
            getString(R.string.destination_zipcode) + " : " + shipEngineServiceModel.destinationZipCode.toString()



        dialogView.findViewById<TextView>(R.id.tv_changevalues).setOnClickListener {
            etZipcode?.setText("")
            etQuantity?.setText("")
            etQuantity?.requestFocus()
            shipServiceDialog?.dismiss()

        }

        dialogView.findViewById<ImageView>(R.id.iv_close).setOnClickListener {
            shipServiceDialog?.dismiss()
            shipEstimatorDialog?.dismiss()
        }

        dialogBuilder.setView(dialogView)
        shipServiceDialog = dialogBuilder.create()
        shipServiceDialog!!.setCancelable(true)
        shipServiceDialog?.show()
    }

    fun generateTable(view: View, shipEngineServiceModel: ShipEngineServiceModel) {
        var tableIds = 1

        val tl: TableLayout = view.findViewById<TableLayout>(R.id.main_table)

        //Setting header
        val tr_head = TableRow(this)
        tr_head.id = ++tableIds
        tr_head.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )


        addColumn(tr_head, getString(R.string.service), ++tableIds, 2)
        addColumn(tr_head, getString(R.string.package_type), ++tableIds, 2)
        addColumn(tr_head, getString(R.string.rate), ++tableIds, 2)
        tl.addView(
            tr_head, TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )
        )

        var i = 0
        for (data in shipEngineServiceModel.data) {

            val tr_head = TableRow(this)
            tr_head.id = ++tableIds

            tr_head.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            var bgvalue = i++ % 2
            addColumn(tr_head, data.serviceType.toString(), ++tableIds, bgvalue)
            addColumn(tr_head, data.packageType.toString(), ++tableIds, bgvalue)
            addColumn(tr_head, data.shippingAmount?.amount.toString(), ++tableIds, bgvalue)

            tl.addView(
                tr_head, TableLayout.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    fun addColumn(tr: TableRow, content: String, id: Int, backgroundId: Int) {
        val params: TableRow.LayoutParams = TableRow.LayoutParams(
            0, TableRow.LayoutParams.MATCH_PARENT
        )
        params.weight = 1.0f

        val tvService = TextView(ContextThemeWrapper(this, R.style.MediumText), null, 0)
        tvService.background = when (backgroundId) {
            1 -> resources.getDrawable(R.drawable.table_border_light)
            0 -> resources.getDrawable(R.drawable.table_border_dark)
            else -> resources.getDrawable(R.drawable.table_border_header)
        }
        tvService.id = id
        tvService.text = content
        tvService.isAllCaps = false
        tvService.gravity = Gravity.CENTER
        tvService.setTextColor(Color.BLACK) // part2
        tvService.setPadding(5, 5, 5, 5)
        tvService.layoutParams = params
        tr.addView(tvService) // add the column to the table row here

    }


    fun boldText(boldTxt: String, normalTxt: String): SpannableStringBuilder {

        var INT_END = boldTxt.length + 3
        val str = SpannableStringBuilder(" • " + boldTxt + " - " + normalTxt)
        str.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            INT_END,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return str

    }

}