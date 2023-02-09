package com.bluehabit.budgetku.admin.auth.v1

import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import org.springframework.data.domain.Pageable

interface AuthService{

     fun getListUsers(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<UserResponse>>

     fun signIn(
        body: LoginRequest
    ): AuthBaseResponse<UserResponse>

     fun addNewUser(
        body: UserRequest
    ): BaseResponse<UserResponse?>


     fun resetPassword(body: ResetPasswordRequest): BaseResponse<UserResponse>

    fun deleteUser(
        userId: Long
    ): BaseResponse<UserResponse?>
}