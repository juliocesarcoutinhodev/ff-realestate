package br.com.fabriciofaceroli.auth.adapter.in.web.controller;

import br.com.fabriciofaceroli.auth.adapter.in.web.dto.LoginRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.RegisterRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.UserResponse;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "Autenticação e gestão de usuários administrativos")
public interface AuthApiDocs {

    @Operation(
            summary = "Login de usuário administrador",
            description = "Valida e-mail e senha, grava o JWT em cookie HttpOnly/Secure/SameSite=Strict e retorna os dados do usuário autenticado. O token nunca é exposto no body."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Usuário inativo")
    ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request);

    @Operation(
            summary = "Renovar sessão via refresh token",
            description = "Lê o cookie HttpOnly 'refresh_token', valida no banco, aplica rotation strategy (revoga o atual e emite novo par) e retorna os dados do usuário. Não requer Authorization header."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Sessão renovada com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Sessão expirada, token ausente, revogado ou não encontrado")
    ResponseEntity<ApiResponse<UserResponse>> refresh(String refreshTokenValue);

    @Operation(
            summary = "Registrar novo usuário administrador",
            description = "Cria um novo usuário com role ADMIN. Requer autenticação com role ADMIN."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request);
}
