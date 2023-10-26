package cliqbuy.dependencies.component

import dagger.Component
import javax.inject.Singleton
import cliqbuy.dependencies.module.NetworkModule
import cliqbuy.dependencies.module.ApplicationModule
import cliqbuy.dependencies.module.AppContainerModule
import cliqbuy.common.CommonActivity
import cliqbuy.common.CommonFragment
import cliqbuy.common.CommonMethods
import cliqbuy.helper.SessionManager
import cliqbuy.network.ApiExceptionHandler
import cliqbuy.repository.Repository
import cliqbuy.ui.adapter.LanguageAdapter
import cliqbuy.viewmodel.CommonViewModel


@Singleton
@Component(modules = [NetworkModule::class, ApplicationModule::class, AppContainerModule::class])
interface AppComponent {

    fun inject(CommonActivity: CommonActivity?)

    fun inject(repository: Repository)

    fun inject(apiExceptionHandler: ApiExceptionHandler)

    fun inject(commonViewModel: CommonViewModel)

    fun inject(commonMethods: CommonMethods)

    fun inject(sessionManager: SessionManager)

    fun inject(commonFragment: CommonFragment)


}