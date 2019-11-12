package org.cloud.console.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/22<br>
 * <br>
 */
@Service
public class EmailService {
    private static final Logger log=LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailProperties mailProperties;

    public void sendSimpleMail(String title, String msg) throws MessagingException {
        MimeMessage message = null;
        message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailProperties.getUsername());
        helper.setTo(mailProperties.getProperties().get("recipients").split(","));
        helper.setSubject(title);
        helper.setText(msg, true);
        javaMailSender.send(message);
    }
}
