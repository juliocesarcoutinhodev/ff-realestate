package br.com.fabriciofaceroli.auth.adapter.in.web.mapper;

import br.com.fabriciofaceroli.auth.adapter.in.web.dto.LoginRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.RegisterRequest;
import br.com.fabriciofaceroli.auth.adapter.in.web.dto.UserResponse;
import br.com.fabriciofaceroli.auth.application.port.in.LoginCommand;
import br.com.fabriciofaceroli.auth.application.port.in.RegisterCommand;
import br.com.fabriciofaceroli.auth.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthWebMapper {

    RegisterCommand toCommand(RegisterRequest request);

    LoginCommand toCommand(LoginRequest request);

    UserResponse toResponse(User user);
}
