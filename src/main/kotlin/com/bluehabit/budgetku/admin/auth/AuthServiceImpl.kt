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

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val validationUtil: ValidationUtil,
    private val jwtUtil: JwtUtil
): UserDetailsService {

     fun getListUsers(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<UserResponse>> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString();
        if(email.isBlank()){
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val user = userRepository
            .findByEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")
        if(user.levelUser == LevelUser.DEV) {
            val getData = userRepository
                .findAll(pageable)

            return BaseResponse(
                code = OK.value(),
                data = PagingDataResponse(
                    page = getData.number,
                    size = getData.size,
                    totalPages = getData.totalPages,
                    totalData = getData.totalElements,
                    items = getData.content.map { it.toResponse() }
                ),
                message = "Data all users"
            )
        }

        throw UnAuthorizedException("You don't have permission")

    }

     fun signIn(
        body: LoginRequest
    ): AuthBaseResponse<UserResponse> {
        validationUtil.validate(body)

        val encoder = BCryptPasswordEncoder(16)


        val login = userRepository
            .findByEmail(
                body.email!!
            ) ?: throw UnAuthorizedException("Username or password didn't match to any account!")

        if(!encoder.matches(body.password,login.password)) throw UnAuthorizedException("Username or password didn't match to any account!")

        val token = jwtUtil.generateToken(login.email)


        return AuthBaseResponse(
            code = OK.value(),
            data = login.toResponse(),
            message = "Sign in success!",
            token = token
        )

    }

     fun addNewUser(
        body: UserRequest
    ): BaseResponse<UserResponse?> {
        validationUtil.validate(body)
        val exist = userRepository.exist(body.email!!)
        if(exist){
            throw DuplicateException("Email already taken!")
        }
        val encoder = BCryptPasswordEncoder(16)
        val result: String = encoder.encode(body.password)
        val user = userRepository.save(body.toEntity().copy(password = result))

        return BaseResponse(
            code = OK.value(),
            data = user.toResponse(),
            message = "Success"
        )

    }


     fun resetPassword(body: ResetPasswordRequest): BaseResponse<UserResponse> {
        validationUtil.validate(body)

        val findUser = userRepository.findByIdOrNull(body.userId) ?: throw DataNotFoundException("Cannot find user!")

        val encoder = BCryptPasswordEncoder(16)
        if(!encoder.matches(body.currentPassword,findUser.password)){
            throw BadRequestException("Current password didn't match!")
        }
       val saved = userRepository.save(findUser.copy(
            password = encoder.encode(body.newPassword)
        ))

        return BaseResponse(
            code=OK.value(),
            data = saved.toResponse(),
            message = "Success edit password"
        )
    }

    fun deleteUser(
        userId: Long
    ): BaseResponse<UserResponse?> {
        val findUserOrNull = userRepository
            .findByIdOrNull(userId) ?: throw DataNotFoundException("Cannot delete,user doesn't exist or has been remove")


        userRepository
            .deleteById(userId)

        return BaseResponse(
            code = OK.value(),
            data = findUserOrNull.toResponse(),
            message ="Success delete user $userId"
        )
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository
            .findByEmail(username!!) ?: throw UnAuthorizedException("token no valid or doesn't exist")

        return org.springframework.security.core.userdetails.User(
            username,
            user.password,
            Collections.singletonList(
                SimpleGrantedAuthority("ROLE_USER")
            )
        )

    }
}