package com.bluehabit.budgetku.data.apiKey

import com.bluehabit.budgetku.common.Constants
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.data.user.User
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import com.bluehabit.budgetku.data.user.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.OffsetDateTime

@ExtendWith(
    MockitoExtension::class
)
class ApiKeyServiceTest {

    @InjectMocks
    lateinit var apiKeyService: ApiKeyService

    @Mock
    lateinit var apiKeyRepository: ApiKeyRepository

    @Mock
    lateinit var userRepository: UserRepository

    lateinit var apiKey: ApiKey
    lateinit var user: User
    private val bcrypt = BCryptPasswordEncoder(Constants.BCrypt.STRENGTH)

    val userNameAuth = UsernamePasswordAuthenticationToken(
        "admin@bluehabit.com",
        null,
        listOf()

    )

    @BeforeEach
    fun setUp() {
        apiKey = ApiKey(
            id = "26ff6c62-a447-4e7f-941e-e3c866bd69bn",
            value = "jkLBU8LMXAiklTSAHDABhsahxt5sgag",
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )
        user = User(
            userId = "26ff6c62-a447-4e7f-941e-e3c866bd69bc",
            userEmail = "admin@bluehabit.com",
            userPassword = bcrypt.encode("1234"),
            userFullName = "Admin blue habit",
            userAuthProvider = BASIC,
            userAuthTokenProvider = "",
            userDateOfBirth = OffsetDateTime.now(),
            userCountryCode = "id",
            userPhoneNumber = "4567890",
            userProfilePicture = "",
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now(),
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getAllApiKeys() {

        val auth = mock(Authentication::class.java)
        `when`(auth.principal).thenReturn(userNameAuth);
        val securityContext = mock(SecurityContext::class.java);
        `when`(securityContext.authentication).thenReturn(auth)
        `when`(securityContext.authentication.principal).thenReturn("admin@bluehabit.com")
        SecurityContextHolder.setContext(securityContext)

        given(userRepository.findByUserEmail(user.userEmail)).willAnswer {
            user
        }

        given(apiKeyRepository.findAll(Pageable.ofSize(1))).willAnswer {
            PageImpl<ApiKey>(emptyList())
        }

        assertEquals(
            baseResponse<PagingDataResponse<ApiKeyResponse>> {
                code = HttpStatus.OK.value()
                message = "Data api keys"
                data = PageImpl<ApiKey>(emptyList()).toResponse()
            },
            apiKeyService.getAllApiKeys(Pageable.ofSize(1))
        )

        given(userRepository.findByUserEmail(user.userEmail)).willAnswer { throw UnAuthorizedException("") }

        assertThrows(UnAuthorizedException::class.java){apiKeyService.getAllApiKeys(Pageable.ofSize(1))}


    }

    @Test
    fun generateApiKey() {
    }

    @Test
    fun deleteApiKey() {
    }
}