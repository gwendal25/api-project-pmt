package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.UserDto;
import com.iscod.api_project_pmt.dtos.UserRequest;
import com.iscod.api_project_pmt.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toUser(UserRequest userRequest);
}
