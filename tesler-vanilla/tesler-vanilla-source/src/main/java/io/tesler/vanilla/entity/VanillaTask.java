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
import io.tesler.model.core.entity.FileEntity;
import io.tesler.model.core.entity.User;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@Table(name = "TASK_VANILLA")
public class VanillaTask extends BaseEntity {

	// Название задачи
	@Column(length = 100)
	private String name;

	// Поднадзорная организация
	@ManyToOne
	@JoinColumn(name = "vised_org_id", nullable = false)
	private VanillaCounterparty superVisedOrg;

	// Отчетный период
	private LOV reportPeriod;

	// Отчетная дата
	private LocalDateTime reportDate;

	// Тип задачи
	private LOV taskType;

	// Задание
	@Column(length = 1000)
	private String job;

	// Источники - задачи (Источники, Связанные объекты)
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH})
	@JoinTable(name = "SOURCE_DICTS_TASK_VANILLA", joinColumns = {@JoinColumn(name = "task_id")}, inverseJoinColumns = {
			@JoinColumn(name = "source_dict_id")})
	@Fetch(FetchMode.SUBSELECT)
	private Set<VanillaSourceDict> sourceDicts;

	// Дата создания
	@Column(nullable = false)
	private LocalDateTime createDate;

	// Инициатор
	@ManyToOne
	@JoinColumn(name = "initiator_Id", nullable = false)
	private User initiator;

	// Исполнитель
	@ManyToOne
	@JoinColumn(name = "executor_Id")
	private User executor;

	// Статус задачи
	private LOV taskStatus;

	/**
	 * Описание
	 */
	private String description;

	/**
	 * Дата начала
	 */
	private LocalDateTime startDate;

	/**
	 * Дата окончания
	 */
	private LocalDateTime endDate;

	/**
	 * Срок исполнения
	 */
	private LocalDateTime dueDate;

	// Важность, приоритет
	private LOV priority;

	// Дата изменения статуса
	private LocalDateTime statusChangeDate;

	// Плановая дата исполнения
	@Column(nullable = false)
	private LocalDateTime planDate;

	// Период исполнения в днях
	private Integer executionPeriod;

	// Календарные/Рабочие дни
	private LOV dayType;

	// Периодичность задач
	private LOV periodicalType;

	// Подтверждение исполнения
	private Boolean isExecute;

	// Флаг недоступности БК
	private Boolean bcDisabledFlg;

	// Надзорный мониторинг
	private Boolean supervisedMonitor;

	// Результат
	@Column(length = 1000)
	private String result;

	// Категория задачи
	private LOV taskCategory;

	@ManyToOne
	@JoinColumn(name = "file_Id")
	private FileEntity fileEntity;

	@ManyToOne
	@JoinColumn(name = "sourced_file_Id")
	private VanillaFileEntity vanillaFileEntity;

	@ManyToOne
	@JoinColumn(name = "user_Id")
	private User user;

	// Вид деятельности
	private LOV activityType;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String comboConditionTest;

	private Double moneyInputTest;

	private Long numberInputTest;

	private Double decimalInputTest;

	private Long percentInputTest;

	@Column(name = "VIRTUAL_NUMBER_TEST", insertable = false, updatable = false)
	private Long virtualNumberTest;

	// Пример иконки
	private String icon;

	private String phone;

	private String postalCode;

}
