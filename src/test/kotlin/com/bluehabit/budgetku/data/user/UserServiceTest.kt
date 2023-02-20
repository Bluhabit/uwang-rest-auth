package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.Constants
import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.AuthBaseResponse
import com.bluehabit.budgetku.common.model.baseAuthResponse
import com.bluehabit.budgetku.config.tokenMiddleware.JwtUtil
import com.bluehabit.budgetku.data.role.RoleRepository
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import com.bluehabit.budgetku.data.userActivity.UserActivityRepository
import kotlinx.coroutines.delay
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.`when`
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
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
    lateinit var userRepository:UserRepository
    @Mock
    lateinit var roleRepository: RoleRepository
    @Mock
    lateinit var userActivityRepository: UserActivityRepository
    @Mock
    lateinit var validationUtil: ValidationUtil
    @Mock
    lateinit var jwtUtil: JwtUtil
    @Mock
    lateinit var environment: Environment

    lateinit var user:User

    private val bcrypt = BCryptPasswordEncoder(Constants.BCrypt.STRENGTH)

    @BeforeEach
    fun setUp() {
        user = User(
            userId = "26ff6c62-a447-4e7f-941e-e3c866bd69bc",
            userEmail = "admin@bluehabit.com",
            userPassword = bcrypt.encode("1234"),
            userFullName = "Admin blue habit",
            userAuthProvider = BASIC,
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
        given(userRepository.findByUserEmail(user.userEmail)).willAnswer { null }

        assertEquals(
            userService.loadUserByUsername(user.userEmail),
            null
        )

        given(userRepository.findByUserEmail(user.userEmail)).willAnswer { user }


        assertEquals(
            userService.loadUserByUsername(user.userEmail)?.username,
            user.userEmail
        )
    }

    @Test
    fun `sign in with email and password`() {
        //success sign in
        given(userRepository.findByUserEmail(user.userEmail)).willAnswer {  user}
        given(jwtUtil.generateToken(user.userEmail)).willAnswer { "Ini token" }


        assertEquals(
            AuthBaseResponse(
                code = HttpStatus.OK.value(),
                data = user.toResponse(),
                message = "Sign In Success!",
                token = "Ini token"
            ),
            userService.signInWithEmailAndPassword(
                LoginRequest(
                    email = user.userEmail,
                    password = "1234"
                )
            )
        )

        //failed
        given(userRepository.findByUserEmail(user.userEmail)).willAnswer {
            throw UnAuthorizedException("")
        }

        assertThrows(
            UnAuthorizedException::class.java
        ){
            userService.signInWithEmailAndPassword(
                LoginRequest(
                    email = user.userEmail,
                    password = ""
                )
            )
        }
    }

    @Test
    fun `sign in with google`() {
    }
}