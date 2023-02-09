package com.bluehabit.budgetku.admin.auth.v1

import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.data.user.LoginRequest
import com.bluehabit.budgetku.data.user.UserResponse

interface AuthAdminService{
     fun signIn(
        body: LoginRequest
    ): AuthBaseResponse<UserResponse>

}