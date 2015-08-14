package com.mnt.businessApp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.mnt.businessApp.engine.AllotmentEngineCache;

@Configuration
@EnableScheduling
public class EscalationSchedular {

	@Autowired
	private SchedularService schedularService;
	
	@Autowired
	private AssignLeadsService assignLeadsService;

	@Autowired
	JdbcTemplate jt;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void onSchedule() {
		System.out.println("The time is now " + dateFormat.format(new Date()));
		Map<String, Map<String, Map<String, List<Long>>>> map = new HashMap<String, Map<String,Map<String,List<Long>>>>();
		try{
			AllotmentEngineCache allotmentEngineCache = AllotmentEngineCache.getInstance();
			map.put("product", allotmentEngineCache.productCache);
			map.put("zipCode", allotmentEngineCache.zipCache);
		} catch (Exception e){
			AllotmentEngineCache.invalidate();
			AllotmentEngineCache.build(assignLeadsService.getZipCodeUserMapping(), assignLeadsService.getProductUserMapping());
		}
		schedularService.escalationScheduler();
	}

}
