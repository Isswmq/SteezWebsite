package org.website.steez.model.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserAvatar {

    private MultipartFile file;
}
