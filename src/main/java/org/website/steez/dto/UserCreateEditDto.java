package org.website.steez.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateEditDto {

    private String email;
    private String username;
    private String rawPassword;
}
