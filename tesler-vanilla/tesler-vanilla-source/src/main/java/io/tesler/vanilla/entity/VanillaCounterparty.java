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

import io.tesler.model.core.entity.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Пользовательские значения
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "COUNTERPARTY_VANILLA")
public class VanillaCounterparty extends BaseEntity {

	/**
	 * Краткое наименование
	 */
	private String legalPersonShortName;

	private String opfName;

	private String okpo;

	private String countryName;

	@Column(name = "reg_date")
	private LocalDateTime registrationDate;

	private String ogrn;

	private String inn;

	private String kpp;

	/**
	 * Полное наименование на английском языке
	 */
	private String legalPersonEnName;

	/**
	 * Краткое наименование на английском языке
	 */
	private String legalPersonShortEnName;

	/**
	 * Лицевой счёт
	 */
	private String account;

	/**
	 * Юридический адрес
	 */
	private String legalAddress;

	/**
	 * Фактический адрес
	 */
	private String streetAddress;

	/**
	 * Почтовый адрес
	 */
	private String postAddress;

	/**
	 * Телефон
	 */
	private String phone;

	/**
	 * Факс
	 */
	private String fax;

	/**
	 * Адрес электронной почты
	 */
	private String email;

	/**
	 * Адрес сайта в сети интернет
	 */
	private String webSite;

	/**
	 * Наименование должности
	 */
	private String contactPosition;

	/**
	 * ФИО
	 */
	private String contactFullName;

	/**
	 * Телефон
	 */
	private String contactPhone;

	/**
	 * Адрес электронной почты
	 */
	private String contactEmail;

	/**
	 * LEI
	 */
	private String lei;

	/**
	 * КИО
	 */
	private String kio;

}
