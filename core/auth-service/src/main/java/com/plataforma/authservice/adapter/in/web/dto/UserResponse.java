package com.plataforma.authservice.adapter.in.web.dto;

import java.util.Set;

public record UserResponse(String id, String email, Set<String> roles) {
}