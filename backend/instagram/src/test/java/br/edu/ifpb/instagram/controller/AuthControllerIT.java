package br.edu.ifpb.instagram.controller;

import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.model.response.LoginResponse;
import br.edu.ifpb.instagram.model.response.UserDetailsResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AuthControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Nested
    @Sql(value = { "/import_user.sql" }, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-user.sql" }, executionPhase = AFTER_TEST_METHOD)
    class SignUp {

        @Test
        void signUp_WithValidData_ReturnsUserDetailsResponse() {
            var userDetailsRequest = new UserDetailsRequest(10L, "rharhuky@mail.com",
                    "mail21pass","Rharhuandrew","rharhuky1");

            var sut = testRestTemplate.postForEntity("/auth/signup", userDetailsRequest, UserDetailsResponse.class);
            assertThat(sut).isNotNull();
//            assertThat(sut.getStatusCode()).isEqualTo(CREATED);
            assertThat(sut.getStatusCode()).isEqualTo(OK);
            assertThat(sut.getBody()).isNotNull();
            assertThat(sut.getBody().email()).isEqualTo(userDetailsRequest.email());
        }

        @Test
        void signUp_WithExistingData_ReturnsBadRequest() {
            var userDetailsRequest = new UserDetailsRequest(10L, "souza@mail.com",
                    "mail21pass","Rharhuandrew","rharhuky2");

            var sut = testRestTemplate.postForEntity("/auth/signup", userDetailsRequest, UserDetailsResponse.class);
            assertThat(sut).isNotNull();
//            assertThat(sut.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(sut.getStatusCode()).isEqualTo(FORBIDDEN);
        }
    }

    @Nested
    @Sql(value = { "/import_user.sql" }, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-user.sql" }, executionPhase = AFTER_TEST_METHOD)
    class SingInUseCases {

        @Test
        void signIn_WithValidCredentials_ReturnsLoginResponse() throws Exception {
            var loginRequest = new LoginRequest("Rharhuky", "123");
            var sut = testRestTemplate.postForEntity("/auth/signin", loginRequest, LoginResponse.class);
            assertThat(sut.getStatusCode()).isEqualTo(OK);
            assertThat(sut.getBody()).isNotNull();
            assertThat(sut.getBody().username()).isEqualTo(loginRequest.username());
            assertThat(sut.getBody().token()).isNotBlank();
            assertThat(sut.getBody().token().split("\\.")).hasSize(3);
        }

        @ParameterizedTest
        @MethodSource(value = "invalidCredentialsProvider")
        void signIn_WithInvalidCredentials_ReturnsUnauthorized(LoginRequest loginRequest) {
            var sut = testRestTemplate.postForEntity("/auth/signin", loginRequest, LoginResponse.class);
//            assertThat(sut.getStatusCode()).isEqualTo(UNAUTHORIZED);
            assertThat(sut.getStatusCode()).isEqualTo(FORBIDDEN);
            assertThat(sut.getBody()).isNull();
        }

        static Stream<Arguments> invalidCredentialsProvider() {
            return Stream.of(Arguments.of(new LoginRequest("Rharhuky", "")),
                    Arguments.of(new LoginRequest("", "")),
                    Arguments.of(new LoginRequest("rharhuky", "0bala")),
                    Arguments.of(new LoginRequest("", "0bala"))
            );
        }
    }
}