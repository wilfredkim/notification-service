package io.github.wilfred.notificationservice.service;

import io.github.wilfred.notificationservice.order.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    private final JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("Got The message from order place topic!");
        //send email
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("wilfredkim5@gmail.com");
            messageHelper.setTo(orderPlacedEvent.getEmail());
            messageHelper.setSubject(String.format("Order placed successfully ", orderPlacedEvent.getOrderNumber()));
            messageHelper.setText("This is the message");
            messageHelper.setValidateAddresses(true);

        };
        try {
            javaMailSender.send(mimeMessagePreparator);
        } catch (MailException e) {
            e.printStackTrace();
            throw  new RuntimeException("Error occurred "+e.getLocalizedMessage());
        }


    }
}
