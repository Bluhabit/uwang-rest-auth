package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.Constants.Permission.HYPEN_READ
import com.bluehabit.budgetku.common.Constants.Permission.USER_PERMISSION
import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.DuplicateException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.isAllowed
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseAuthResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.config.tokenMiddleware.JwtUtil
import com.bluehabit.budgetku.data.role.RoleRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val validationUtil: ValidationUtil,
    private val jwtUtil: JwtUtil
) : UserDetailsService {
    //region admin
    fun signIn(
        body: LoginRequest
    ): AuthBaseResponse<UserResponse> {
        validationUtil.validate(body)

        val encoder = BCryptPasswordEncoder(16)

        val login = userRepository.findByUserEmail(body.email!!)
            ?: throw UnAuthorizedException("Username or password didn't match to any account!")

        if (!encoder.matches(body.password, login.userPassword))
            throw UnAuthorizedException("Username or password didn't match to any account!")

        val findRoleSuper = roleRepository
            .findByRoleName("SUPER_ADMIN")

        if (!login.userRoles.contains(findRoleSuper)) throw UnAuthorizedException("User doesn't have access")

        val generatedToken = jwtUtil.generateToken(login.userEmail)

        return baseAuthResponse {
            code = OK.value()
            data = login.toResponse()
            message = "Sign In Success!"
            token = generatedToken
        }

    }

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails? {

        val user = userRepository
            .findByUserEmail(username) ?: return null
        return User(
            username,
            user.userPassword,
            user.userRoles.map {
                SimpleGrantedAuthority(it.roleName)
            }
        )

    }

    //end region

    //region user auth
    fun signInWithEmailAndPassword(
        body: LoginRequest
    ): AuthBaseResponse<UserResponse> {
        validationUtil.validate(body)

        val encoder = BCryptPasswordEncoder(16)


        val login = userRepository
            .findByUserEmail(
                body.email!!
            ) ?: throw UnAuthorizedException("Username or password didn't match to any account!")

        if (!encoder.matches(
                body.password,
                login.userPassword
            )
        ) throw UnAuthorizedException("Username or password didn't match to any account!")

        val generatedToken = jwtUtil.generateToken(login.userEmail)


        return baseAuthResponse {
            code = OK.value()
            data = login.toResponse()
            message = "Sign In Success!"
            token = generatedToken
        }

    }

    //
    fun getListUsers(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<UserResponse>> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString();
        if (email.isEmpty()) throw UnAuthorizedException("[98] You don't have access!")

        val user =
            userRepository.findByUserEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")

        if (!user.getListPermission().isAllowed(
                to = listOf(
                    USER_PERMISSION.plus(HYPEN_READ)
                )
            )
        ) throw UnAuthorizedException("User not allowed use this operations")

        val getData = userRepository.findAll(pageable)

        return baseResponse {
            code = OK.value()
            data = pagingResponse {
                page = getData.number
                size = getData.size
                items = getData.toListResponse()
                totalPages = getData.totalPages
                totalData = getData.totalElements
            }
            message = ""
        }
    }

    fun addNewUser(
        body: CreateNewUserRequest
    ): BaseResponse<UserResponse?> {
        validationUtil.validate(body)
        val exist = userRepository.exist(body.userEmail!!)
        if (exist) throw DuplicateException("Email already taken!")

        val encoder = BCryptPasswordEncoder(16)
        val result: String = encoder.encode(body.userPassword)
        val user = userRepository.save(body.toEntity().copy(userPassword = result))

        return baseResponse {
            code = OK.value()
            data = user.toResponse()
            message = ""
        }
    }


    fun resetPassword(body: ResetPasswordRequest): BaseResponse<UserResponse> {
        validationUtil.validate(body)

        val findUser = userRepository.findByIdOrNull(body.userId) ?: throw DataNotFoundException("Cannot find user!")

        val encoder = BCryptPasswordEncoder(16)
        if (!encoder.matches(body.currentPassword, findUser.userPassword)) throw BadRequestException(
            "Current password didn't match!"
        )

        val saved = userRepository.save(
            findUser.copy(
                userPassword = encoder.encode(body.newPassword)
            )
        )

        return baseResponse {
            code = OK.value()
            data = saved.toResponse()
            message = ""
        }
    }

    fun deleteUser(
        userId: String
    ): BaseResponse<UserResponse?> {
        val findUserOrNull = userRepository.findByIdOrNull(userId)
            ?: throw DataNotFoundException("Cannot delete,user doesn't exist or has been remove")

        userRepository.deleteById(userId)

        return baseResponse {
            code = OK.value()
            data = findUserOrNull.toResponse()
            message = ""
        }
    }


}