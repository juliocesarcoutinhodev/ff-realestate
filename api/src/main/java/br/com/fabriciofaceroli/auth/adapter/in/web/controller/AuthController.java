package br.com.fabriciofaceroli.auth.adapter.in.web.controller;

import br.com.fabriciofaceroli.auth.adapter.in.web.dto.LoginRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.RegisterRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.UserResponse;
import br.com.fabriciofaceroli.auth.adapter.in.web.mapper.AuthWebMapper;
import br.com.fabriciofaceroli.auth.application.port.in.LoginUserPort;
import br.com.fabriciofaceroli.auth.application.port.in.RegisterUserPort;
import br.com.fabriciofaceroli.infrastructure.security.CookieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApiDocs {

    private final LoginUserPort loginUserPort;
    private final RegisterUserPort registerUserPort;
    private final AuthWebMapper authWebMapper;
    private final CookieService cookieService;

    public AuthController(LoginUserPort loginUserPort,
                          RegisterUserPort registerUserPort,
                          AuthWebMapper authWebMapper,
                          CookieService cookieService) {
        this.loginUserPort = loginUserPort;
        this.registerUserPort = registerUserPort;
        this.authWebMapper = authWebMapper;
        this.cookieService = cookieService;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = loginUserPort.login(authWebMapper.toCommand(request));
        var authCookie = cookieService.createAuthCookie(result.token());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .body(authWebMapper.toResponse(result.user()));
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        var user = registerUserPort.register(authWebMapper.toCommand(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authWebMapper.toResponse(user));
    }
}
