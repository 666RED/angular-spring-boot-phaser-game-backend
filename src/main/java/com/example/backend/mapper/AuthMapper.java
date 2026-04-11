package com.example.backend.mapper;

import com.example.backend.domain.request.auth.RegisterRequest;
import com.example.backend.domain.request.auth.RegisterUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {
  RegisterUser toEntity(RegisterRequest request);
}
