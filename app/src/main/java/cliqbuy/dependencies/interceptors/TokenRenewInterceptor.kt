package cliqbuy.dependencies.interceptors


import cliqbuy.helper.SessionManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException


class TokenRenewInterceptor(private val sessionManager: SessionManager) : Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        println("Authenticating for response: $response")
        println("Challenges: " + response.challenges())
        println("Success: " + response.isSuccessful)
        println("Body: " + response.body!!.string())
        println("Body: " + response.request.header("Authorization"))
        if (response.request.header("Authorization") != null) {
            return null // Give up, we've already attempted to authenticate.
        }
        val credential = "sessionManager.getToken()"
        return if (responseCount(response) >= 3) {
            null
        } else response.request.newBuilder().header("Authorization", credential).build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        while (response.priorResponse != null) {
            result++
        }
        return result
    }
}