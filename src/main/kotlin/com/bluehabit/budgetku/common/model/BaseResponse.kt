package com.bluehabit.budgetku.common.model

data class BaseResponse<DATA>(
    var code:Int=0,
    var data:DATA?=null,
    var message:String=""
)

data class AuthBaseResponse<DATA>(
    var code:Int=0,
    var data:DATA?=null,
    var message:String="",
    var token:String=""
)

fun <Data> baseResponse(lambda: BaseResponse<Data>.()->Unit):BaseResponse<Data> = BaseResponse<Data>().apply(lambda)

fun <Data> baseAuthResponse(lambda: AuthBaseResponse<Data>.() -> Unit) = AuthBaseResponse<Data>().apply(lambda)