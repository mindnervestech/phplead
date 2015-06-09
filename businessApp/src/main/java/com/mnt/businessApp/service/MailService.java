package com.mnt.businessApp.service;


import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;


@Service
public class MailService
{
    @Autowired
    private MailSender mailSender;
    
    @Autowired
    private VelocityEngine velocityEngine;
     
    public void sendMail(String to, String subject, String body)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("mindnervesdemo@gmail.com");
        message.setSubject(subject);
        Template template = velocityEngine.getTemplate("./template/email/email-template.vm" );

        Map velocityContext = new HashMap();
        velocityContext.put("firstName", "Yashwant");
        velocityContext.put("lastName", "Chavan");
        velocityContext.put("location", "Pune");
        
        String text = VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "src/main/resources/template/email/email-template.vm", "UTF-8", velocityContext);
        message.setText(text);
        
        mailSender.send(message);
    }
 
}
