package com.bluehabit.budgetku.common.model

import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.isAllowed
import com.bluehabit.budgetku.data.user.User
import com.bluehabit.budgetku.data.user.UserRepository
import com.bluehabit.budgetku.data.user.getListPermission
import org.springframework.security.core.context.SecurityContextHolder

data class BaseResponse<DATA>(
    var code: Int = 0,
    var data: DATA? = null,
    var message: String = ""
)

data class AuthBaseResponse<DATA>(
    var code: Int = 0,
    var data: DATA? = null,
    var message: String = "",
    var token: String = ""
)

fun <Data> baseResponse(lambda: BaseResponse<Data>.() -> Unit): BaseResponse<Data> =
    BaseResponse<Data>().apply(lambda)

fun <Data> baseAuthResponse(lambda: AuthBaseResponse<Data>.() -> Unit) =
    AuthBaseResponse<Data>().apply(lambda)

fun <Type> buildResponse(
    userRepository: UserRepository,
    permission: List<String> = listOf(),
    whenValidAndAccepted: (currentUser: User) -> Type
): Type {
    val email = SecurityContextHolder.getContext().authentication.principal.toString();
    if (email.isEmpty()) throw UnAuthorizedException("[98] You don't have access!")

    val user =
        userRepository.findByUserEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")
    if (!user.getListPermission().isAllowed(
            to = permission
        )
    ) throw UnAuthorizedException("User not allowed use this operations")

    return whenValidAndAccepted(
        user
    )
}

fun <Type> buildResponse(
    userRepository: UserRepository,
    whenValidAndAccepted: (user: User) -> Type
): Type {
    val email = SecurityContextHolder.getContext().authentication.principal.toString();
    if (email.isEmpty()) throw UnAuthorizedException("[98] You don't have access!")

    val user = userRepository.findByUserEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")

    return whenValidAndAccepted(
        user
    )
}
