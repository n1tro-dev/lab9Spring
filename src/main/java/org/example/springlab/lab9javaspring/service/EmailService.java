package org.example.springlab.lab9javaspring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendSimpleEmail(String toAddress, String subject, String message) {

        log.debug("Асинхронная попытка отправки письма на адрес: {}", toAddress);

        try {
//            StopWatch timer = new StopWatch();
//            timer.start();
//            long start = System.currentTimeMillis();

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toAddress);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailMessage.setFrom("noreply@myapp.com");

            javaMailSender.send(mailMessage);
            log.info("Email успешно отправлен пользователю: {}", toAddress);

//            long end = System.currentTimeMillis();
//            System.out.println("Время работы метода: " + (end - start) + "мс");
//            timer.stop();
//            System.out.println(timer.prettyPrint());
        } catch (Exception e){
            log.error("Ошибка при отправке письма на {}. Причина: {}", toAddress, e.getMessage());
        }
    }
}
