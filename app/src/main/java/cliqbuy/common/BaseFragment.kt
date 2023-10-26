package cliqbuy.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.viewpump.ViewPump
import dev.b3nedikt.viewpump.ViewPumpContextWrapper
import cliqbuy.helper.SessionManager
import javax.inject.Inject

abstract class BaseFragment : Fragment() {


    @Inject
    lateinit var local: SessionManager


    override fun onResume() {
        super.onResume()
        //AppController.appComponent!!.inject(this)
        ViewPump.setOverwriteContext(Restring.wrapContext(requireContext()))
        updateLocale()
    }



    fun updateLocale() {
      /*  val locale = Locale(local.appLanguageCode!!)
        Locale.setDefault(locale)
        val resources: Resources = getResources()
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())*/
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val wrappedContext = ViewPumpContextWrapper.wrap(Restring.wrapContext(requireContext()))
        return wrappedContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}