package com.plataforma.authservice.domain.port.in;

import com.plataforma.authservice.adapter.in.web.dto.UserResponse;
import java.util.List;

public interface GetAllUsersUseCase {
    List<UserResponse> getAllUsers();
}