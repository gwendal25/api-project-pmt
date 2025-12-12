package com.iscod.api_project_pmt.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {
    @Email
    private String email;
    @NotBlank
    private String password;
}
