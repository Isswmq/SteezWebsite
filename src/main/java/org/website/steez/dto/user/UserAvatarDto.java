package org.website.steez.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserAvatarDto {

    @NotNull(message = "Avatar must be not null.")
    private MultipartFile file;
}
