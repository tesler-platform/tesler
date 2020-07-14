/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.dto;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dto.Lov;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.impl.BooleanValueProvider;
import io.tesler.core.util.filter.provider.impl.DateValueProvider;
import io.tesler.core.util.filter.provider.impl.LovValueProvider;
import io.tesler.model.core.entity.ScheduledJob;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ScheduledJobDTO extends DataResponseDTO {

	@Lov(DictionaryType.SCHEDULED_SERVICES)
	@SearchParameter(provider = LovValueProvider.class, name = "service")
	private String serviceName;

	private String serviceKey;

	private String cronExpression;

	@SearchParameter(provider = DateValueProvider.class)
	private LocalDateTime lastLaunchDate;

	@Lov(DictionaryType.LAUNCH_STATUS)
	@SearchParameter(provider = LovValueProvider.class, name = "lastLaunchStatus")
	private String launchStatusCd;

	@SearchParameter(provider = BooleanValueProvider.class)
	private boolean active;

	public ScheduledJobDTO(ScheduledJob job) {
		this.id = job.getId().toString();
		this.serviceKey = Optional.ofNullable(job.getService()).map(LOV::getKey).orElse(null);
		this.serviceName = DictionaryType.SCHEDULED_SERVICES.lookupValue(job.getService());
		this.cronExpression = job.getCronExpression();
		this.launchStatusCd = DictionaryType.LAUNCH_STATUS.lookupValue(job.getLastLaunchStatus());
		this.lastLaunchDate = job.getLastLaunchDate();
		this.active = job.isActive();
	}

}
