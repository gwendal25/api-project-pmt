package com.iscod.api_project_pmt.dtos.projectuser;

import com.iscod.api_project_pmt.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectUserIdRequest {
    private Long userId;
    private UserRole userRole;
}
