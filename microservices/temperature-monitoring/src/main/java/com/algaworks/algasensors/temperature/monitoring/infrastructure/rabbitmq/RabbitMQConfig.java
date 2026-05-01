package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private static final String PROCESS_TEMPERATURE = "temperature-monitoring.process-temperature.v1";
    public static final String QUEUE_PROCESS_TEMPERATURE = PROCESS_TEMPERATURE + ".q";
    public static final String DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE = PROCESS_TEMPERATURE + ".dlq";
    public static final String QUEUE_ALERTING = "temperature-monitoring.alerting.v1.q";
    public static final String EXCHANGE_TEMPERATURE_RECEIVED = "temperature-processing.temperature-received.v1.e";
    public static final String TEXT_POST = "text-processor-service.post-processing.v1";
    public static final String POST_QUEUE = TEXT_POST+".q";
    public static final String POST_QUEUE_DLQ = TEXT_POST+".dlq";
    public static final String TEXT_RESULT = "post-service.post-processing-result.v1";
    public static final String RESULT_QUEUE = TEXT_RESULT+".q";
    public static final String RESULT_QUEUE_DLQ = TEXT_RESULT+".dlq";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public Queue postDlq() {
        return new Queue(POST_QUEUE_DLQ);
    }
    @Bean
    public Queue postQueue() {
        return QueueBuilder.durable(POST_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", POST_QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue resultDlq() {
        return new Queue(RESULT_QUEUE_DLQ);
    }
    @Bean
    public Queue resultQueue() {
        return QueueBuilder.durable(RESULT_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", RESULT_QUEUE_DLQ)
                .build();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public Queue queueProcessTemperature() {
        return QueueBuilder
                .durable(QUEUE_PROCESS_TEMPERATURE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE)
                .build();
    }

    @Bean
    public Queue deadLetterQueueProcessTemperature() {
        return QueueBuilder
                .durable(DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE)
                .build();
    }

    @Bean
    public Queue queueAlerting() {
        return QueueBuilder
                .durable(QUEUE_ALERTING)
                .build();
    }

    public FanoutExchange exchange() {
        return ExchangeBuilder
                .fanoutExchange(EXCHANGE_TEMPERATURE_RECEIVED)
                .build();
    }

    @Bean
    public Binding bindingProcessTemperature() {
        return BindingBuilder
                .bind(queueProcessTemperature())
                .to(exchange());
    }

    @Bean
    public Binding bindingAlerting() {
        return BindingBuilder
                .bind(queueAlerting())
                .to(exchange());
    }
}
