package cliqbuy.repository

import cliqbuy.configs.AppController
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.SessionManager
import cliqbuy.interfaces.ApiService
import cliqbuy.model.ShipEngineServiceModel
import retrofit2.Response
import java.util.HashMap
import javax.inject.Inject

class Repository {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sessionManager: SessionManager


    init {
        AppController.appComponent!!.inject(this)
    }

    suspend fun register(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.register(hashMap) as Response<Any>
    }

    suspend fun pickupPoint(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.pickupPoint(hashMap) as Response<Any>
    }

   suspend fun SellerPoint(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.seller(hashMap) as Response<Any>
    }

    suspend fun addressList(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.addressList(sessionManager.userId) as Response<Any>
    }

    suspend fun purchaseHistory(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.purchaseHistory(sessionManager.userId,hashMap) as Response<Any>
    }

    suspend fun deleteAddress(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.deleteAddress(CommonKeys.deleteAddressId.toString(),hashMap) as Response<Any>
    }

    suspend fun createAddress(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.createAddress(hashMap) as Response<Any>
    }

    suspend fun updateAddress(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.updateAddress(hashMap) as Response<Any>
    }

    suspend fun countryList(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.countryList() as Response<Any>
    }

    suspend fun stateList(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.stateList(hashMap) as Response<Any>
    }

    suspend fun cityList(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.cityList(hashMap) as Response<Any>
    }

    suspend fun shippingCost(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.shippingCost(hashMap) as Response<Any>
    }

    suspend fun login(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.login(hashMap) as Response<Any>
    }

    suspend fun socialLogin(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.socialLogin(hashMap) as Response<Any>
    }

    suspend fun commonData(): Response<Any> {

        return apiService.commonData() as Response<Any>
    }

    suspend fun slider(): Response<Any> {

        return apiService.slider() as Response<Any>
    }

    suspend fun homeCategory(): Response<Any> {

        return apiService.homeCategory() as Response<Any>
    }

    suspend fun homeProducts(): Response<Any> {

        return apiService.homeProducts() as Response<Any>
    }

    suspend fun resendCode(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.resendCode(hashMap) as Response<Any>
    }

    suspend fun getOrderDetail(productId: String): Response<Any> {

        return apiService.getOrderDetail(productId) as Response<Any>
    }

    suspend fun getOrderItem(productId: String): Response<Any> {

        return apiService.getOrderItem(productId) as Response<Any>
    }

    suspend fun getShopDetails(shopID: String): Response<Any> {

        return apiService.getShopDetails(shopID) as Response<Any>
    }

    suspend fun confirmCode(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.confirmCode(hashMap) as Response<Any>
    }
    suspend fun forgetPassword(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.forgetPassword(hashMap) as Response<Any>
    }
    suspend fun resetPassword(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.resetPassword(hashMap) as Response<Any>
    }

    suspend fun resendCodeForPassword(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.resendCodeForPassword(hashMap) as Response<Any>
    }

    suspend fun logout(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.logout( hashMap) as Response<Any>
    }

    suspend fun updateProfile(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.updateProfile( hashMap) as Response<Any>
    }


    suspend fun userProfile(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.userProfile(sessionManager.userId) as Response<Any>
    }


    suspend fun getProducts(productId :String,hashMap: HashMap<String, String>): Response<Any> {

        return apiService.getProducts(productId,hashMap) as Response<Any>
    }

    suspend fun getProductRelated(productId :String): Response<Any> {

        return apiService.getProductRelated(productId) as Response<Any>
    }

    suspend fun getTopSelling(productId :String): Response<Any> {

        return apiService.getTopSelling(productId) as Response<Any>
    }

    suspend fun getSellerTopSelling(productId :String): Response<Any> {

        return apiService.getSellerTopSelling(productId) as Response<Any>
    }

    suspend fun getSellerNewArrival(productId :String): Response<Any> {

        return apiService.getSellerNewArrival(productId) as Response<Any>
    }

    suspend fun getSellerFeatured(productId :String): Response<Any> {

        return apiService.getSellerFeatured(productId) as Response<Any>
    }

    suspend fun getCart(userId :String): Response<Any> {

        return apiService.getCart(userId) as Response<Any>
    }

    suspend fun deleteCart(userId :String): Response<Any> {

        return apiService.deleteCart(userId) as Response<Any>
    }

    suspend fun updateCart(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.updateCart(hashMap) as Response<Any>
    }

    suspend fun getWishListAdd(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.getWishListAdd(hashMap) as Response<Any>
    }

