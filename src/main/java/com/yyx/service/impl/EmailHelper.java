package com.yyx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class EmailHelper {
    private static final String fromId = "yummyyyx@163.com";

    private MailSender sender;

    @Autowired
    public EmailHelper(MailSender sender) {
        this.sender = sender;
    }

    public void send(String toId, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromId);
        message.setTo(toId);
        message.setSubject(subject);
        message.setText(body);
        sender.send(message);
    }
}
