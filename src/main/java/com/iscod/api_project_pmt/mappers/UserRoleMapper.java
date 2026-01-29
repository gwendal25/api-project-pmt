package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.user.UserRoleDto;
import com.iscod.api_project_pmt.entities.ProjectUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    @Mapping(source="user.id", target="id")
    @Mapping(source="user.name", target="name")
    @Mapping(source="user.email", target="email")
    public abstract UserRoleDto toUserRoleDto(ProjectUser projectUser);
}
