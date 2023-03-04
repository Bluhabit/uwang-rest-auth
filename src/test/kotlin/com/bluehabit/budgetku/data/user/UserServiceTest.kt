package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.Constants
import com.bluehabit.budgetku.common.utils.ValidationUtil
import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.config.JwtUtil
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import com.bluehabit.budgetku.data.user.userActivity.UserActivityRepository
import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var userCredentialRepository: UserCredentialRepository
    @Mock
    lateinit var userActivityRepository: UserActivityRepository
    @Mock
    lateinit var validationUtil: ValidationUtil
    @Mock
    lateinit var jwtUtil: JwtUtil
    @Mock
    lateinit var environment: Environment

    lateinit var userCredential: UserCredential

    private val bcrypt = BCryptPasswordEncoder(Constants.BCrypt.STRENGTH)

    @BeforeEach
    fun setUp() {
        userCredential = UserCredential(
            userId = "26ff6c62-a447-4e7f-941e-e3c866bd69bc",
            userEmail = "admin@bluehabit.com",
            userPassword = bcrypt.encode("1234"),
            userFullName = "Admin blue habit",
            userAuthProvider = BASIC.name,
            userAuthTokenProvider="",
            userDateOfBirth = OffsetDateTime.now(),
            userCountryCode = "id",
            userPhoneNumber = "4567890",
            userProfilePicture ="",
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now(),
        )
    }

    @AfterEach
    fun tearDown() {

    }

    @Test
    fun `load user for Authorization header`() {

        //user doesn't exist
        given(userCredentialRepository.findByUserEmail(userCredential.userEmail)).willAnswer { null }

        assertEquals(userService.loadUserByUsername(userCredential.userEmail), null)

        given(userCredentialRepository.findByUserEmail(userCredential.userEmail)).willAnswer { userCredential }

        assertEquals(
            userService.loadUserByUsername(userCredential.userEmail)?.username,
            userCredential.userEmail
        )
    }

    @Test
    fun `sign in with email and password`() {
        val request = SignInWithEmailRequest(
            email = userCredential.userEmail,
            password = "1234"
        )
        //success sign in
        given(userCredentialRepository.findByUserEmail(userCredential.userEmail)).willAnswer {  userCredential}
        given(jwtUtil.generateToken(userCredential.userEmail)).willAnswer { "Ini token" }


        assertEquals(
            AuthBaseResponse(
                code = HttpStatus.OK.value(),
                data = userCredential.toResponse(),
                message = "Sign In Success!",
                token = "Ini token"
            ),
            userService.signInWithEmailAndPassword(
                SignInWithEmailRequest(
                    email = userCredential.userEmail,
                    password = "1234"
                )
            )
        )

        //failed
        given(userCredentialRepository.findByUserEmail(userCredential.userEmail)).willAnswer {
            throw UnAuthorizedException("")
        }

        assertThrows(
            UnAuthorizedException::class.java
        ){
            userService.signInWithEmailAndPassword(
                request
            )
        }

        //validation error
        given(validationUtil.validate(request)).willAnswer { throw BadRequestException("")  }
        assertThrows(
            BadRequestException::class.java
        ){
            userService.signInWithEmailAndPassword(request)
        }
    }

    @Test
    fun `sign in with google`() {
    }
}