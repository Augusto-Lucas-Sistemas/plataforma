package com.plataforma.tenant_service.domain.exception;

public class TenantIllegalArgumentRequestException extends RuntimeException {
    public TenantIllegalArgumentRequestException(String message) {
        super(message);
    }
}
