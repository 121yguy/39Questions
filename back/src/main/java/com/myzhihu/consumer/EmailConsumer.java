package com.myzhihu.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myzhihu.config.RabbitConfig;
import com.myzhihu.domain.entity.Email;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.mail.SimpleMailMessage;

import java.nio.charset.StandardCharsets;

@Component
public class EmailConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(RabbitConfig.MY_ZHI_HU_EMAIL_TOPIC),
                    exchange = @Exchange(RabbitConfig.MY_ZHI_HU_EMAIL_EXCHANGE)
            )
    )
    @RabbitHandler(isDefault = true)
    public void sendMail(Message message) throws JsonProcessingException {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        Email email = new ObjectMapper().readValue(json, Email.class);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email.getRecipient());
        msg.setSubject(email.getSubject());
        msg.setText(email.getText());
        msg.setFrom("1064583426@qq.com");
        mailSender.send(msg);
    }
}
