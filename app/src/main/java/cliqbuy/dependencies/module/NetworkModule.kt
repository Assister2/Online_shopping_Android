package cliqbuy.dependencies.module

import android.content.Context
import cliqbuy.dependencies.interceptors.AuthTokenInterceptor
import cliqbuy.helper.SessionManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import cliqbuy.interfaces.ApiService
import cliqbuy.network.NetworkInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

@Module
class NetworkModule @Inject
constructor(private val mBaseUrl: String, val mDomain: String) {

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(context: Context, httpLoggingInterceptor: HttpLoggingInterceptor, sessionManager: SessionManager): OkHttpClient.Builder {
        val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES)
        client.addInterceptor(httpLoggingInterceptor)
        client.addInterceptor(NetworkInterceptor(context))
        client.addInterceptor(AuthTokenInterceptor(sessionManager))
        //  client.authenticator(new TokenRenewInterceptor(sessionManager));

        return client
    }

    @Provides
    @Singleton
    fun providesRetrofitService(okHttpClient: OkHttpClient.Builder, gson: Gson): Retrofit {


        okHttpClient.hostnameVerifier(HostnameVerifier { hostname, session ->


            val hv = HttpsURLConnection.getDefaultHostnameVerifier()
            hv.verify(mDomain, session)


            /*val certs: Array<Certificate>
            certs = try {
                session.peerCertificates
            } catch (e: SSLException) {
                return@HostnameVerifier false
            }
            val x509 = certs[0] as X509Certificate
            // We can be case-insensitive when comparing the host we used to
            // establish the socket to the hostname in the certificate.
            val hostName = hostname.trim { it <= ' ' }.toLowerCase(Locale.ENGLISH)
            // Verify the first CN provided. Other CNs are ignored. Firefox, wget,
            // curl, and Sun Java work this way.
            val firstCn = getFirstCn(x509)
            if (matches(hostName, firstCn)) {
                return@HostnameVerifier true
            }
            for (cn in getDNSSubjectAlts(x509)) {
                if (matches(hostName, cn)) {
                    return@HostnameVerifier true
                }
            }
            false*/
        })



        return Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient.build())
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    private fun getFirstCn(cert: X509Certificate): String? {
        val subjectPrincipal: String = cert.getSubjectX500Principal().toString()
        for (token in subjectPrincipal.split(",".toRegex()).toTypedArray()) {
            val x = token.indexOf("CN=")
            if (x >= 0) {
                return token.substring(x + 3)
            }
        }
        return null
    }
}