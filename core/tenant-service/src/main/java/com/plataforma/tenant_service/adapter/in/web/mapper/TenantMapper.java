package com.plataforma.tenant_service.adapter.in.web.mapper;

import com.plataforma.tenant_service.adapter.in.web.dto.CreateTenantRequest;
import com.plataforma.tenant_service.domain.model.Tenant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    Tenant toTenant(CreateTenantRequest createTenantRequest);

}
