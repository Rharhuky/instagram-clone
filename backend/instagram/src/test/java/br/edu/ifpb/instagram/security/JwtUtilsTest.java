package br.edu.ifpb.instagram.security;

import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static br.edu.ifpb.instagram.security.util.Token.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest implements SecurityTest{

    @Mock
    private Authentication authentication;

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void validateToken_WithValidToken_ReturnsTrue() {
        String token = validToken();
        var sut = jwtUtils.validateToken(token);
        assertThat(sut).isTrue();
    }

    @Test
    void validateToken_WithInvalidToken_ReturnsFalse() {
        String token = invalidToken();
        var sut = jwtUtils.validateToken(token);
        assertThat(sut).isFalse();
    }

    @Test
    void validateToken_WithEmptyToken_ReturnsFalse() {
        String token = "";
        var sut = jwtUtils.validateToken(token);
        assertThat(sut).isFalse();
    }

    @Test
    void validateToken_WithExpiredToken_ReturnsFalse() {
        String token = expiredToken();
        var sut = jwtUtils.validateToken(token);
        assertThat(sut).isFalse();
    }

    @Test
    void getUsernameFromToken_WithValidToken_ReturnsUsername() {
        var tokenUsername = "e18cabab-d672-46f7-b962-87497a20777a";
        String token = validToken();

        var sut = jwtUtils.getUsernameFromToken(token);

        assertThat(sut).isEqualTo(tokenUsername);
    }

    @Test
    void getUsernameFromToken_WithInValidToken_ThrowsException() {
        String token = invalidToken();
        assertThatThrownBy(() ->jwtUtils.getUsernameFromToken(token)).isInstanceOf(SignatureException.class)
                .hasMessageContaining("JWT signature does not match locally computed signature");
    }

    @ParameterizedTest(name = "[{index}] - {arguments}")
    @MethodSource(value = "invalidTokensArgumentsProvider")
    void getUsernameFromToken_WithInValidToken_ThrowsException(String token){
        assertThatThrownBy(() ->jwtUtils.getUsernameFromToken(token)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JWT String argument cannot be null or empty");
    }

    static Stream<Arguments> invalidTokensArgumentsProvider(){
        return Stream.of(
                Arguments.of(""),
                Arguments.of((String) null)
        );
    }

    @Test
    void generateToken_WithInstantiatedAuthentication_ReturnsValidToken() {
        when(authentication.getName()).thenReturn("Rharhuandrew");

        var sut = jwtUtils.generateToken(authentication);
        assertThat(sut).isNotBlank();
        var tokenParts = sut.split("\\.");
        assertThat(tokenParts).hasSize(3);

        var validatedToken = jwtUtils.validateToken(sut);
        assertThat(validatedToken).isTrue();

        Predicate<String> p = token -> jwtUtils.validateToken(token);
        assertThat(p).accepts(sut);
    }

}