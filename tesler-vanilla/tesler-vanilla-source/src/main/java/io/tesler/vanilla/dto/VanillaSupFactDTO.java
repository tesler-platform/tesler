/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.dto;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.vanilla.entity.VanillaSupervisoryFact;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VanillaSupFactDTO extends DataResponseDTO {

	// Дата выявления
	private LocalDateTime detectionDate;

	// Инициатор
	private String initiator;

	// Краткое описание
	private String name;

	// Дата совершения
	private LocalDateTime commissionDate;

	// Статус
	private String status;

	// Приоритет
	private String priority;

	//Некий показатель
	private Number someIndex;

	//Некий показатель
	private String someDrillDown;

	public VanillaSupFactDTO(VanillaSupervisoryFact supervisoryFact) {
		this.id = supervisoryFact.getId().toString();
		this.detectionDate = supervisoryFact.getDetectionDate();
		this.initiator =
				supervisoryFact.getInitiator() != null ? supervisoryFact.getInitiator().getLastName() + " " + supervisoryFact
						.getInitiator().getFirstName() : null;
		this.name = supervisoryFact.getName();
		this.commissionDate = supervisoryFact.getCommissionDate();
		this.status = DictionaryType.SUPERVISORY_FACT_STATUS.lookupValue(supervisoryFact.getStatus());
		this.priority = DictionaryType.TASK_PRIORITY.lookupValue(supervisoryFact.getPriority());
		this.someIndex = Math.random() * 100;
		this.someDrillDown = "screen/vanilla/view/vanilla2//legalResidentVanilla/?filters=eyJsZWdhbFJlc2lkZW50VmFuaWxsYSI6ICJsZWdhbFBlcnNvbk5hbWUuY29udGFpbnM90JDQniJ9";
	}

}
