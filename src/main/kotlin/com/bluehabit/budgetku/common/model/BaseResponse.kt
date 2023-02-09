package com.bluehabit.budgetku.common.model

data class BaseResponse<DATA>(
    var code:Int,
    var data:DATA,
    var message:String
)

data class AuthBaseResponse<DATA>(
    var code:Int,
    var data:DATA,
    var message:String,
    var token:String
)

