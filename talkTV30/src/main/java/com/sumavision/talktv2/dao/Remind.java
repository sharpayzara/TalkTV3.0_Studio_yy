package com.sumavision.talktv2.dao;

import org.litepal.crud.DataSupport;

import com.sumavision.talktv2.bean.VodProgramData;

public class Remind extends DataSupport {
	private long cpId;
	private String channelId;
	private String cpName;
	private String channelName;
	private String channelLogo;
	private String cpDate;
	private String startTime;

	public long getCpId() {
		return cpId;
	}

	public void setCpId(long cpId) {
		this.cpId = cpId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelLogo() {
		return channelLogo;
	}

	public void setChannelLogo(String channelLogo) {
		this.channelLogo = channelLogo;
	}

	public String getCpDate() {
		return cpDate;
	}

	public void setCpDate(String cpDate) {
		this.cpDate = cpDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public static Remind get(VodProgramData program) {
		Remind r = new Remind();
		r.cpId = program.cpId;
		r.channelId = program.channelId;
		r.cpName = program.cpName == null ? "" : program.cpName;
		r.channelLogo = program.channelLogo == null ? "" : program.channelLogo;
		r.cpDate = program.cpDate == null ? "" : program.cpDate;
		r.startTime = program.startTime == null ? "" : program.startTime;
		r.channelName = program.channelName == null ? "" : program.channelName;
		return r;
	}
}
