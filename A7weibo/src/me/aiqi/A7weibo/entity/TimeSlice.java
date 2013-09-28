/**
 * Project: A7weibo
 * File:  me.aiqi.A7weibo.entity.TimeSlice.java
 * created at: Sep 28, 2013 2:20:47 PM
 * @author starfish
 */

package me.aiqi.A7weibo.entity;

import java.util.Calendar;
import java.util.Date;

import me.aiqi.A7weibo.util.WbUtil;

/**
 * represents a period of time, e.g. 2013.09.13 12:23:02 - 2013.09.13 15:23:25,
 * it's up to you to decide whether {@code startDate} should never late than
 * {@code endDate}
 * 
 * @author starfish
 * 
 */

public class TimeSlice {
	private Date startDate;
	private Date endDate;

	public TimeSlice() {
	}

	public TimeSlice(Date startDate, Date endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		String startDateString = WbUtil.getUserFriendlyTime(calendar);
		calendar.setTime(endDate);
		String endDateString = WbUtil.getUserFriendlyTime(calendar);

		return "TimeSlice [startDate=" + startDateString + ", endDate=" + endDateString + "]";
	}

}
