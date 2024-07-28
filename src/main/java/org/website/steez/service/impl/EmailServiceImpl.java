package org.website.steez.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.website.steez.dto.EmailDto;
import org.website.steez.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMailId;

    @Override
    public void sendSimpleMessage(EmailDto emailDto) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMailId);
        simpleMailMessage.setTo(emailDto.getTo());
        simpleMailMessage.setText(emailDto.getText());
        simpleMailMessage.setSubject(emailDto.getSubject());

        javaMailSender.send(simpleMailMessage);
    }
}
