package com.smis.security;

import com.smis.common.core.util.YamlPropertySourceFactory;
import com.smis.security.util.JwtUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

record Condition(long onset, String title) {
}

record User(long id, String username, String gender, long age) {
}

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WebMvcTest(TestController.class)
@ContextConfiguration(classes = {TestControllerTest.TestConfig.class})
@EnableAutoConfiguration(exclude = R2dbcAutoConfiguration.class)
@PropertySources({
        @PropertySource(value = "classpath:security-application.yml", factory = YamlPropertySourceFactory.class)
})
class TestControllerTest {

    @Configuration
    @ComponentScan(basePackages = "com.smis")
    static class TestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private SecurityConfig securityConfig;

    private final String expiredBearerToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoidG9kb191c2VybmFtZV9oZXJlIiwiZXhwIjoxNzI5NzY1NTc3LCJpYXQiOjE3Mjk3NjU1NzYsImF1dGhvcml0aWVzIjpbInVzZXJfcmVhZCIsInVzZXJfd3JpdGUiLCJyZXBvcnRfZG93bmxvYWQiXX0.U50rErI48SWTmG6SrWwokKSOBpmlXLhzU1dZvwAekKQ8D33LR3DLL78vk0DAGGisvTn6_PDrs2QwqWF0rPPDbi-lWEjrsItfwj9bPUOmK1uW0QPH7V1v2g8ycVj-4CoJFbLpkmZV02tdz5G7YdpNKiXmAmRUskSLXIPN2jSAKvH19y27VefYXYsWJf7vqumiNN1om32DCHlhucZgViEIkSNKZ6bP15OOg2wesdpKJvvHMjeAGmIkDSBGTgy8F1ge_bMaGrKfycsjEIbYjz2YAKzJmQcKFfckURjsRUB9TTrGORNuHEKjy3qxgw-iYEg7Vt_JpAQakZhhusARt3y7qA";
    private String bearerToken;

    private String username = "jean";
    private User user;
    private List<String> authorities;
    private Map<String, Object> extraClaims;

    @BeforeAll
    void init() {
        user = new User(575993, username, "female", 45);
        authorities = List.of("user_read", "user_write", "report_download");
        extraClaims = Map.of(
                "condition", new Condition(Instant.now().minus(23, ChronoUnit.DAYS).toEpochMilli(), "Covid-19"),
                "LOCATION", "Nairobi",
                "registeredAt", Instant.now().toEpochMilli());
    }

    @Order(0)
    @Test
    void contextLoads() {
    }

    @Order(1)
    @Test
    void testLoginEndpoint() throws Exception {
        mockMvc.perform(get("/test/login"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    bearerToken = "Bearer " + result.getResponse().getContentAsString();
                    log.info("{}", bearerToken);
                });
    }

    @Order(2)
    @Test
    void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/test/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("Public endpoint"));
    }

    @Order(3)
    @Test
    @WithMockUser(roles = "USER")
    void testPrivateEndpointWithUser() throws Exception {
        mockMvc.perform(get("/test/private"))
                .andExpect(status().isOk())
                .andExpect(content().string("Private endpoint"));
    }

    @Order(4)
    @Test
    void testPrivateEndpointWithoutUser() throws Exception {
        mockMvc.perform(get("/test/private"))
                .andExpect(status().isUnauthorized());
    }

    @Order(5)
    @Test
    void testPrivateReadUser() throws Exception {
        mockMvc.perform(get("/test/read-user")
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Private read user endpoint"));
    }

    @Order(6)
    @Test
    void testPrivateWriteUser() throws Exception {
        mockMvc.perform(get("/test/write-user")
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Private write user endpoint"));
    }

    @Order(7)
    @Test
    void testPrivateDownloadReport() throws Exception {
        mockMvc.perform(get("/test/download-report")
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Private download report endpoint"));
    }

    @Order(8)
    @Test
    void testPrivateRand_Forbidden() throws Exception {
        mockMvc.perform(get("/test/rand")
                        .header("Authorization", bearerToken))
                .andExpect(status().isForbidden());
    }

    @Order(9)
    @Test
    void testExpiredBearerToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/test/download-report")
                        .header("Authorization", "Bearer " + expiredBearerToken))
                .andExpect(status().isUnauthorized());
    }

    @Order(10)
    @Test
    void testJwtUtil_generateJwt_fail() {
        assertThrows(ConstraintViolationException.class, () -> jwtUtil.generateJwt(3600L, null, null, null, null));
        assertThrows(ConstraintViolationException.class, () -> jwtUtil.generateJwt(3600L, "jean", null, null, null));

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.generateJwt(3600L, username, authorities, user, Map.of("conditon", "testing", "user", "test1")));
        assertEquals("Cannot use 'authorities' or 'user' as a key in extraClaims", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.generateJwt(3600L, username, authorities, user, Map.of("conditon", "testing", "authorities", List.of("supervisor", "senior counsel"))));
        assertEquals("Cannot use 'authorities' or 'user' as a key in extraClaims", exception2.getMessage());
    }

    @Order(11)
    @Test
    void testJwtUtil_generateJwt_success() {
        String jwt1 = jwtUtil.generateJwt(3600L, username, authorities, null, null);
        log.info(jwt1);
        assertNotNull(jwt1);

        String jwt2 = jwtUtil.generateJwt(3600L, username, authorities, user, extraClaims);
        log.info(jwt2);
        assertNotNull(jwt2);
    }

    @Order(12)
    @Test
    void testPrivateWriteUserAuth() throws Exception {
        mockMvc.perform(get("/test/auth-claims")
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Private auth payload endpoint"));
    }
}
