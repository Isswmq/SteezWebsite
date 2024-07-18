package org.website.steez.service;

import org.website.steez.dto.EmailDto;

public interface EmailService {

    void sendSimpleMessage(EmailDto emailDto);
}
