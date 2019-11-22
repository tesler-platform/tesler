/*-
 * #%L
 * IO Tesler - Model Core
 * %%
 * Copyright (C) 2018 - 2019 Tesler Contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package io.tesler.model.core.entity;

import io.tesler.api.data.dictionary.LOV;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SCHEDULED_JOB")
public class ScheduledJob extends BaseEntity {

	@Column(name = "SERVICE_NAME")
	private LOV service;

	@Column(name = "CRON_EXPRESSION")
	private String cronExpression;

	@Column(name = "LAST_LAUNCH_DATE")
	private LocalDateTime lastLaunchDate;

	@Column(name = "LAST_SUCCESS_LAUNCH_DATE")
	private LocalDateTime lastSuccessLaunchDate;

	@Column(name = "LAST_LAUNCH_STATUS_CD")
	private LOV lastLaunchStatus;

	@Column(name = "LAUNCH_CNT")
	private long launchCnt;

	@Column(name = "LAUNCH_FAILED_CNT")
	private long launchFailedCnt;

	@Column(name = "LAUNCH_FAILED_LAST_CNT")
	private long launchFailedLastCnt;

	@Column(name = "LAST_SUCCESS_LAUNCH_DURATION")
	private Long lastSuccessLaunchDuration;

	@Column(name = "ACTIVE_FLAG")
	private boolean active;

	@Column(name = "SYSTEM_FLAG")
	private boolean system;

	@Column(name = "LAUNCH_ON_CREATE")
	private boolean launchOnCreate;

	@OneToMany(mappedBy = "job")
	private List<ScheduledJobParam> params;

}
