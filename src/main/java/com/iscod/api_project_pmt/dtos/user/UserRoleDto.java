package com.iscod.api_project_pmt.dtos.user;

import com.iscod.api_project_pmt.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserRoleDto {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
