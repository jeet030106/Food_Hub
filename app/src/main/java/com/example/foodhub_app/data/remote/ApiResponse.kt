package com.example.foodhub_app.data.remote

import retrofit2.Response

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T): ApiResponse<T>()
    data class Error(val code:Int,val message: String): ApiResponse<Nothing>(){
        fun formatMsg():String{
            return "Error $code: $message"
        }
    }
    data class Exception(val exception: kotlin.Exception): ApiResponse<Nothing>()
}
suspend fun <T>safeApiCall(apiCall: suspend () -> Response<T>): ApiResponse<T> {
    try {
        val response=apiCall()
        if(response.isSuccessful){
            return ApiResponse.Success(response.body()!!)
        }else{
            return ApiResponse.Error(response.code(),response.errorBody().toString())
        }
    }catch (e:Exception) {
        return ApiResponse.Exception(e)
    }
}