/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.Constants.Permission
import com.bluehabit.budgetku.common.Constants.Permission.RANDOM
import com.bluehabit.budgetku.common.Constants.Permission.READ_USER
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseAuthResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.common.utils.GoogleAuthUtil
import com.bluehabit.budgetku.common.utils.ValidationUtil
import com.bluehabit.budgetku.common.utils.allowTo
import com.bluehabit.budgetku.config.JwtUtil
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import com.bluehabit.budgetku.data.user.UserStatus.WAITING_CONFIRMATION
import com.bluehabit.budgetku.data.user.userActivity.UserActivityRepository
import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import com.bluehabit.budgetku.data.user.userProfile.UserProfile
import com.bluehabit.budgetku.data.user.userProfile.UserProfileRepository
import com.bluehabit.budgetku.data.user.userVerification.UserVerification
import com.bluehabit.budgetku.data.user.userVerification.UserVerificationRepository
import jakarta.transaction.Transactional
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.env.Environment
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID


@Service
class UserService(
    private val userCredentialRepository: UserCredentialRepository,
    private val userActivityRepository: UserActivityRepository,
    private val userVerificationRepository: UserVerificationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val validationUtil: ValidationUtil,
    private val environment: Environment,
    private val i18n: ResourceBundleMessageSource,
    private val scrypt: SCryptPasswordEncoder,
    private val jwtUtil: JwtUtil
) : UserDetailsService, BaseService(
    userCredentialRepository,
    i18n
) {

    //region admin
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails? {

        val user = userCredentialRepository
            .findByUserEmail(username) ?: return null
        return User(
            username,
            user.userPassword,
            user.userPermissions.map { SimpleGrantedAuthority(it.permissionType) }
        )

    }
    //end region

    //region user auth
    fun signInWithEmailAndPassword(
        body: SignInWithEmailRequest
    ): AuthBaseResponse<UserResponse> {
        validationUtil.validate(body)

        val login = userCredentialRepository
            .findByUserEmail(
                body.email!!
            ) ?: throw UnAuthorizedException(translate("auth.user.not.exist"))

        if (!scrypt.matches(body.password, login.userPassword)) {
            throw UnAuthorizedException(translate("auth.invalid"))
        }

        if (login.userAuthProvider != UserAuthProvider.BASIC.name) {
            throw UnAuthorizedException(translate("auth.method.not.allowed"))
        }

        val generatedToken = jwtUtil.generateToken(login.userEmail)

        return baseAuthResponse {
            code = OK.value()
            data = login.toResponse()
            message = translate("auth.success")
            token = generatedToken
        }

    }

    fun signInWithGoogle(
        request: SignInWithGoogleRequest
    ): AuthBaseResponse<UserResponse> {
        validationUtil.validate(request)
        val googleAuth = GoogleAuthUtil(environment, i18n)
        val verifyUser =
            googleAuth.getCredential(request.token!!)
        if (!verifyUser.first) {
            throw UnAuthorizedException(verifyUser.third)
        }
        val findUser = userCredentialRepository.findByUserEmail(verifyUser.second?.userEmail.orEmpty())
            ?: throw UnAuthorizedException(translate("auth.user.not.exist"))

        if(findUser.userAuthProvider != UserAuthProvider.GOOGLE.name){
            throw UnAuthorizedException(translate("auth.method.not.allowed"))
        }

        return baseAuthResponse {
            code = OK.value()
            data = findUser.toResponse()
            message = translate("auth.success")
        }
    }

    @Transactional
    fun signUpWithEmail(
        request: SignUpWithEmailRequest
    ): AuthBaseResponse<UserResponse> {
        validationUtil.validate(request)

        val isExist = userCredentialRepository.exist(request.email!!)
        if(isExist){
            throw UnAuthorizedException(translate("auth.failed.user.exist"))
        }

        val uuid = UUID.fromString(request.email).toString()
        val date = OffsetDateTime.now()
        val activationToken = UUID.randomUUID().toString()
        val userProfile = UserProfile(
            userId = uuid,
            userFullName = request.fullName!!,
            userProfilePicture = null,
            userDateOfBirth = null,
            userCountryCode = "id",
            userPhoneNumber = null,
            createdAt = date,
            updatedAt = date
        )
        val savedProfile = userProfileRepository.save(userProfile)
        val userCredential = UserCredential(
            userId = uuid,
            userEmail = request.email!!,
            userPassword = scrypt.encode(request.password),
            userStatus = WAITING_CONFIRMATION.name,
            userAuthProvider = BASIC.name,
            userPermissions = listOf(),
            userProfile = savedProfile,
            userAuthTokenProvider="",
            createdAt = date,
            updatedAt = date
        )
        val savedCredential = userCredentialRepository.save(userCredential)

        //create verification
        val verification = UserVerification(
            userActivationId = uuid,
            userActivationToken = activationToken,
            userId = uuid,
            createdAt = date,
            activationAt = null

        )
        val savedVerification = userVerificationRepository.save(verification)
        //todo send email


        return baseAuthResponse {
            code = OK.value()
            data = savedCredential.toResponse()
            message = translate("auth.success")
        }
    }
    //end region

    fun getAllUsers(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<UserProfileResponse>> = buildResponse(
        allow = { it.allowTo(RANDOM) }
    ) {
        val findAll = userProfileRepository.findAll(pageable)

        baseResponse {
            code = OK.value()
            data = findAll.toResponse()
            message = translate("users.found")
        }
    }
}