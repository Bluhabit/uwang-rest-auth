package com.bluehabit.budgetku.common.model

data class PagingDataResponse<OUTPUT>(
    var page: Int = 0,
    var size: Int = 0,
    var totalPages: Int = 0,
    var totalData: Long = 0,
    var items: List<OUTPUT> = listOf()
)

fun <OUTPUT> pagingResponse(lambda: PagingDataResponse<OUTPUT>.() -> Unit) = PagingDataResponse<OUTPUT>().apply(lambda)
