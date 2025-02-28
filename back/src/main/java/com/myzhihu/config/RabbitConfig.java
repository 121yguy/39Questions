package com.myzhihu.config;

import com.myzhihu.constant.MQDeleteExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String RABBITMQ_DIRECT_ROUTING = "rabbitmq.direct.routing";
    public static final String MY_ZHI_HU_WEB_MESSAGE_TOPIC = "myzhihu.webMessage.topic";
    public static final String MY_ZHI_HU_WEB_MESSAGE_EXCHANGE = "myzhihu.webMessage.exchange";
    public static final String MY_ZHI_HU_EMAIL_TOPIC = "myzhihu.email.topic";
    public static final String MY_ZHI_HU_EMAIL_EXCHANGE = "myzhihu.email.exchange";
    public static final String MY_ZHI_HU_LIKE_TOPIC = "myzhihu.like.topic";
    public static final String MY_ZHI_HU_LIKE_EXCHANGE = "myzhihu.like.exchange";

    @Bean
    public Queue webMessageQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue(MY_ZHI_HU_WEB_MESSAGE_TOPIC,true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(MY_ZHI_HU_EMAIL_TOPIC,true);
    }

    @Bean
    public Queue likeQueue() {
        return new Queue(MY_ZHI_HU_LIKE_TOPIC,true);
    }

    @Bean
    DirectExchange webMessageExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange(MY_ZHI_HU_WEB_MESSAGE_EXCHANGE,true,false);
    }
    @Bean
    DirectExchange emailExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange(MY_ZHI_HU_EMAIL_EXCHANGE,true,false);
    }
    @Bean
    DirectExchange likeExchange() {
        return new DirectExchange(MY_ZHI_HU_LIKE_EXCHANGE, true, false);
    }
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(webMessageQueue()).to(webMessageExchange()).with(RABBITMQ_DIRECT_ROUTING);
    }

    @Bean
    Binding emailBindingDirect() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(RABBITMQ_DIRECT_ROUTING);
    }
    @Bean
    Binding likeBindingDirect() {
        return BindingBuilder.bind(likeQueue()).to(likeExchange()).with(RABBITMQ_DIRECT_ROUTING);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }


    @Bean
    DirectExchange deleteExchange() {
        return new DirectExchange(MQDeleteExchange.DELETE_EXCHANGE, true, false);
    }
    @Bean
    public Queue deleteFavorQueue() {
        return new Queue(MQDeleteExchange.DELETE_FAVOR_QUEUE,true);
    }
    @Bean
    public Queue deleteLikeQueue() {
        return new Queue(MQDeleteExchange.DELETE_LIKE_QUEUE,true);
    }
    @Bean
    public Queue deleteImageQueue() {
        return new Queue(MQDeleteExchange.DELETE_IMAGE_QUEUE, true);
    }
    @Bean
    Binding deleteLikeBindingDirect() {
        return BindingBuilder.bind(deleteLikeQueue()).to(deleteExchange()).with(MQDeleteExchange.DELETE_LIKE_ROUTING);
    }
    @Bean
    Binding deleteFavorBindingDirect() {
        return BindingBuilder.bind(deleteFavorQueue()).to(deleteExchange()).with(MQDeleteExchange.DELETE_FAVOR_ROUTING);
    }
    @Bean
    Binding deleteImageBindingDirect() {
        return BindingBuilder.bind(deleteImageQueue()).to(deleteExchange()).with(MQDeleteExchange.DELETE_IMAGE_ROUTING);
    }
}
