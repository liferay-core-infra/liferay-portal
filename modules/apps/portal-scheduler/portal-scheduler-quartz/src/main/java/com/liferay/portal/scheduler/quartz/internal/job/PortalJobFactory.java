/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.scheduler.quartz.internal.job;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * @author Tina Tian
 */
public class PortalJobFactory implements JobFactory {

	@Override
	public Job newJob(
			TriggerFiredBundle triggerFiredBundle, Scheduler scheduler)
		throws SchedulerException {

		JobDetail jobDetail = triggerFiredBundle.getJobDetail();

		Class<? extends Job> jobClass = jobDetail.getJobClass();

		SchedulerContext schedulerContext = scheduler.getContext();

		return (Job)schedulerContext.get(jobClass.getName());
	}

}