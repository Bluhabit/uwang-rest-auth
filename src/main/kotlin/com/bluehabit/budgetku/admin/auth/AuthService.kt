package com.bluehabit.budgetku.admin.auth

import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.DuplicateException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.config.admin.JwtUtil
import com.bluehabit.budgetku.model.AuthBaseResponse
import com.bluehabit.budgetku.model.BaseResponse
import com.bluehabit.budgetku.model.LevelUser
import com.bluehabit.budgetku.model.PagingDataResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

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