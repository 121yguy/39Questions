package com.myzhihu.service.impl;

import com.myzhihu.config.RabbitConfig;
import com.myzhihu.domain.entity.Email;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

//    @Autowired
//    private JavaMailSender mailSender;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendEmail(String to, String subject, String text) {
        Email email = new Email();
        email.setRecipient(to);
        email.setSubject(subject);
        email.setText(text);
        rabbitTemplate.convertAndSend(RabbitConfig.MY_ZHI_HU_EMAIL_EXCHANGE, RabbitConfig.RABBITMQ_DIRECT_ROUTING, email);
    }
}

