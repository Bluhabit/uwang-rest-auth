package com.bluehabit.blu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
    classes = {ServletWebServerFactoryAutoConfiguration.class},
    webEnvironment = RANDOM_PORT,
    properties = {"spring.cloud.config.enabled=false"}
)
@ExtendWith(MockitoExtension.class)
class EurekaApplicationTests {

    @Test
    public void load() throws Exception {
    }

}
