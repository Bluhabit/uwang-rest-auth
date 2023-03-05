/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.Constants.Permission.READ_USER
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseAuthResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.utils.GoogleAuthUtil
import com.bluehabit.budgetku.common.utils.ValidationUtil
import com.bluehabit.budgetku.common.utils.allowTo
import com.bluehabit.budgetku.common.utils.createFileName
import com.bluehabit.budgetku.common.utils.getMimeTypes
import com.bluehabit.budgetku.common.utils.getTodayDateTime
import com.bluehabit.budgetku.common.utils.getTodayDateTimeOffset
import com.bluehabit.budgetku.config.JwtUtil
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import com.bluehabit.budgetku.data.user.UserStatus.ACTIVE
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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.Period
import java.util.*


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
            user.userStatus == ACTIVE.name,
            false,
            false,
            false,
            user.userPermissions.map { SimpleGrantedAuthority(it.permissionType) }
        )

    }
    //end region

    //region user auth
    fun signInWithEmail(
        body: SignInWithEmailRequest
    ): AuthBaseResponse<UserCredentialResponse> {
        validationUtil.validate(body)

        val login = userCredentialRepository
            .findByUserEmail(
                body.email!!
            ) ?: throw UnAuthorizedException(translate("auth.user.not.exist"))

        if (!scrypt.matches(body.password, login.userPassword)) {
            throw UnAuthorizedException(translate("auth.invalid"))
        }

        if (login.userAuthProvider != BASIC.name) {
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
    ): AuthBaseResponse<UserCredentialResponse> {
        validationUtil.validate(request)
        val googleAuth = GoogleAuthUtil(environment, i18n)
        val verifyUser =
            googleAuth.getGoogleClaim(request.token!!)
        if (!verifyUser.valid) {
            throw UnAuthorizedException(verifyUser.message)
        }
        val findUser = userCredentialRepository.findByUserEmail(verifyUser.email)
            ?: throw UnAuthorizedException(translate("auth.user.not.exist"))

        if (findUser.userAuthProvider != UserAuthProvider.GOOGLE.name) {
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
    ): AuthBaseResponse<UserCredentialResponse> {
        validationUtil.validate(request)

        val isExist = userCredentialRepository.exist(request.email!!)
        if (isExist) {
            throw UnAuthorizedException(translate("auth.failed.user.exist"))
        }

        val uuid = UUID.randomUUID().toString()
        val date = getTodayDateTimeOffset()
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
            userAuthTokenProvider = "",
            userNotificationToken = "",
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

    @Transactional
    fun signUpWithGoogle(
        request: SignUpWithGoogleRequest
    ): AuthBaseResponse<UserCredentialResponse> {
        validationUtil.validate(request)

        val claim = GoogleAuthUtil(
            environment,
            i18n
        ).getGoogleClaim(request.token!!)

        if (!claim.valid) {
            throw UnAuthorizedException(claim.message)
        }

        val isExist = userCredentialRepository.exist(claim.email)
        if (isExist) {
            throw UnAuthorizedException(translate("auth.failed.user.exist"))
        }

        val uuid = UUID.randomUUID().toString()
        val date = getTodayDateTimeOffset()
        val userProfile = UserProfile(
            userId = uuid,
            userFullName = claim.fullName,
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
            userEmail = claim.email,
            userPassword = scrypt.encode(claim.email),
            userStatus = ACTIVE.name,
            userAuthProvider = BASIC.name,
            userPermissions = listOf(),
            userProfile = savedProfile,
            userAuthTokenProvider = request.token!!,
            userNotificationToken = "",
            createdAt = date,
            updatedAt = date
        )
        val savedCredential = userCredentialRepository.save(userCredential)

        return baseAuthResponse {
            code = OK.value()
            data = savedCredential.toResponse()
            message = translate("auth.success")
        }
    }

    @Transactional
    fun userVerification(
        token: String
    ): BaseResponse<List<Any>> {
        if (token.isEmpty()) {
            throw UnAuthorizedException("Verification failed")
        }

        val verificationData = userVerificationRepository.findByUserActivationToken(token)
            ?: throw UnAuthorizedException("Token not valid 1")

        if (verificationData.activationAt != null) {
            throw UnAuthorizedException("Token not valid 2")
        }

        if (Period.between(
                verificationData.createdAt.toLocalDate(),
                getTodayDateTime().toLocalDate()
            ).days > 1
        ) {
            throw UnAuthorizedException("Token not valid or expired")
        }

        val findUser = userCredentialRepository.findByIdOrNull(verificationData.userId)
            ?: throw UnAuthorizedException("User not found 3")

        userCredentialRepository.save(
            findUser.copy(
                userStatus = ACTIVE.name
            )
        )
        userVerificationRepository.save(
            verificationData.copy(
                activationAt = OffsetDateTime.now()
            )
        )

        return baseResponse {
            code = OK.value()
            data = listOf()
            message = translate("auth.verification.success")

        }
    }

    fun refreshToken(
        jwt: String?
    ): AuthBaseResponse<List<Any>> {
        if (jwt.isNullOrEmpty()) {
            throw UnAuthorizedException("Token not valid")
        }
        val claims = jwtUtil.decode(jwt)
        if (!claims.first) {
            throw UnAuthorizedException(claims.second)
        }

        val generate = jwtUtil.generateToken(claims.second)

        return baseAuthResponse {
            code = OK.value()
            data = listOf()
            message = translate("auth.success")
            token = generate
        }
    }

    fun uploadProfilePicture(
        request: UpdateProfilePictureRequest
    ): BaseResponse<UserProfileResponse> = buildResponse {
        val user = userCredentialRepository.findByUserEmail(it)
            ?: throw UnAuthorizedException(translate("auth.user.not.exist"))

        val mediaType = request.file?.getMimeTypes().orEmpty()
        val fileNAme = user.createFileName(mediaType)



        baseResponse { }
    }
    //end region

    fun getAllUsers(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<UserProfileResponse>> = buildResponse(
        allow = { it.allowTo(READ_USER) }
    ) {
        val findAll = userProfileRepository.findAll(pageable)

        baseResponse {
            code = OK.value()
            data = findAll.toResponse()
            message = translate("users.found")
        }
    }
}