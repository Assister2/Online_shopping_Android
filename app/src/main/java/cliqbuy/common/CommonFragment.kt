package cliqbuy.common

import android.content.Context
import android.content.res.Configuration
import android.os.SystemClock
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cliqbuy.configs.AppController
import cliqbuy.dependencies.component.AppComponent
import cliqbuy.helper.SessionManager
import cliqbuy.interfaces.ApiService
import cliqbuy.viewmodel.CommonViewModel
import java.util.*
import javax.inject.Inject

open class CommonFragment : Fragment() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var apiService: ApiService

    var commonViewModel: CommonViewModel? = null

    init {
        AppController.appComponent!!.inject(this)
    }

    /**
     * Called when a fragment is first attached to its context.
     * [.onCreate] will be called after this.
     */
    override fun onAttach(context: Context) {
        super.onAttach(updateLocale(context)!!)
    }

    public fun initViewModel() {
        commonViewModel = ViewModelProvider(this).get(CommonViewModel::class.java)
        commonViewModel?.initAppController(requireContext(), requireActivity())
    }
    /**
     * Solving multiple clicks problem
     */

    //Hashmap
    val commonHashMap: HashMap<String, String> = HashMap()

    class SafeClickListener(
        private var defaultInterval: Int = 1000,
        private val onSafeCLick: (View) -> Unit
    ) : View.OnClickListener {
        private var lastTimeClicked: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }

    /**  set language for whole Application */
    fun updateLocale(newBase: Context?): Context? {
        var newBase = newBase
        val lang: String = sessionManager.languageCode!!
        val locale = Locale(lang)
        val config = Configuration(newBase?.resources?.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        newBase = newBase?.createConfigurationContext(config)
        newBase?.resources?.updateConfiguration(config, newBase.resources.displayMetrics)
        println("### -- locale frage    ${sessionManager.languageCode}")

        return newBase
    }


    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}