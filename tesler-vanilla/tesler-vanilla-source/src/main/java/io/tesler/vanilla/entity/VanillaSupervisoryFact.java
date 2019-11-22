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

package io.tesler.vanilla.entity;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.User;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Надзорный факт
 */
@Entity
@Getter
@Setter
@Table(name = "SUPERVISORY_FACT_VANILLA")
public class VanillaSupervisoryFact extends BaseEntity {

	// Поднадзорная организация
	@ManyToOne
	@JoinColumn(name = "party_id")
	private VanillaCounterparty party;

	// Инициатор
	@ManyToOne
	@JoinColumn(name = "initiator_id")
	private User initiator;

	// Вид деятельности
	private LOV activityType;

	// Дата выявления
	private LocalDateTime detectionDate;

	// Дата совершения
	private LocalDateTime commissionDate;

	// Краткое описание
	@Column(length = 200)
	private String name;

	// Описание
	@Column(length = 1000)
	private String description;

	// Прочее
	@Column(length = 250)
	private String other;

	// Статус
	private LOV status;

	// Приоритет
	private LOV priority;

}