    suspend fun getWishListRemove(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.getWishListRemove(hashMap) as Response<Any>
    }

    suspend fun getWishListCheck(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.getWishListCheck(hashMap) as Response<Any>
    }

    suspend fun deleteAccount(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.deleteaccount(hashMap) as Response<Any>
    }

    suspend fun getUserAccessToken(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.getUserAccessToken(hashMap) as Response<Any>
    }

    suspend fun addToCart(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.addToCart(hashMap) as Response<Any>
    }

    suspend fun proceedCheckOut(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.proceedCheckOut(hashMap) as Response<Any>
    }

    suspend fun topSelling(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.topSelling() as Response<Any>
    }

    suspend fun topDeal(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.topDeal() as Response<Any>
    }
    suspend fun flashDeal(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.flashDeal() as Response<Any>
    }
    suspend fun viewProduct(productId :String,hashMap: HashMap<String, String>): Response<Any> {

        return apiService.viewProduct(productId,hashMap) as Response<Any>
    }

    suspend fun sellerViewProduct(productId :String,hashMap: HashMap<String, String>): Response<Any> {

        return apiService.sellerViewProduct(productId,hashMap) as Response<Any>
    }

    suspend fun viewBrand(productId :String,hashMap: HashMap<String, String>): Response<Any> {

        return apiService.viewBrand(productId,hashMap) as Response<Any>
    }

    suspend fun getCategories(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.getCategories(hashMap) as Response<Any>
    }

    suspend fun updateUserImage( hashMap: HashMap<String, String>): Response<Any> {
        return apiService.updateUserImage(hashMap) as Response<Any>
    }


    suspend fun getTopCategories(): Response<Any> {
        return apiService.getTopCategories() as Response<Any>
    }

    suspend fun userWishlist(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.userWishlist(sessionManager.userId) as Response<Any>
    }
    suspend fun deleteWishlistItem(itemId:String): Response<Any> {

        return apiService.deleteWishlistItem(itemId) as Response<Any>
    }

    suspend fun flashDealDetails(productId:String): Response<Any> {

        return apiService.flashDealDetails(productId) as Response<Any>
    }
    suspend fun cartSummary(userId:String,defaultId:String): Response<Any> {

        return apiService.cartSummary(userId,defaultId) as Response<Any>
    }
    suspend fun getPaymentTypes(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.getPaymentTypes(hashMap) as Response<Any>
    }

    suspend fun applyCoupon(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.applyCoupon(hashMap) as Response<Any>
    }

    suspend fun removeCoupon(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.removeCoupon(hashMap) as Response<Any>
    }

    suspend fun placeOrderByCash(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.placeOrderByCash(hashMap) as Response<Any>
    }
    suspend fun placeOrderByPaypal(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.placeOrderByPaypal(hashMap) as Response<Any>
    }
    suspend fun deleteOtp(hashMap: HashMap<String, String>): Response<Any> {

        return apiService.deleteOtp(hashMap) as Response<Any>
    }


    suspend fun getSearchProduct(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.getProducts() as Response<Any>
    }


    suspend fun getBrands(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.getBrands(hashMap) as Response<Any>
    }
    suspend fun sortProducts(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.sortProducts(hashMap) as Response<Any>
    }
    suspend fun filterCategories(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.filterCategories() as Response<Any>
    }

    suspend fun filterBrands(hashMap: HashMap<String, String>): Response<Any> {
        return apiService.filterBrands() as Response<Any>
    }

    suspend fun changeLanguage(hashMap: HashMap<String, String>): Response<Any>{
        return  apiService.changeLanguage(hashMap) as Response<Any>
    }
    suspend fun makeDefault(hashMap: HashMap<String, String>): Response<Any>{
        return  apiService.makeDefault(hashMap) as Response<Any>
    }
    suspend fun getShipEngineServiceDetails(hashMap: HashMap<String, String>): Response<Any>{
        return  apiService.getShipEngineServicedDetails(hashMap) as Response<Any>
    }
    suspend fun getDeliveryinfo(userId :String): Response<Any>{
        return  apiService.getDeliveryinfo(userId) as Response<Any>
    }
    suspend fun updateQuantity(hashMap: HashMap<String, String>): Response<Any>{
        return  apiService.updateQuantity(hashMap) as Response<Any>
    }
    suspend fun updateShipEngineProvider(hashMap: HashMap<String, String>): Response<Any>{
        return  apiService.updateShipEngineProvider(hashMap) as Response<Any>
    }
}