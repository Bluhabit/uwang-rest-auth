package com.bluehabit.budgetku.common

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.File
import java.io.FileInputStream
import javax.annotation.PostConstruct

@Configuration
class FirebaseConfig(
    private val env: Environment
) {
    private val type = env.getProperty("googleType")
    private val projectId = env.getProperty("googleProjectId")
    private val privateKeyId = env.getProperty("googlePrivateKeyId")
    private val privateKey = env.getProperty("googlePrivateKey")
    private val clientEmail = env.getProperty("googleClientEmail")
    private val clientId = env.getProperty("googleClientId")
    private val authUri = env.getProperty("googleAuthUri")
    private val tokenUri = env.getProperty("googleTokenUri")
    private val authProviderxX509CertUrl = env.getProperty("googleAuthProviderX509CertUrl")
    private val clientX509CertUrl = env.getProperty("googleClientX509CertUrl")

    @PostConstruct
    fun initFCM() {
        val file = File("fba.json")
        file.writeText(
            """
            {
             "type": "$type",
             "project_id": "$projectId",
             "private_key_id": "$privateKeyId",
             "private_key": "$privateKey",
             "client_email": "$clientEmail",
             "client_id": "$clientId",
             "auth_uri": "$authUri",
             "token_uri": "$tokenUri",
             "auth_provider_x509_cert_url": "$authProviderxX509CertUrl",
             "client_x509_cert_url": "$clientX509CertUrl"
            }
        """.trimIndent()
        )
        val ips = FileInputStream(file)
        val opt = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(ips))
            .build()

        FirebaseApp.initializeApp(opt)

    }
}