package cliqbuy.interfaces


import cliqbuy.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList


interface ApiService {
    // Home page restaurant list
    @GET("home")
    fun home(
        @Query("token") token: String?,
        @Query("service_type") orderType: Int?,
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("auth/signup")
    suspend fun register(@FieldMap hashMap: HashMap<String, String>): Response<SigninSignupModel>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(@FieldMap hashMap: HashMap<String, String>): Response<SigninSignupModel>

    @FormUrlEncoded
    @POST("auth/social-login")
    suspend fun socialLogin(@FieldMap hashMap: HashMap<String, String>): Response<SigninSignupModel>

    @GET("common_data")
    suspend fun commonData(): Response<CommonDataModel>

    @GET("sliders")
    suspend fun slider(): Response<CommonHomeModel>

    @GET("categories/featured")
    suspend fun homeCategory(): Response<CommonHomeModel>

    @GET("products/featured")
    suspend fun homeProducts(): Response<CommonHomeModel>

    @GET("pickup_points")
    suspend fun pickupPoint(@QueryMap hashMap: HashMap<String, String>): Response<PickupPointModel>


    @GET("countries")
    suspend fun countryList(): Response<CountryListModel>

    @GET("states")
    suspend fun stateList(@QueryMap hashMap: HashMap<String, String>): Response<StateListModel>

    @GET("cities")
    suspend fun cityList(@QueryMap hashMap: HashMap<String, String>): Response<CityListModel>

    @GET("user/shipping/address/{id}")
    suspend fun addressList(@Path("id") userId: String?): Response<AddressListModel>

    @GET("purchase-history/{id}")
    suspend fun purchaseHistory(
        @Path("id") userId: String?,
        @QueryMap hashMap: HashMap<String, String>,
    ): Response<PurchaseHistoryModel>

    @GET("user/shipping/delete/{id}")
    suspend fun deleteAddress(
        @Path("id") userId: String?,
        @QueryMap hashMap: HashMap<String, String>,
    ): Response<GenericModel>

    @FormUrlEncoded
    @POST("user/shipping/create")
    suspend fun createAddress(@FieldMap hashMap: HashMap<String, String>): Response<GenericModel>

    @FormUrlEncoded
    @POST("shipping_cost")
    suspend fun shippingCost(@FieldMap hashMap: HashMap<String, String>): Response<ShippingCostModel>

    @FormUrlEncoded
    @POST("user/shipping/update")
    suspend fun updateAddress(@FieldMap hashMap: HashMap<String, String>): Response<GenericModel>

    @FormUrlEncoded
    @POST("auth/resend_code")
    suspend fun resendCode(@FieldMap hashMap: HashMap<String, String>): Response<OtpVerificationModel>

    @FormUrlEncoded
    @POST("auth/confirm_code")
    suspend fun confirmCode(@FieldMap hashMap: HashMap<String, String>): Response<OtpVerificationModel>

    @FormUrlEncoded
    @POST("auth/password/forget_request")
    suspend fun forgetPassword(@FieldMap hashMap: HashMap<String, String>): Response<SigninSignupModel>

    @FormUrlEncoded
    @POST("auth/password/confirm_reset")
    suspend fun resetPassword(@FieldMap hashMap: HashMap<String, String>): Response<SigninSignupModel>

    @FormUrlEncoded
    @POST("auth/password/resend_code")
    suspend fun resendCodeForPassword(@FieldMap hashMap: HashMap<String, String>): Response<SigninSignupModel>

    @FormUrlEncoded
    @POST("profile/update")
    suspend fun updateProfile(@FieldMap hashMap: HashMap<String, String>): Response<GenericModel>

    @GET("auth/logout")
    suspend fun logout(@QueryMap hashMap: HashMap<String, String>): Response<SigninSignupModel>

    @GET("profile/counters/{id}")
    suspend fun userProfile(@Path("id") userId: String?): Response<UserProfileModel>

    @GET("purchase-history-details/{id}")
    suspend fun getOrderDetail(@Path("id") userId: String?): Response<OrderDetailsModel>

    @GET("purchase-history-items/{id}")
    suspend fun getOrderItem(@Path("id") userId: String?): Response<OrderDetailItemModel>

    @GET("shops/details/{id}")
    suspend fun getShopDetails(@Path("id") userId: String?): Response<StoreDetailsModel>

    @GET("products/{id}")
    suspend fun getProducts(
        @Path("id") userId: String?,
        @QueryMap hashMap: HashMap<String, String>,
    ): Response<ProductDataModel>

    @GET("products/related/{id}")
    suspend fun getProductRelated(@Path("id") userId: String?): Response<CommonHomeModel>

    @GET("products/top-from-seller/{id}")
    suspend fun getTopSelling(@Path("id") userId: String?): Response<CommonHomeModel>

    @GET("shops/products/top/{id}")
    suspend fun getSellerTopSelling(@Path("id") userId: String?): Response<CommonHomeModel>

    @GET("shops/products/new/{id}")
    suspend fun getSellerNewArrival(@Path("id") userId: String?): Response<CommonHomeModel>

    @GET("shops/products/featured/{id}")
    suspend fun getSellerFeatured(@Path("id") userId: String?): Response<CommonHomeModel>

    @POST("carts/{id}")
    suspend fun getCart(@Path("id") userId: String?): Response<ArrayList<CartModel>>

    @FormUrlEncoded
    @POST("carts/process")
    suspend fun updateCart(@FieldMap hashMap: HashMap<String, String>): Response<CommonDataModel>

    @DELETE("carts/{id}")
    suspend fun deleteCart(@Path("id") userId: String?): Response<CommonDataModel>

    @GET("wishlists-check-product")
    suspend fun getWishListCheck(@QueryMap hashMap: HashMap<String, String>): Response<CommonHomeModel>

    @GET("wishlists-add-product")
    suspend fun getWishListAdd(@QueryMap hashMap: HashMap<String, String>): Response<CommonHomeModel>

    @GET("wishlists-remove-product")
    suspend fun getWishListRemove(@QueryMap hashMap: HashMap<String, String>): Response<CommonHomeModel>


    @GET("get-user-by-access_token")
    suspend fun getUserAccessToken(@QueryMap hashMap: HashMap<String, String>): Response<UserAccessModel>

    @FormUrlEncoded
    @POST("profile/delete_account")
    suspend fun deleteaccount(@FieldMap hashMap: HashMap<String, String>): Response<CommonHomeModel>

    @FormUrlEncoded
    @POST("carts/add")
    suspend fun addToCart(@FieldMap hashMap: HashMap<String, String>): Response<CommonDataModel>

    @FormUrlEncoded
    @POST("update-address-in-cart")
    suspend fun proceedCheckOut(@FieldMap hashMap: HashMap<String, String>): Response<CommonDataModel>

    @GET("products/best-seller")
    suspend fun topSelling(): Response<CommonHomeModel>

    @GET("products/todays-deal")
    suspend fun topDeal(): Response<CommonHomeModel>

    @GET("flash-deals")
    suspend fun flashDeal(): Response<CommonHomeModel>

    @GET("products/category/{id}")
    suspend fun viewProduct(
        @Path("id") Id: String,
        @QueryMap hashMap: HashMap<String, String>,
    ): Response<ViewProductModel>

    @GET("products/seller/{id}")
    suspend fun sellerViewProduct(
        @Path("id") Id: String,
        @QueryMap hashMap: HashMap<String, String>,
    ): Response<ViewProductModel>

    @GET("products/brand/{id}")
    suspend fun viewBrand(
        @Path("id") Id: String,
        @QueryMap hashMap: HashMap<String, String>,
    ): Response<ViewProductModel>

    @GET("categories")
    suspend fun getCategories(@QueryMap hashMap: HashMap<String, String>): Response<CategoriesModel>

    @GET("categories/top")
    suspend fun getTopCategories(): Response<CategoriesModel>

    @FormUrlEncoded
    @POST("profile/update-image")
    suspend fun updateUserImage(@FieldMap hashMap: HashMap<String, String>): Response<GenericModel>

    @GET("wishlists/{id}")
    suspend fun userWishlist(@Path("id") userId: String?): Response<WishlistModel>

    @DELETE("wishlists/{id}")
    suspend fun deleteWishlistItem(@Path("id") userId: String?): Response<WishlistModel>

    @GET("cart-summary/{id}/{default_id}")
    suspend fun cartSummary(
        @Path("id") userId: String?,
        @Path("default_id") defaultId: String?,
    ): Response<CartInvoiceModel>

    @GET("flash-deal-products/{id}")
    suspend fun flashDealDetails(@Path("id") userId: String?): Response<ViewProductModel>

    @FormUrlEncoded
    @POST("profile/delete_account_otp")
    suspend fun deleteOtp(@FieldMap hashMap: HashMap<String, String>): Response<CommonHomeModel>

    @FormUrlEncoded
    @POST("coupon-apply")
    suspend fun applyCoupon(@FieldMap hashMap: HashMap<String, String>): Response<GenericModel>

    @FormUrlEncoded
    @POST("coupon-remove")
    suspend fun removeCoupon(@FieldMap hashMap: HashMap<String, String>): Response<GenericModel>

    @FormUrlEncoded
    @POST("payments/pay/cod")
    suspend fun placeOrderByCash(@FieldMap hashMap: HashMap<String, String>): Response<GenericModel>

    @GET("products/best-seller")
    suspend fun getProducts(): Response<ViewProductModel>

    @GET("shops")
    suspend fun seller(@QueryMap hashMap: HashMap<String, String>): Response<SellerModels>

    @GET("paypal/payment/url?")
    suspend fun placeOrderByPaypal(@QueryMap hashMap: HashMap<String, String>): Response<GenericModel>

    @GET("brands")
    suspend fun getBrands(@QueryMap hashMap: HashMap<String, String>): Response<SellerModels>

    @GET("payment-types")
    suspend fun getPaymentTypes(@QueryMap hashMap: HashMap<String, String>): Response<ArrayList<PaymentMethodsModel>>

    @GET("products/search")
    suspend fun sortProducts(@QueryMap hashMap: HashMap<String, String>): Response<ViewProductModel>

    @GET("filter/categories")
    suspend fun filterCategories(): Response<CategoriesModel>

    @GET("filter/brands")
    suspend fun filterBrands(): Response<CategoriesModel>

    @GET("language")
    suspend fun changeLanguage(@QueryMap hashMap: HashMap<String, String>): Response<CommonDataModel>

    @FormUrlEncoded
    @POST("user/shipping/make_default")
    suspend fun makeDefault(@FieldMap hashMap: HashMap<String, String>): Response<Any>

    @FormUrlEncoded
    @POST("ship_estimate")
    suspend fun getShipEngineServicedDetails(@FieldMap hashMap: HashMap<String, String>): Response<ShipEngineServiceModel>


    @POST("delivery_info/{id}")
    suspend fun getDeliveryinfo(@Path("id") userId: String?): Response<ArrayList<CartModel>>

    @FormUrlEncoded
    @POST("cart/updateRateId")
    suspend fun updateShipEngineProvider(@FieldMap hashMap: HashMap<String, String>): Response<UpdateQuantityModel>

    @FormUrlEncoded
    @POST("cart/updateQuantity")
    suspend fun updateQuantity(@FieldMap hashMap: HashMap<String, String>): Response<UpdateQuantityModel>


}