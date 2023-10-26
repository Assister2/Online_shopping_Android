package cliqbuy.interfaces

import androidx.lifecycle.LiveData

interface ApiResponseCommonInterface {

    fun onSuccessResponse(jsonResponse: LiveData<Any>, requestCode : Int)

    fun onFailureResponse(errorResponse: String,requestCode : Int)


}
