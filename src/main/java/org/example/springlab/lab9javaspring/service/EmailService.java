package org.example.springlab.lab9javaspring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendSimpleEmail(String toAddress, String subject, String message) {
        try {
            StopWatch timer = new StopWatch();
            timer.start();
            long start = System.currentTimeMillis();

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toAddress);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailMessage.setFrom("noreply@myapp.com");

            javaMailSender.send(mailMessage);
            System.out.println("Email sent successfully to " + toAddress);

            long end = System.currentTimeMillis();
            System.out.println("Время работы метода: " + (end - start) + "мс");
            timer.stop();
            System.out.println(timer.prettyPrint());
        } catch (Exception e){
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}
