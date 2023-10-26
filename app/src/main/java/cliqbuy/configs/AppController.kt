package cliqbuy.configs

import android.app.Application
import android.content.Context
import dev.b3nedikt.app_locale.AppLocale.supportedLocales
import dev.b3nedikt.restring.Restring.init
import androidx.multidex.MultiDex
import cliqbuy.dependencies.component.DaggerAppComponent
import cliqbuy.dependencies.module.ApplicationModule
import cliqbuy.dependencies.module.NetworkModule
import com.google.firebase.FirebaseApp
import dev.b3nedikt.viewpump.ViewPump
import dev.b3nedikt.reword.RewordInterceptor
import cliqbuy.R
import cliqbuy.dependencies.component.AppComponent
import java.util.*


class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        appComponent = DaggerAppComponent.builder()
            .applicationModule(ApplicationModule(this)) // This also corresponds to the name of your module: %component_name%Module
            .networkModule(NetworkModule(resources.getString(R.string.apiBaseUrl),resources.getString(R.string.domain))).build()
        FirebaseApp.initializeApp(this)
        supportedLocales = APP_LOCALES
        init(this)
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(RewordInterceptor)
                .build()
        )
        instance = this
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        var appComponent: AppComponent? = null
            private set
        private val APP_LOCALES = Arrays.asList(Locale.ENGLISH, Locale.US)
        private var instance: AppController? = null
        val contexts: Context
            get() = instance!!.applicationContext
    }
}