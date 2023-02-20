package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.Constants
import com.bluehabit.budgetku.common.Constants.Permission.HYPEN_READ
import com.bluehabit.budgetku.common.Constants.Permission.HYPEN_WRITE
import com.bluehabit.budgetku.common.Constants.Permission.USER_PERMISSION
import com.bluehabit.budgetku.common.GoogleAuthUtil
import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.DuplicateException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseAuthResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.model.buildResponse
import com.bluehabit.budgetku.config.tokenMiddleware.JwtUtil
import com.bluehabit.budgetku.data.role.RoleRepository
import com.bluehabit.budgetku.data.userActivity.UserActivity
import com.bluehabit.budgetku.data.userActivity.UserActivityRepository
import org.springframework.core.env.Environment
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val userActivityRepository: UserActivityRepository,
    private val validationUtil: ValidationUtil,
    private val jwtUtil: JwtUtil,
    private val environment: Environment,
) : UserDetailsService {
    private val bcrypt = BCryptPasswordEncoder(Constants.BCrypt.STRENGTH)

    //region admin
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

        val login = userRepository
            .findByUserEmail(
                body.email!!
            ) ?: throw UnAuthorizedException("Username or password didn't match to any account!")

//        if (!bcrypt.matches(
//                body.password,
//                login.userPassword
//            )
//        ) throw UnAuthorizedException("Username or password didn't match to any account!")

        val generatedToken = jwtUtil.generateToken(login.userEmail)


        return baseAuthResponse {
            code = OK.value()
            data = login.toResponse()
            message = "Sign In Success!"
            token = generatedToken
        }

    }

    fun signInWithGoogle(
        request: LoginGoogleRequest
    ): AuthBaseResponse<UserResponse> = buildResponse(
        userRepository,
    ) {
        validationUtil.validate(request)
        val googleAuth = GoogleAuthUtil(environment)
        val verifyUser =
            googleAuth.getProfile(request.token!!)
                ?: throw UnAuthorizedException("Token from provider not valid")
        val findUser = userRepository.findByUserEmail(verifyUser.userEmail)
            ?: throw UnAuthorizedException("User not registered")

        baseAuthResponse {
            code=OK.value()
            data = findUser.toResponse()
            message = ""
        }
    }
}