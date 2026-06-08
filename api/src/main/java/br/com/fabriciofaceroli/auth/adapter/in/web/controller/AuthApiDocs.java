package br.com.fabriciofaceroli.auth.adapter.in.web.controller;

import br.com.fabriciofaceroli.auth.adapter.in.web.dto.LoginRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.RegisterRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "Autenticação e gestão de usuários administrativos")
public interface AuthApiDocs {

    @Operation(
            summary = "Login de usuário administrador",
            description = "Valida e-mail e senha, grava o JWT em cookie HttpOnly/Secure/SameSite=Strict e retorna os dados do usuário autenticado."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos")
    @ApiResponse(responseCode = "403", description = "Usuário inativo")
    ResponseEntity<UserResponse> login(LoginRequest request);

    @Operation(
            summary = "Registrar novo usuário administrador",
            description = "Cria um novo usuário com role ADMIN. Requer autenticação com role ADMIN."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Acesso negado - requer role ADMIN")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    ResponseEntity<UserResponse> register(RegisterRequest request);
}
