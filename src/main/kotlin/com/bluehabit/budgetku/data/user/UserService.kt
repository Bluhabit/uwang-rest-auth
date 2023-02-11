package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.Constants.Permission.HYPEN_READ
import com.bluehabit.budgetku.common.Constants.Permission.USER_PERMISSION
import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.DuplicateException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val validationUtil: ValidationUtil
) {
    fun getListUsers(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<UserResponse>> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString();
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val user = userRepository
            .findByUserEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")

        if (!validationUtil.isAllowed(
                permissions = user.getListPermission(),
                to = listOf(
                    USER_PERMISSION.plus(HYPEN_READ)
                )
            )
        ) throw UnAuthorizedException("User not allowed use this operations")

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

    fun addNewUser(
        body: CreateUserRequest
    ): BaseResponse<UserResponse?> {
        validationUtil.validate(body)
        val exist = userRepository.exist(body.userEmail!!)
        if (exist) {
            throw DuplicateException("Email already taken!")
        }
        val encoder = BCryptPasswordEncoder(16)
        val result: String = encoder.encode(body.userPassword)
        val user = userRepository.save(body.toEntity().copy(userPassword = result))

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
        if (!encoder.matches(body.currentPassword, findUser.userPassword)) {
            throw BadRequestException("Current password didn't match!")
        }
        val saved = userRepository.save(
            findUser.copy(
                userPassword = encoder.encode(body.newPassword)
            )
        )

        return BaseResponse(
            code = OK.value(),
            data = saved.toResponse(),
            message = "Success edit password"
        )
    }

    fun deleteUser(
        userId: Long
    ): BaseResponse<UserResponse?> {
        val findUserOrNull = userRepository
            .findByIdOrNull(userId)
            ?: throw DataNotFoundException("Cannot delete,user doesn't exist or has been remove")


        userRepository
            .deleteById(userId)

        return BaseResponse(
            code = OK.value(),
            data = findUserOrNull.toResponse(),
            message = "Success delete user $userId"
        )
    }


}