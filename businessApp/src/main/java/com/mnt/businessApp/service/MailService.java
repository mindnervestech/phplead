package com.mnt.businessApp.service;


import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;


@Service
public class MailService
{
    @Autowired
    private JavaMailSender mailSender;
    
    
    
    @Autowired
    private VelocityEngine velocityEngine;
     
    public void sendMail(String to, String subject, String body)
    {
    	sendSuggestPodcastNotification(to,subject);
    }
    
    public void sendSuggestPodcastNotification(final String to, final String subject) {
	      MimeMessagePreparator preparator = new MimeMessagePreparator() {
		        @SuppressWarnings({ "rawtypes", "unchecked" })
				public void prepare(MimeMessage mimeMessage) throws Exception {
		             MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
		             message.setTo(to);
		             message.setFrom("mindnervesdemo@gmail.com");
		             message.setSubject(subject);
		             Map velocityContext = new HashMap();
		             velocityContext.put("firstName", "Yashwant");
		             velocityContext.put("lastName", "Chavan");
		             velocityContext.put("location", "Pune");
		             velocityContext.put("message", "Message");
		             
		             String text = VelocityEngineUtils.mergeTemplateIntoString(
		                     velocityEngine, "email-template.vm", "UTF-8", velocityContext);
		             message.setText(text, true);
		          }
		       };
		       mailSender.send(preparator);			
	}
 
}
