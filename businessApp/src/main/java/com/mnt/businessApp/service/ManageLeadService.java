package com.mnt.businessApp.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.LeadDetailsVM;
import com.mnt.businessApp.viewmodel.LeadHistoryVM;
import com.mnt.businessApp.viewmodel.LeadVM;
import com.mnt.entities.businessApp.ActivityStream;
import com.mnt.entities.businessApp.Lead;

@Service
public class ManageLeadService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private JdbcTemplate jt;

	public List<LeadDetailsVM> getAllLeadDetails() {
	      List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
         List leads = sessionFactory.getCurrentSession().createQuery("FROM Lead").list(); 
         for (Iterator iterator = 
        		 leads.iterator(); iterator.hasNext();){
        	Lead lead = (Lead) iterator.next(); 
        	vms.add(new LeadDetailsVM(lead));
         }
	     
		return vms;
		
	}

	public LeadVM getLeadVMById(Long id) {
		Lead lead = getLeadById(id);
		LeadVM leadVM = new LeadVM(lead); 
		int i = 0;
		Map map = jt.queryForList("select * from generalconfig").get(0);
		int followUpReminderCount =  Integer.parseInt((String)map.get("followUpReminderCount"));
		for (ActivityStream activityStream : lead.getActivityStream()){
			if(activityStream.newDisposition2.equals("Not Contacted")){
				i++;
				if(i>followUpReminderCount){
					leadVM.isLost = true;
					break;
				}
			}
		}
		return leadVM;
	}
	
	public Lead getLeadById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Lead lead = null;
		Query query = session.createQuery("FROM Lead Where id = :id"); 
		query.setParameter("id", id);
		List results = query.list();
		lead = (Lead) results.get(0);
		return lead;
	}
	
	

	public void updateLead(LeadVM vm) {
		Lead lead = getLeadById(vm.getId());
		ActivityStream activityStream = new ActivityStream();
		activityStream.setNewDisposition1(vm.getDisposition1());
		activityStream.setNewDisposition2(vm.getDisposition2());
		activityStream.setOldDisposition1(lead.getDisposition1());
		activityStream.setOldDisposition2(lead.getDisposition2());
		activityStream.setCreatedDate(new Date());
		sessionFactory.getCurrentSession().save(activityStream);
		lead.setDisposition1(vm.getDisposition1());
		lead.setDisposition2(vm.getDisposition2());
		lead.setFollowUpDate(vm.getFollowUpDate());
		lead.setReason(vm.getReason());
		lead.addActivityStream(activityStream);
		sessionFactory.getCurrentSession().update(lead);
	}

	public List<LeadHistoryVM> getLeadHistory(Long id) {
		Lead lead = getLeadById(id);
		List<LeadHistoryVM> leadHistoryVMs = new ArrayList<LeadHistoryVM>();
		for(ActivityStream stream : lead.getActivityStream()){
			LeadHistoryVM leadHistoryVM = new LeadHistoryVM(stream);
			leadHistoryVM.setStatus(lead.getLeadDetails().getCategorization());
			leadHistoryVMs.add(leadHistoryVM);
		}
		return leadHistoryVMs;
	}

	public List<LeadDetailsVM> getAllEscalatedLeadDetails() {
		 List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
         List leads = sessionFactory.getCurrentSession().createQuery("FROM Lead where disposition1 = 'Escalated'").list(); 
         for (Iterator iterator = 
        		 leads.iterator(); iterator.hasNext();){
        	Lead lead = (Lead) iterator.next(); 
        	vms.add(new LeadDetailsVM(lead));
         }
	     
		return vms;
	}

	public List<LeadDetailsVM> getFollowUpLeads() {
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
        List leads = sessionFactory.getCurrentSession().createQuery("FROM Lead where disposition1 = 'Escalated' and DATE(followUpDate) = CURRENT_DATE()").list(); 
        for (Iterator iterator = 
       		 leads.iterator(); iterator.hasNext();){
       	Lead lead = (Lead) iterator.next(); 
       	vms.add(new LeadDetailsVM(lead));
        }
	     
		return vms;
	}
	
	

}
