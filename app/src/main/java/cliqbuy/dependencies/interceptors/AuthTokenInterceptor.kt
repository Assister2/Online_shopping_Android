package cliqbuy.dependencies.interceptors


import cliqbuy.helper.SessionManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import kotlin.jvm.Throws


class AuthTokenInterceptor(private val sessionManager: SessionManager) : Interceptor {
    private var requestBuilder: Request.Builder? = null


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val original = chain.request()
            if (sessionManager.accessToken != null) {
                // Request customization: add request headers
                requestBuilder = original.newBuilder().header("Authorization","Bearer "+sessionManager.accessToken!!).method(original.method, original.body)
            } else {
                // Request customization: add request headers
                requestBuilder = original.newBuilder().method(original.method, original.body)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val request = requestBuilder!!.build()
        return chain.proceed(request)
    }
}