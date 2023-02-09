package com.bluehabit.budgetku.feature.auth.v1

import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.user.LoginRequest
import com.bluehabit.budgetku.user.UserResponse

interface AuthService{

     fun signInWithEmailAndPassword(
        body: LoginRequest
    ): AuthBaseResponse<UserResponse>

}