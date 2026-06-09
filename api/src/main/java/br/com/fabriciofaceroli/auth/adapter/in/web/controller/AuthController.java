package br.com.fabriciofaceroli.auth.adapter.in.web.controller;

import br.com.fabriciofaceroli.auth.adapter.in.web.dto.LoginRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.RegisterRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.UserResponse;
import br.com.fabriciofaceroli.auth.adapter.in.web.mapper.AuthWebMapper;
import br.com.fabriciofaceroli.auth.application.port.in.LoginUserPort;
import br.com.fabriciofaceroli.auth.application.port.in.LogoutPort;
import br.com.fabriciofaceroli.auth.application.port.in.RefreshSessionPort;
import br.com.fabriciofaceroli.auth.application.port.in.RegisterUserPort;
import br.com.fabriciofaceroli.auth.application.port.out.AuthCookiePort;
import br.com.fabriciofaceroli.shared.exception.UnauthorizedException;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApiDocs {

    private final LoginUserPort loginUserPort;
    private final RefreshSessionPort refreshSessionPort;
    private final LogoutPort logoutPort;
    private final RegisterUserPort registerUserPort;
    private final AuthWebMapper authWebMapper;
    private final AuthCookiePort authCookiePort;

    public AuthController(LoginUserPort loginUserPort,
                          RefreshSessionPort refreshSessionPort,
                          LogoutPort logoutPort,
                          RegisterUserPort registerUserPort,
                          AuthWebMapper authWebMapper,
                          AuthCookiePort authCookiePort) {
        this.loginUserPort = loginUserPort;
        this.refreshSessionPort = refreshSessionPort;
        this.logoutPort = logoutPort;
        this.registerUserPort = registerUserPort;
        this.authWebMapper = authWebMapper;
        this.authCookiePort = authCookiePort;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {
        var result = loginUserPort.login(authWebMapper.toCommand(request));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookiePort.createAuthCookie(result.accessToken()))
                .header(HttpHeaders.SET_COOKIE, authCookiePort.createRefreshCookie(result.refreshToken()))
                .body(ApiResponse.success("Login realizado com sucesso.", authWebMapper.toResponse(result.user())));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<UserResponse>> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenValue) {
        if (refreshTokenValue == null) {
            throw new UnauthorizedException("Sessão expirada. Faça login novamente.");
        }
        var result = refreshSessionPort.refresh(refreshTokenValue);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookiePort.createAuthCookie(result.accessToken()))
                .header(HttpHeaders.SET_COOKIE, authCookiePort.createRefreshCookie(result.refreshToken()))
                .body(ApiResponse.success("Sessão renovada com sucesso.", authWebMapper.toResponse(result.user())));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenValue) {
        logoutPort.logout(refreshTokenValue);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookiePort.createLogoutCookie())
                .header(HttpHeaders.SET_COOKIE, authCookiePort.createLogoutRefreshCookie())
                .build();
    }

    @Override
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        var user = registerUserPort.register(authWebMapper.toCommand(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuário registrado com sucesso.", authWebMapper.toResponse(user)));
    }
}
