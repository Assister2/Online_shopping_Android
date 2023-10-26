package cliqbuy.dependencies.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import cliqbuy.R
import cliqbuy.common.CommonMethods
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import cliqbuy.helper.SessionManager
import cliqbuy.model.JsonResponse
import cliqbuy.network.ApiExceptionHandler
import cliqbuy.repository.Repository
import cliqbuy.viewmodel.CommonViewModel
import java.util.ArrayList


@Module(includes = [ApplicationModule::class])
class AppContainerModule {
    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(application.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesSessionManager(): SessionManager {
        return SessionManager()
    }
    @Provides
    @Singleton
    fun providesCommonMethods(): CommonMethods {
        return CommonMethods()
    }

    @Provides
    @Singleton
    fun jsonResponse(): JsonResponse {
        return JsonResponse()
    }

    @Provides
    @Singleton
    fun repository(): Repository {
        return Repository()
    }

    @Provides
    @Singleton
    fun viewModel(): CommonViewModel {
        return CommonViewModel()
    }

    @Provides
    @Singleton
    fun apiExceptionHandler(): ApiExceptionHandler {
        return ApiExceptionHandler()
    }


    
    @Provides
    @Singleton
    fun providesContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesStringArrayList(): ArrayList<String> {
        return ArrayList()
    }

}