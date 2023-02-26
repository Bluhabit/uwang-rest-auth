package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.Constants
import com.bluehabit.budgetku.common.GoogleAuthUtil
import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.common.model.baseAuthResponse
import com.bluehabit.budgetku.common.translate
import com.bluehabit.budgetku.config.tokenMiddleware.JwtUtil
import com.bluehabit.budgetku.data.role.RoleRepository
import com.bluehabit.budgetku.data.userActivity.UserActivityRepository
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
    private val userActivityRepository: UserActivityRepository,
    private val validationUtil: ValidationUtil,
    private val jwtUtil: JwtUtil,
    private val environment: Environment,
    private val message: ResourceBundleMessageSource
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
            ) ?: throw UnAuthorizedException(message.translate("auth.user.not.exist"))

        if (!bcrypt.matches(
                body.password,
                login.userPassword
            )
        ) throw UnAuthorizedException(message.translate("auth.invalid"))

        val generatedToken = jwtUtil.generateToken(login.userEmail)


        return baseAuthResponse {
            code = OK.value()
            data = login.toResponse()
            message = this@UserService.message.translate("auth.success")
            token = generatedToken
        }

    }

    fun signInWithGoogle(
        request: LoginGoogleRequest
    ): AuthBaseResponse<UserResponse> {
        validationUtil.validate(request)
        val googleAuth = GoogleAuthUtil(environment,message)
        val verifyUser =
            googleAuth.getProfile(request.token!!)
        if (!verifyUser.first) throw UnAuthorizedException(verifyUser.third)
        val findUser = userRepository.findByUserEmail(verifyUser.second?.userEmail.orEmpty())
            ?: throw UnAuthorizedException(
                message.translate("auth.user.not.exist")
            )

        return baseAuthResponse {
            code = OK.value()
            data = findUser.toResponse()
            message = this@UserService.message.translate("auth.success")
        }
    }

    fun signUpWithEmail():AuthBaseResponse<UserResponse>{

        return baseAuthResponse {  }
    }
}