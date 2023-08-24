/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka;

import com.bluehabit.eureka.component.data.UserCredentialRepository;
import com.bluehabit.eureka.controller.AuthenticationController;
import com.bluehabit.eureka.services.SignInService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

//@SpringBootTest(
//    classes = {ServletWebServerFactoryAutoConfiguration.class},
//    webEnvironment = RANDOM_PORT,
//    properties = {"spring.cloud.config.enabled=false"}
//)
@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(
    classes = {ServletWebServerFactoryAutoConfiguration.class})
@ActiveProfiles("test")
public class SignInTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    SignInService signInService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserCredentialRepository userCredentialRepository;

    @MockBean
    BCryptPasswordEncoder encoder;

    @Test
    public void shouldFailedSignIn() throws Exception {


    }
}
