package cliqbuy.ui.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.common.CommonFragment
import cliqbuy.databinding.FragmentAccountBinding
import cliqbuy.helper.Enums
import cliqbuy.helper.Enums.REQ_CHANGE_LANGUAGE
import cliqbuy.helper.Enums.REQ_USER_PROFILE
import cliqbuy.model.CommonDataModel
import cliqbuy.model.UserProfileModel
import cliqbuy.ui.adapter.CityCountryAdapter
import cliqbuy.ui.adapter.LanguageAdapter
import cliqbuy.ui.view.*
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso


class AccountFragment : CommonFragment() {

    private lateinit var binding: FragmentAccountBinding
    lateinit var profileModel: UserProfileModel
    private lateinit var rvLanguage: RecyclerView
    private var languageDialog: android.app.AlertDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAccountBinding.inflate(layoutInflater)
        changeView()
        loadImage(sessionManager.userAvatar.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initAllViews()
        initViewModel()
        initApiResponseHandler()
        binding.rltProfileDetails.setSafeOnClickListener { callProfilePage() }
        binding.btnSignIn.setSafeOnClickListener { callSignInSignUp() }
        binding.rltAddress.setSafeOnClickListener { callAddressDetailsPage() }
        binding.lltPurchaseHistory.setSafeOnClickListener { callPurchaseHistoryPage() }
        binding.rltLanguage.setSafeOnClickListener { callLanguageDialog() }
        return binding.root

    }



    private fun initAllViews() {
        binding.tvUserName.text = sessionManager.userName
        binding.tvUserEmail.text = sessionManager.userEmail
    }

    override fun onResume() {
        super.onResume()

        if (!sessionManager.accessToken.isNullOrEmpty()){
            getUserProfile()
        }

    }

    private fun changeView() = if (!sessionManager.accessToken.isNullOrEmpty()){

            binding.rltBeforeSignIn.visibility =View.GONE
            binding.lltAfterSignIn.visibility = View.VISIBLE
        }else{

            binding.lltAfterSignIn.visibility = View.GONE
            binding.rltBeforeSignIn.visibility =View.VISIBLE

            commonMethods.rotateArrow(binding.ivBeforeLogin,requireActivity())
        }


    private fun getUserProfile() {

        val commonHashMap: HashMap<String, String> = HashMap()
        commonMethods.showProgressDialog(requireContext())
        commonViewModel!!.apiRequest(REQ_USER_PROFILE,commonHashMap,requireContext())
    }

    private fun loadImage(imageUrl: String) {
        if (imageUrl.isNotEmpty() && imageUrl != "null")
            if (!sessionManager.isSocialLogin!!)
                Picasso.get().load(resources.getString(R.string.imageUrl)+imageUrl).into(binding.ivProfile)
            else

                Glide.with(requireContext())
                    .load(resources.getString(R.string.imageUrl)+imageUrl).into(binding.ivProfile)
              //  Picasso.get().load(resources.getString(R.string.imageUrl)+imageUrl).into(binding.ivProfile)
        else
            Picasso.get().load(R.drawable.ic_default_profile_pic).into(binding.ivProfile)

    }

    private fun callProfilePage() {

        var intent = Intent(requireContext(), ProfileActivity::class.java)
        intent.putExtra("delete",profileModel.deleteMessage)
        intent.putExtra("showdelete",profileModel.showDelete)
        val animation = ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun callSignInSignUp() {
        var intent = Intent(requireContext(), SigninSignupActivity::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.cb_fade_in,
            R.anim.cb_face_out
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun callAddressDetailsPage() {
        var intent = Intent(requireContext(), AddressDetailsActivity::class.java)
        intent.putExtra("redirect",1)
        val animation = ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun callPurchaseHistoryPage() {
        var intent = Intent(requireContext(), PurchaseHistory::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun callProceedToShipping() {
        var intent = Intent(requireContext(), ProceedToShippingActivity::class.java)
        val animation = ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }
    private fun initApiResponseHandler() {
        commonViewModel!!.liveDataResponse.observe(requireActivity(), Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {
                commonMethods.hideProgressDialog()
                when (commonViewModel!!.responseCode) {
                    REQ_USER_PROFILE->{
                        profileModel = commonViewModel!!.liveDataResponse.value as UserProfileModel
                        binding.tvCartCount.text =profileModel.cartItemCount.toString()
                        binding.tvWishlistCount.text =profileModel.wishlistCount.toString()
                        
                    }
                    REQ_CHANGE_LANGUAGE -> {
                        val commonModel = commonViewModel!!.liveDataResponse.value as CommonDataModel
                        if(!commonModel.success) commonMethods.showToast(requireActivity(),getString(R.string.please_try_again))

                    }
                }
            } else {
                commonMethods.hideProgressDialog()
            }
        })
    }

    private fun callLanguageDialog() {
        rvLanguage = RecyclerView(requireContext())
        rvLanguage.overScrollMode = View.OVER_SCROLL_NEVER
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.header, null)
        val header = view.findViewById<TextView>(R.id.header)

        initLanguageRv()

        header.text = getString(R.string.language_select)

        languageDialog = android.app.AlertDialog.Builder(requireContext(), R.style.customDialog)
            .setCustomTitle(view)
            .setView(rvLanguage)
            .setCancelable(true)
            .show()
    }

    private fun initLanguageRv() {
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvLanguage.layoutManager = linearLayoutManager
        rvLanguage.adapter = LanguageAdapter(resources.getStringArray(R.array.languages),
            resources.getStringArray(R.array.languageCode),sessionManager.language.toString()).apply {

                this.setOnItemClickListener{ language, languagecode ->
                    if(sessionManager.language != language){
                        sessionManager.language = language
                        sessionManager.languageCode = languagecode
                        changeLanguage()
                    }
                    languageDialog!!.dismiss()
                }
        }
    }

    private fun changeLanguage() {
        if(!sessionManager.accessToken.isNullOrEmpty()){
            commonViewModel!!.apiRequest(REQ_CHANGE_LANGUAGE,getData(),requireContext())
            println("#### hashmap -> ${getData()}")
        }
        recreateApp()
    }

    private fun getData() = HashMap<String,String>().apply {
        println("### user id -->${sessionManager.userId}")

        put("language",sessionManager.languageCode.let {
            if(it == "ar")"sa"
            else "en"
        })
        put("user_id", sessionManager.userId.toString())
    }

    private fun recreateApp() {
        Intent(
            requireContext(),
            SplashActivity::class.java
        ).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
            requireActivity().finish()
        }
    }
    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}