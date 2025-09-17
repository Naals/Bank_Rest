package com.aslan.project.bank_rest.dto;

import com.aslan.project.bank_rest.entity.User;
import com.aslan.project.bank_rest.entity.UserRole;
import lombok.*;

@Getter @Setter
public class UserDto {
    private Long id;
    private String username;
    private UserRole role;

    public static UserDto fromEntity(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setRole(u.getRoles());
        return dto;
    }
}

