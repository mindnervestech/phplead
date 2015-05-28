package com.mnt.businessApp.viewmodel;

public class GeneralConfigVM {
	public Long id;
	public String firstEscalationTime;
	public String subsequentEscalationTime;
	public String frequencyReport;
	public String followUpReminder;
	public String followUpReminderCount;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstEscalationTime() {
		return firstEscalationTime;
	}

	public void setFirstEscalationTime(String firstEscalationTime) {
		this.firstEscalationTime = firstEscalationTime;
	}
	public String getSubsequentEscalationTime() {
		return subsequentEscalationTime;
	}
	public void setSubsequentEscalationTime(String subsequentEscalationTime) {
		this.subsequentEscalationTime = subsequentEscalationTime;
	}
	public String getFrequencyReport() {
		return frequencyReport;
	}
	public void setFrequencyReport(String frequencyReport) {
		this.frequencyReport = frequencyReport;
	}
	public String getFollowUpReminder() {
		return followUpReminder;
	}
	public void setFollowUpReminder(String followUpReminder) {
		this.followUpReminder = followUpReminder;
	}
	public String getFollowUpReminderCount() {
		return followUpReminderCount;
	}
	public void setFollowUpReminderCount(String followUpReminderCount) {
		this.followUpReminderCount = followUpReminderCount;
	}
}
