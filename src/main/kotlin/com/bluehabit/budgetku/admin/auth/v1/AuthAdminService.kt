package com.bluehabit.budgetku.admin.auth.v1

import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.user.LoginRequest
import com.bluehabit.budgetku.user.UserResponse

interface AuthAdminService{
     fun signIn(
        body: LoginRequest
    ): AuthBaseResponse<UserResponse>

}