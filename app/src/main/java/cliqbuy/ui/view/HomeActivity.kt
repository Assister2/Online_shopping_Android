package cliqbuy.ui.view

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.common.CommonActivity
import cliqbuy.databinding.ActivityHomeBinding
import cliqbuy.helper.CommonKeys
import cliqbuy.helper.CommonKeys.fragmentType
import cliqbuy.helper.Enums
import cliqbuy.helper.Enums.REQ_CHANGE_LANGUAGE
import cliqbuy.helper.Enums.REQ_LOGOUT
import cliqbuy.helper.Enums.REQ_USER_ACCESS_BY_TOKEN
import cliqbuy.model.CommonDataModel
import cliqbuy.model.SigninSignupModel
import cliqbuy.model.UserAccessModel
import cliqbuy.ui.adapter.LanguageAdapter
import cliqbuy.ui.fragments.AccountFragment
import cliqbuy.ui.fragments.CartFragment
import cliqbuy.ui.fragments.CategoriesFragment
import cliqbuy.ui.fragments.HomeFragment
import cliqbuy.utils.gone
import cliqbuy.utils.visible
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso

class HomeActivity : CommonActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var backPressed = 0    // used by onBackPressed()
    lateinit var headerview: View
    private lateinit var tvUserName: TextView
    private lateinit var tvNotSignedStatus: TextView
    private lateinit var rltProfile: RelativeLayout
    lateinit var tvUserImage: ImageView
    private lateinit var homeViewModel: SigninSignupModel
    private lateinit var userAccessViewModel: UserAccessModel
    private val logoutHashMap: HashMap<String, String> = HashMap()
    var fragmentData = 0
    var reDirect = 0
    var productId: String = ""
    var id: String ?= ""

    private lateinit var rvLanguage: RecyclerView
    private var languageDialog: android.app.AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initViewModel()
        initNavigationView()
        initApiResponseHandler()
        binding.fab.setSafeOnClickListener {
            val i = Intent(applicationContext, HomeSearchActivity::class.java)
            i.putExtra("type", "seller")
            startActivity(i)
        }

        binding.commonHeader.ivDrawer.setSafeOnClickListener {
            openDrawer()
        }
        if (sessionManager.userName!!.isEmpty())
            binding.tvAccount.text = resources.getString(R.string.account)
        else
            binding.tvAccount.text = sessionManager.userName

        val drawer = binding.drawerLayout
        binding.commonHeader.ivDrawer.visibility = View.VISIBLE
        binding.lltHome.setSafeOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
                callHomeFragment()
            } else {
                callHomeFragment()
            }
        }
        binding.lltCategories.setSafeOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
                callCategoriesFragemnt()
            } else {
                callCategoriesFragemnt()
            }
        }
        binding.lltCart.setSafeOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
                callCartFragemnt()
            } else {
                callCartFragemnt()
            }
        }
        binding.lltAccount.setSafeOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
                callAccountFragemnt()
            } else {
                callAccountFragemnt()
            }
        }

        binding.commonHeader.ivBack.setOnClickListener {
            onBackPressed()
        }

        loadFragement()
    }

    fun loadFragement() {
        if (intent.hasExtra(fragmentType)) {
            var type: Int = intent.getIntExtra(fragmentType, 0)
            if (type == 4) {
                callAccountFragemnt()
            } else if (type == 2) {
                callCategoriesFragemnt()
            } else if (type == 3) {
                callCartFragemnt()
            } else callHomeFragment()
        } else callHomeFragment()
    }

    private fun callCheckoutActivity() {
        val intent = Intent(this, CheckoutActivity::class.java)
        startActivity(intent)
    }

    private fun getUserAccess() {

        logoutHashMap["access_token"] = sessionManager.accessToken!!
        commonViewModel!!.apiRequest(REQ_USER_ACCESS_BY_TOKEN, logoutHashMap, this)
    }


    private fun initNavigationView() {
        mDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(mDrawerToggle!!)
        mDrawerToggle?.syncState()
        binding.drawerLayout.drawerElevation = 0F
        binding.navView.setNavigationItemSelectedListener(this)
        headerview = binding.navView.getHeaderView(0)
        val menu = binding.navView.menu
        menu.findItem(R.id.nav_home).isVisible = true
        val logoutLayout = binding.navView.menu.findItem(R.id.nav_logout)
        val signInLayout = binding.navView.menu.findItem(R.id.nav_sign_in)
        tvUserName = headerview.findViewById<View>(R.id.tv_user_name) as TextView
        tvUserImage = headerview.findViewById<View>(R.id.iv_user_image) as ImageView
        rltProfile = headerview.findViewById<View>(R.id.rlt_nav_profile) as RelativeLayout
        tvNotSignedStatus = headerview.findViewById<View>(R.id.tv_not_signed) as TextView


        if (!sessionManager.accessToken.isNullOrEmpty() && sessionManager.userId != "0" && sessionManager.userId != "") {

            tvNotSignedStatus.visibility = View.GONE
            rltProfile.visibility = View.VISIBLE

            tvUserName.text = sessionManager.userName.toString()
            loadImage(sessionManager.userAvatar.toString())

            true.also {
                menu.findItem(R.id.nav_profile).isVisible = it
                menu.findItem(R.id.nav_purchase_history).isVisible = it
                menu.findItem(R.id.nav_wishlist).isVisible = it

                binding.navView.menu.findItem(R.id.nav_logout).isVisible = true
                logoutLayout.actionView!!.findViewById<Button>(R.id.btn_nav_logout)
                    .setSafeOnClickListener {
                        showLogoutDialog()
                    }
            }


        } else {

            rltProfile.visibility = View.GONE
            tvNotSignedStatus.visibility = View.VISIBLE

            false.also {
                menu.findItem(R.id.nav_profile).isVisible = it
                menu.findItem(R.id.nav_purchase_history).isVisible = it
                menu.findItem(R.id.nav_wishlist).isVisible = it
                binding.navView.menu.findItem(R.id.nav_logout).isVisible = it
                binding.navView.menu.findItem(R.id.nav_sign_in).isVisible = true
                signInLayout.actionView!!.findViewById<Button>(R.id.btn_nav_signin)
                    .setSafeOnClickListener {
                        callSignIn()
                    }
            }

        }
    }

    private fun loadImage(imageUrl: String) {
        if (imageUrl.isNotEmpty() && imageUrl != "null")
            if (!sessionManager.isSocialLogin!!)
                Picasso.get().load(resources.getString(R.string.imageUrl) + imageUrl)
                    .into(tvUserImage)
            else
                Picasso.get().load(imageUrl).into(tvUserImage)
        else
            Picasso.get().load(R.drawable.ic_default_profile_pic).into(tvUserImage)

    }


    private fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        val drawer = binding.drawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
            backPressed = 0
        } else if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate()
        } else if (reDirect == 1) {
            finish()
        } else {
            if (backPressed >= 1) {
                finishAffinity()
                super.onBackPressed()
            } else {
                backPressed += 1
                Toast.makeText(
                    this,
                    resources.getString(R.string.press_again_to_exit),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun callSignIn() {
        binding.drawerLayout.closeDrawers()
        val intent = Intent(this, SigninSignupActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.cb_fade_in,
            R.anim.cb_face_out
        )
    }


    private fun callAccountFragemnt() {
        //Image color
        binding.ivAccount.setColorFilter(resources.getColor(R.color.bg_third))
        binding.ivHome.setColorFilter(resources.getColor(R.color.bg_window))
        binding.ivCart.setColorFilter(resources.getColor(R.color.bg_window))
        binding.ivCategories.setColorFilter(resources.getColor(R.color.bg_window))
        //Text color
        binding.tvAccount.setTextColor(resources.getColor(R.color.txt_sixth))
        binding.tvHome.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.tvCategories.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.tvCart.setTextColor(resources.getColor(R.color.txt_secondary))
        //Text size
        binding.tvCategories.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        binding.tvAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
        binding.tvHome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        binding.tvCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        //Header change
        binding.commonHeader.tvTitle.visibility = View.VISIBLE
        binding.commonHeader.tvTitle.text = resources.getString(R.string.account)
        binding.commonHeader.ivHeaderLogo.visibility = View.GONE

        val fragment: Fragment = AccountFragment.newInstance()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()

    }

    @SuppressLint("ResourceAsColor")
    private fun callCartFragemnt() {
        //Image color
        binding.ivAccount.setColorFilter(resources.getColor(R.color.bg_window))
        binding.ivHome.setColorFilter(resources.getColor(R.color.bg_window))
        binding.ivCart.setColorFilter(resources.getColor(R.color.bg_third))
        binding.ivCategories.setColorFilter(resources.getColor(R.color.bg_window))
        //Text color
        binding.tvAccount.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.tvHome.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.tvCategories.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.tvCart.setTextColor(resources.getColor(R.color.txt_sixth))
        //Text size
        binding.tvCategories.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        binding.tvAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        binding.tvHome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        binding.tvCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
        //Header change
        binding.commonHeader.tvTitle.visibility = View.VISIBLE
        binding.commonHeader.tvTitle.text = resources.getString(R.string.cart)
        binding.commonHeader.ivHeaderLogo.visibility = View.GONE

        val bundle = Bundle()
        bundle.putInt("redirect", 0)
        val fragment: Fragment = CartFragment.newInstance()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        fragment.arguments = bundle
        transaction.commit()
    }

    fun homeFragmentchanges() {
        binding.ivAccount.setColorFilter(resources.getColor(R.color.bg_window))
        binding.ivHome.setColorFilter(resources.getColor(R.color.bg_third))
        binding.ivCart.setColorFilter(resources.getColor(R.color.bg_window))
        binding.ivCategories.setColorFilter(resources.getColor(R.color.bg_window))
        //Text color
        binding.tvAccount.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.tvHome.setTextColor(resources.getColor(R.color.txt_sixth))
        binding.tvCategories.setTextColor(resources.getColor(R.color.txt_secondary))
        binding.tvCart.setTextColor(resources.getColor(R.color.txt_secondary))
        //Text size
        binding.tvCategories.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        binding.tvAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        binding.tvHome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
        binding.tvCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
        //Header change
        binding.commonHeader.tvTitle.visibility = View.GONE
        binding.commonHeader.ivHeaderLogo.visibility = View.VISIBLE
    }

    private fun callHomeFragment() {
        //Image color
        homeFragmentchanges()

        clearBackStack()
        val fragment: Fragment = HomeFragment.newInstance()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack("")
        transaction.commit()
    }

    fun callCategoriesFragemnt(callFrom: String = "1") {
        //Image color
        if (callFrom.equals("1")) {
            binding.ivAccount.setColorFilter(resources.getColor(R.color.bg_window))
            binding.ivHome.setColorFilter(resources.getColor(R.color.bg_window))
            binding.ivCart.setColorFilter(resources.getColor(R.color.bg_window))
            binding.ivCategories.setColorFilter(resources.getColor(R.color.bg_third))
            //Text color
            binding.tvAccount.setTextColor(resources.getColor(R.color.txt_secondary))
            binding.tvHome.setTextColor(resources.getColor(R.color.txt_secondary))
            binding.tvCategories.setTextColor(resources.getColor(R.color.txt_sixth))
            binding.tvCart.setTextColor(resources.getColor(R.color.txt_secondary))
            //Text size
            binding.tvCategories.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
            binding.tvAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
            binding.tvHome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
            binding.tvCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
            //Header change

        }
        binding.commonHeader.tvTitle.visibility = View.VISIBLE
        binding.commonHeader.tvTitle.text = resources.getString(R.string.categories)
        binding.commonHeader.ivHeaderLogo.visibility = View.GONE
        callCategoryFragment(callFrom)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.nav_home -> {
                binding.drawerLayout.closeDrawers()
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    callHomeFragment()
                } else {
                    callHomeFragment()
                }
            }

            R.id.nav_profile -> {
                binding.drawerLayout.closeDrawers()
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    callAccountFragemnt()
                } else {
                    callAccountFragemnt()
                }
            }

            R.id.nav_purchase_history -> {
                binding.drawerLayout.closeDrawers()
                val intent = Intent(this, PurchaseHistory::class.java)
                val animation =
                    ActivityOptions.makeCustomAnimation(this, R.anim.cb_fade_in, R.anim.cb_face_out)
                        .toBundle()
                startActivity(intent, animation)
            }

            R.id.nav_wishlist -> {
                binding.drawerLayout.closeDrawers()
                val intent = Intent(this, WishlistActivity::class.java)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.cb_fade_in,
                    R.anim.cb_face_out
                )
            }

            R.id.nav_language -> {

                callLanguageDialog()

            }
        }
        return false
    }

    private fun callLanguageDialog() {
        rvLanguage = RecyclerView(this)
        rvLanguage.overScrollMode = View.OVER_SCROLL_NEVER
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.header, null)
        val header = view.findViewById<TextView>(R.id.header)

        initLanguageRv()

        header.text = getString(R.string.language_select)

        languageDialog = android.app.AlertDialog.Builder(this, R.style.customDialog)
            .setCustomTitle(view)
            .setView(rvLanguage)
            .setCancelable(true)
            .show()
    }

    private fun initLanguageRv() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvLanguage.layoutManager = linearLayoutManager
        rvLanguage.adapter = LanguageAdapter(
            resources.getStringArray(R.array.languages),
            resources.getStringArray(R.array.languageCode), sessionManager.language.toString()
        ).apply {

            this.setOnItemClickListener { language, languagecode ->

                if (sessionManager.language != language) {
                    sessionManager.language = language
                    sessionManager.languageCode = languagecode
                    binding.drawerLayout.closeDrawers()
                    changeLanguage()
                }
                languageDialog!!.dismiss()

            }
        }
    }

    private fun getData() = HashMap<String, String>().apply {
        println("### user id -->${sessionManager.userId}")
        put("language", sessionManager.languageCode.let {
            if (it == "ar") "sa"
            else "en"
        })
        put("user_id", sessionManager.userId.toString())
    }

    private fun changeLanguage() {
        if (!sessionManager.accessToken.isNullOrEmpty()) {
            commonViewModel!!.apiRequest(Enums.REQ_CHANGE_LANGUAGE, getData(), this)
        }
        recreateApp()
    }

    private fun recreateApp() {
        Intent(
            this,
            SplashActivity::class.java
        ).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
            this.finish()
        }
    }


    private fun showLogoutDialog() {

        val dialogBuilder = AlertDialog.Builder(this, R.style.DialogStyle)
        val view = layoutInflater.inflate(R.layout.dialog_logout, null)
        dialogBuilder.setView(view)
        val alertDialog = dialogBuilder.create()

        val confirm = view.findViewById<View>(R.id.btn_confirm) as Button
        val decline = view.findViewById<View>(R.id.btn_decline) as Button


        confirm.setOnClickListener {
            alertDialog.dismiss()
            callLogout()
        }

        decline.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun callLogout() {

        commonViewModel!!.apiRequest(REQ_LOGOUT, logoutHashMap, this)


    }

    override fun onResume() {
        super.onResume()

        if (intent.hasExtra("redirect")) {
            reDirect = intent.getIntExtra("redirect", 0)
            id = intent.getStringExtra("id")
            setViewEnable(reDirect)

        }

    }

    fun callProceedToShipping(id: Int, isPickup: Boolean, isNeedDeliveryinfo: Boolean) {
        val intent = Intent(this, ProceedToShippingActivity::class.java)
        intent.putExtra("seller_id", id.toString())
        intent.also { it.putExtra("id", productId) }
        intent.also { it.putExtra("redirect", reDirect) }
        intent.also { it.putExtra("is_pickup", isPickup) }
        intent.also { it.putExtra("is_need_delivery_infor", isNeedDeliveryinfo) }
        val animation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
        ).toBundle()
        startActivity(intent, animation)
    }

    private fun setViewEnable(reDirect: Int) = with(binding) {
        if (reDirect == 1) {
            fab.gone()
            rltBottomNav.gone()
        } else {
            fab.visible()
            rltBottomNav.visible()
        }
    }

    private fun initApiResponseHandler() {

        commonViewModel!!.liveDataResponse.observe(this, Observer {
            try {
                if (commonViewModel!!.apiResponseHandler!!.isSuccess) {
                    when (commonViewModel!!.responseCode) {
                        REQ_LOGOUT -> {
                            homeViewModel =
                                commonViewModel?.liveDataResponse?.value as SigninSignupModel

                            if (homeViewModel.result) {

                                binding.drawerLayout.closeDrawers()
                                commonMethods.showToast(this, resources.getString(R.string.logout_success))
                                clearSession()
                                //initNavigationView()


                            } else
                                commonMethods.showToast(this, homeViewModel.statusMessage)

                            callActivityItself()
                        }

                        REQ_USER_ACCESS_BY_TOKEN -> {
                            userAccessViewModel =
                                commonViewModel?.liveDataResponse?.value as UserAccessModel

                            if (userAccessViewModel.result) {

                                sessionManager.userId = userAccessViewModel.userId.toString()
                                sessionManager.userName = userAccessViewModel.userName
                                sessionManager.userEmail = userAccessViewModel.userMail
                                sessionManager.userPhone = userAccessViewModel.userPhone
                                sessionManager.userAvatar = userAccessViewModel.userAvatar

                            } else {
                                // commonMethods.showToast(this, resources.getString(R.string.internal_server_error))
                            }
                        }

                        REQ_CHANGE_LANGUAGE -> {
                            val commonModel =
                                commonViewModel!!.liveDataResponse.value as CommonDataModel
                            if (!commonModel.success) commonMethods.showToast(
                                this,
                                getString(R.string.please_try_again)
                            )

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })


    }


    private fun callActivityItself() {
        binding.drawerLayout.closeDrawers()
        val intent = Intent(this, HomeActivity::class.java)
        intent.also { it.putExtra(CommonKeys.fragmentType, 1) }
        startActivity(intent)
        overridePendingTransition(R.anim.cb_fade_in, R.anim.cb_face_out)
    }


    /*
    topcategroy -> 0
    category -> 1
     */
    fun callCategoryFragment(comeFrom: String) {
        if (comeFrom.equals("1"))
            clearBackStack()

        launchFragment(CategoriesFragment.newInstance().apply {
            arguments = Bundle().apply {
                putString("comeFrom", comeFrom)
            }
        })
        setCategoryCallback()
    }

    fun setCategoryCallback() {
        supportFragmentManager.setFragmentResultListener("requestKey", this) { requestKey, bundle ->
            launchFragment(CategoriesFragment.newInstance().apply { arguments = bundle })
        }
    }

    fun setHeader(title: String, isBackenabled: Boolean) = with(binding) {
        commonHeader.tvTitle.visibility = View.VISIBLE
        commonHeader.tvTitle.text = title
        if (isBackenabled) {
            commonHeader.ivDrawer.gone()
            commonHeader.ivBack.visible()
            fab.gone()
            rltBottomNav.gone()
        } else {
            commonHeader.ivDrawer.visible()
            commonHeader.ivBack.gone()
            fab.visible()
            rltBottomNav.visible()
        }
    }

    fun launchFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack("")
        transaction.commit()
    }

    fun clearBackStack() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}

