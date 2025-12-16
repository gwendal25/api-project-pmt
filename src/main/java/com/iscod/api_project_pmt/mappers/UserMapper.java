package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.TaskUserDto;
import com.iscod.api_project_pmt.dtos.UserDto;
import com.iscod.api_project_pmt.dtos.UserRequest;
import com.iscod.api_project_pmt.entities.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toUser(UserRequest userRequest);
    TaskUserDto toTaskUserDto(User user);
    List<TaskUserDto> toTaskUserDtoList(List<User> users);
}
