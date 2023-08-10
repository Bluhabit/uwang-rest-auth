/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Component
public class MailUtil {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(
        String to,
        String subject,
        String folder,
        Map<String, Object> map
    ) throws MessagingException {
        final Locale locale = Locale.forLanguageTag("ID");
        final Context ctx = new Context(locale);
        map.forEach(ctx::setVariable);
        final String html = this.templateEngine.process("/email/" + folder +"/"+ ctx.getLocale().getLanguage(), ctx);

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage);

        mailMessage.setFrom(new InternetAddress(sender));
        helper.addTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        javaMailSender.send(mailMessage);
    }

}
