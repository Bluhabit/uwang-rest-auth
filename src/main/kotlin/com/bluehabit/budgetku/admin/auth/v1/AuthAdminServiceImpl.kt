package com.bluehabit.budgetku.admin.auth.v1

import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.config.tokenMiddleware.JwtUtil
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.data.role.RoleRepository
import com.bluehabit.budgetku.data.user.LoginRequest
import com.bluehabit.budgetku.data.user.UserRepository
import com.bluehabit.budgetku.data.user.UserResponse
import com.bluehabit.budgetku.data.user.toResponse
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class AuthAdminServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val validationUtil: ValidationUtil,
    private val jwtUtil: JwtUtil
) : AuthAdminService, UserDetailsService {

    override fun signIn(
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

        val token = jwtUtil.generateToken(login.userEmail)

        return AuthBaseResponse(
            code = OK.value(),
            data = login.toResponse(),
            message = "Sign in success!",
            token = token
        )

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
}