package com.mnt.report.service;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Configuration
@EnableScheduling
public class Schedular {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	JdbcTemplate jt;

	@Scheduled(cron = "0 0 0 * * *")
	public void onSchedule() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		System.out.println("The time is now " + dateFormat.format(new Date()));
		getSchedularData();
	}


	public void getSchedularData() {
		//return sessionFactory.getCurrentSession().createQuery("FROM ReportMD1").list();
		try{
			List<Map<String, Object>> rows =  jt.queryForList("Select * from reportmd where  cast(reportmd.emailSendDate as date) = cast(Now() as date) and reportmd.isMail = true");
			for(Map map : rows) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Calendar c = Calendar.getInstance();
				c.setTime(new Date()); // Now use today date.

				switch((String)map.get("frequency")){
				case "weekly":
					c.add(Calendar.WEEK_OF_YEAR, 1); // Adding 5 days
					break;
				case "monthly":
					c.add(Calendar.MONTH, 1); // Adding 5 days
					break;
				case "daily":
					c.add(Calendar.DATE, 1);
					break;
				case "fortnightly":
					c.add(Calendar.WEEK_OF_YEAR, 2);
					break;
				case "quaterly":
					c.add(Calendar.MONTH, 4);
					break;
				}
				Object[] params = {c.getTime(),  (Long) map.get("id")};
				int[] types = { Types.DATE, Types.BIGINT};
				jt.update("UPDATE reportmd SET reportmd.emailSendDate = ? WHERE id = ?", params, types);
				//sendMail((String) map.get("userEmail"),"Email", null);
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
	}


	public void sendMail(String to, String subject, String body)
	{
		//sendSuggestPodcastNotification(to,subject);
	}

	public void sendSuggestPodcastNotification(final String to, final String subject) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
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
