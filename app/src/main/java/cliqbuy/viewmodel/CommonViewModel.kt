package cliqbuy.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cliqbuy.R
import cliqbuy.common.CommonMethods
import cliqbuy.configs.AppController
import cliqbuy.helper.Enums
import cliqbuy.helper.Enums.REQ_DELETE_WISHLIST_ITEM
import cliqbuy.helper.Enums.REQ_ADD_TO_CART
import cliqbuy.helper.Enums.REQ_APPLY_COUPON
import cliqbuy.helper.Enums.REQ_CART_INVOICE
import cliqbuy.helper.Enums.REQ_FLASH_DETAILS
import cliqbuy.helper.Enums.REQ_PAYMENT_TYPES
import cliqbuy.helper.Enums.REQ_BRANDS
import cliqbuy.helper.Enums.REQ_SEARCH_PRODUCT
import cliqbuy.helper.Enums.REQ_SELLER
import cliqbuy.helper.Enums.REQ_USER_ACCESS_BY_TOKEN
import cliqbuy.helper.Enums.REQ_USER_WISHLIST
import cliqbuy.helper.Enums.REQ_CART
import cliqbuy.helper.Enums.REQ_CHANGE_LANGUAGE
import cliqbuy.helper.Enums.REQ_FILTER_CATEGORIES
import cliqbuy.helper.Enums.REQ_DELETE_CART
import cliqbuy.helper.Enums.REQ_DELETE_OTP
import cliqbuy.helper.Enums.REQ_DELIVERY_INFO
import cliqbuy.helper.Enums.REQ_FILTER_BRANDS
import cliqbuy.helper.Enums.REQ_MAKE_DEFAULT
import cliqbuy.helper.Enums.REQ_ORDER_BY_CASH
import cliqbuy.helper.Enums.REQ_ORDER_BY_PAYPAL
import cliqbuy.helper.Enums.REQ_PROCEED_CHECKOUT
import cliqbuy.helper.Enums.REQ_REMOVE_COUPON
import cliqbuy.helper.Enums.REQ_SHIPENGINE_SERVICES
import cliqbuy.helper.Enums.REQ_SORT
import cliqbuy.helper.Enums.REQ_UPDATE_CART
import cliqbuy.helper.Enums.REQ_UPDATE_QUANTITY
import cliqbuy.helper.Enums.REQ_UPDATE_SHIPENGINE
import cliqbuy.helper.SessionManager
import cliqbuy.interfaces.ApiResponseCommonInterface
import cliqbuy.interfaces.ApiService
import cliqbuy.network.ApiExceptionHandler
import cliqbuy.network.ApiResponseHandler
import cliqbuy.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class CommonViewModel : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sessionManager: SessionManager


    @Inject
    lateinit var respository: Repository

    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var apiExceptionHandler: ApiExceptionHandler

    lateinit var context: Context
    lateinit var activity: Activity
    var responseCode: Int? = null
    var commonInterface: ApiResponseCommonInterface? = null
    var apiResponseHandler: ApiResponseHandler? = null
    var liveDataResponse = MutableLiveData<Any>()


    fun initAppController(context: Context, activity: Activity) {
        this.context = context
        this.activity = activity
        AppController.appComponent!!.inject(this)

    }

    fun apiRequest(requestCode: Int, hashMap: HashMap<String, String>, context: Context) {
        if (commonMethods.isOnline(context)) {

            CoroutineScope(Dispatchers.IO).launch {
                var response: Response<Any>? = null
                when (requestCode) {
                    Enums.REQ_LOGIN -> {
                        response = respository.login(hashMap) as Response<Any>
                    }

                    Enums.REQ_SOCIAL_LOGIN -> {
                        response = respository.socialLogin(hashMap) as Response<Any>
                    }

                    Enums.REQ_SIGNUP -> {
                        response = respository.register(hashMap) as Response<Any>
                    }

                    Enums.REQ_PICKUP_POINT -> {
                        response = respository.pickupPoint(hashMap) as Response<Any>
                    }

                    Enums.REQ_ADDRESS_LIST -> {
                        response = respository.addressList(hashMap) as Response<Any>
                    }

                    Enums.REQ_PURCHASE_HISTORY -> {
                        response = respository.purchaseHistory(hashMap) as Response<Any>
                    }

                    Enums.REQ_ORDER_DETAILS -> {
                        response =
                            respository.getOrderDetail(hashMap["id"].toString()) as Response<Any>
                    }

                    Enums.REQ_ORDER_ITEM -> {
                        response =
                            respository.getOrderItem(hashMap["id"].toString()) as Response<Any>
                    }

                    Enums.REQ_SHOP_DETAILS -> {
                        response =
                            respository.getShopDetails(hashMap["id"].toString()) as Response<Any>
                    }

                    Enums.REQ_ADDRESS_DELETE -> {
                        response = respository.deleteAddress(hashMap) as Response<Any>
                    }

                    Enums.REQ_ADDRESS_CREATE -> {
                        response = respository.createAddress(hashMap) as Response<Any>
                    }

                    Enums.REQ_ADDRESS_UPDATE -> {
                        response = respository.updateAddress(hashMap) as Response<Any>
                    }

                    Enums.REQ_COUNTRY_LIST -> {
                        response = respository.countryList(hashMap) as Response<Any>
                    }

                    Enums.REQ_STATE_LIST -> {
                        response = respository.stateList(hashMap) as Response<Any>
                    }

                    Enums.REQ_CITY_LIST -> {
                        response = respository.cityList(hashMap) as Response<Any>
                    }

                    Enums.REQ_SHIPPING_COST -> {
                        response = respository.shippingCost(hashMap) as Response<Any>
                    }

                    Enums.REQ_WISHLIST_ADD -> {
                        response = respository.getWishListAdd(hashMap) as Response<Any>
                    }

                    Enums.REQ_WISHLIST_CHECK -> {
                        response = respository.getWishListCheck(hashMap) as Response<Any>
                    }

                    Enums.REQ_WISHLIST_REMOVE -> {
                        response = respository.getWishListRemove(hashMap) as Response<Any>
                    }

                    Enums.REQ_COMMON_DATA -> {
                        response = respository.commonData() as Response<Any>
                    }

                    Enums.REQ_SLIDERS -> {
                        response = respository.slider() as Response<Any>
                    }

                    Enums.REQ_HOME_CATEGORIES -> {
                        response = respository.homeCategory() as Response<Any>
                    }

                    Enums.REQ_HOME_PRODUCTS -> {
                        response = respository.homeProducts() as Response<Any>
                    }

                    Enums.REQ_RESEND_CODE -> {
                        response = respository.resendCode(hashMap) as Response<Any>
                    }

                    Enums.REQ_CONFIRM_CODE -> {
                        response = respository.confirmCode(hashMap) as Response<Any>
                    }

                    Enums.REQ_FORGET_PASSWORD -> {
                        response = respository.forgetPassword(hashMap) as Response<Any>
                    }

                    Enums.REQ_RESET_PASSWORD -> {
                        response = respository.resetPassword(hashMap) as Response<Any>
                    }

                    Enums.REQ_RESEND_PASSWORD_CODE -> {
                        response = respository.resendCodeForPassword(hashMap) as Response<Any>
                    }

                    Enums.REQ_LOGOUT -> {
                        response = respository.logout(hashMap) as Response<Any>
                    }

                    Enums.REQ_UPDATE_PROFILE -> {
                        response = respository.updateProfile(hashMap) as Response<Any>
                    }

                    Enums.REQ_DELETE_PROFILE -> {
                        response = respository.deleteAccount(hashMap) as Response<Any>
                    }

                    Enums.REQ_USER_PROFILE -> {
                        response = respository.userProfile(hashMap) as Response<Any>
                    }

                    Enums.REQ_PRODUCTS -> {
                        response = respository.getProducts(
                            hashMap["id"].toString(),
                            hashMap
                        ) as Response<Any>
                    }

                    Enums.REQ_PRODUCT_RELATED -> {
                        response =
                            respository.getProductRelated(hashMap["id"].toString()) as Response<Any>
                    }

                    Enums.REQ_TOP_SELLING -> {
                        response =
                            respository.getTopSelling(hashMap["id"].toString()) as Response<Any>
                    }

                    Enums.REQ_SELLER_TOP_SELLING -> {
                        response =
                            respository.getSellerTopSelling(hashMap["id"].toString()) as Response<Any>
                    }

                    Enums.REQ_SELLER_NEW_ARRIVALS -> {
                        response =
                            respository.getSellerNewArrival(hashMap["id"].toString()) as Response<Any>
                    }

                    Enums.REQ_SELLER_FEATURED -> {
                        response =
                            respository.getSellerFeatured(hashMap["id"].toString()) as Response<Any>
                    }

                    REQ_USER_ACCESS_BY_TOKEN -> {
                        response = respository.getUserAccessToken(hashMap) as Response<Any>
                    }

                    REQ_ADD_TO_CART -> {
                        response = respository.addToCart(hashMap) as Response<Any>
                    }

                    REQ_PROCEED_CHECKOUT -> {
                        response = respository.proceedCheckOut(hashMap) as Response<Any>
                    }

                    REQ_CART -> {
                        response = respository.getCart(hashMap["id"].toString()) as Response<Any>
                    }

                    REQ_DELETE_CART -> {
                        response = respository.deleteCart(hashMap["id"].toString()) as Response<Any>
                    }

                    REQ_UPDATE_CART -> {
                        response = respository.updateCart(hashMap) as Response<Any>
                    }

                    Enums.REQ_TOP_SELLER -> {
                        response = respository.topSelling(hashMap) as Response<Any>
                    }

                    Enums.REQ_TOP_DEAL -> {
                        response = respository.topDeal(hashMap) as Response<Any>
                    }

                    Enums.REQ_FLASH_DEAL -> {
                        response = respository.flashDeal(hashMap) as Response<Any>
                    }

                    Enums.REQ_VIEW_PRODUCT -> {
                        response = respository.viewProduct(
                            hashMap["id"].toString(),
                            hashMap
                        ) as Response<Any>
                    }

                    Enums.REQ_SELLER_VIEW_PRODUCT -> {
                        response = respository.sellerViewProduct(
                            hashMap["id"].toString(),
                            hashMap
                        ) as Response<Any>
                    }

                    Enums.REQ_VIEW_BRAND -> {
                        response = respository.viewBrand(
                            hashMap["id"].toString(),
                            hashMap
                        ) as Response<Any>
                    }

                    Enums.REQ_CATEGORY -> {
                        response = respository.getCategories(hashMap)
                    }

                    Enums.REQ_TOP_CATEGORY -> {
                        response = respository.getTopCategories()
                    }

                    Enums.REQ_UPDATE_USER_IMAGE -> {
                        response = respository.updateUserImage(hashMap)
                    }

                    REQ_USER_WISHLIST -> {
                        response = respository.userWishlist(hashMap)
                    }

                    REQ_DELETE_WISHLIST_ITEM -> {
                        response = respository.deleteWishlistItem(hashMap["id"].toString())
                    }

                    REQ_FLASH_DETAILS -> {
                        response = respository.flashDealDetails(hashMap["id"].toString())
                    }

                    REQ_CART_INVOICE -> {
                        response = respository.cartSummary(
                            hashMap["id"].toString(),
                            hashMap["default_id"].toString()
                        )
                    }

                    REQ_PAYMENT_TYPES -> {
                        response = respository.getPaymentTypes(hashMap)
                    }

                    REQ_APPLY_COUPON -> {
                        response = respository.applyCoupon(hashMap)
                    }

                    REQ_REMOVE_COUPON -> {
                        response = respository.removeCoupon(hashMap)
                    }

                    REQ_ORDER_BY_CASH -> {
                        response = respository.placeOrderByCash(hashMap)
                    }

                    REQ_ORDER_BY_PAYPAL -> {
                        response = respository.placeOrderByPaypal(hashMap)
                    }

                    REQ_DELETE_OTP -> {
                        response = respository.deleteOtp(hashMap)
                    }

                    REQ_SEARCH_PRODUCT -> {
                        response = respository.getSearchProduct(hashMap)
                    }

                    REQ_SELLER -> {
                        response = respository.SellerPoint(hashMap)
                    }

                    REQ_BRANDS -> {
                        response = respository.getBrands(hashMap)
                    }

                    REQ_SORT -> {
                        response = respository.sortProducts(hashMap)
                    }

                    REQ_FILTER_CATEGORIES -> {
                        response = respository.filterCategories(hashMap)
                    }

                    REQ_FILTER_BRANDS -> {
                        response = respository.filterBrands(hashMap)
                    }

                    REQ_CHANGE_LANGUAGE -> {
                        response = respository.changeLanguage(hashMap)
                    }

                    REQ_MAKE_DEFAULT -> {
                        response = respository.makeDefault(hashMap)
                    }

                    REQ_SHIPENGINE_SERVICES -> {
                        response = respository.getShipEngineServiceDetails(hashMap)
                    }

                    REQ_DELIVERY_INFO -> {
                        response = respository.getDeliveryinfo(hashMap["id"].toString())
                    }

                    REQ_UPDATE_SHIPENGINE -> {
                        response = respository.updateShipEngineProvider(hashMap)
                    }

                    REQ_UPDATE_QUANTITY -> {
                        response = respository.updateQuantity(hashMap)
                    }
                }


                withContext(Dispatchers.Main) {

                    apiResponseHandler = apiExceptionHandler.exceptionHandler(response, context)
                    responseCode = requestCode

                    if (apiResponseHandler!!.isSuccess) {
                        liveDataResponse.value = response!!.body()
                        commonInterface?.onSuccessResponse(liveDataResponse, requestCode)
                    } else {
                        commonInterface?.onFailureResponse(
                            apiResponseHandler!!.errorResonse,
                            requestCode
                        )
                    }

                }


            }
        } else {
            commonInterface?.onFailureResponse(
                context.resources.getString(R.string.no_internet_connection),
                requestCode
            )
        }
    }


}