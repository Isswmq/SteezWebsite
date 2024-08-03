package org.website.steez.model.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserAvatar {

    private MultipartFile file;
}
