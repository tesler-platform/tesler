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

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.vanilla.dto.validators.KppConstraint;
import io.tesler.vanilla.entity.VanillaCounterparty;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VanillaResidentDTO extends DataResponseDTO {

	private String country;

	private String counterpartyType;

	@SearchParameter(name = "legalPersonShortName")
	private String legalPersonName;

	private String ogrn;

	private LocalDateTime registrationDate;

	@SearchParameter
	private String inn;

	private String legalAddress;

	@SearchParameter
	@KppConstraint(message = "{error.validation.kpp}")
	private String kpp;

	private String okpo;

	private String opf;

	private String account;

	private boolean ufr;

	private String legalPersonNamebgColor;

	// Пример дриллдауна с серчспекой
	private String drllDwnWthSrchSpc;

	// Пример для карты
	private String okato;

	private String legalAddressDrillDown;
	//Поля для документации

	private Boolean testCheckbox;

	private String testInput;

	private String testText;

	// Пример числовых типов для форматирования
	private int testPercent; //процнет

	private Double testFractional; //плавающая точка

	private Double testNumber; //число

	private Double testMoney;

	private String intendModeBgColor;

	private LocalDateTime testDate;

	private LocalDateTime testDateTime;

	private LocalDateTime testDateTimeWithSeconds;

	private LocalDateTime testMonthYear;

	private String testDictionary;

	private String testPickList;

	public VanillaResidentDTO(VanillaCounterparty sourceEntity) {
		this.id = sourceEntity.getId().toString();
		this.legalPersonName = sourceEntity.getLegalPersonShortName();
		this.ogrn = sourceEntity.getOgrn();
		this.registrationDate = sourceEntity.getRegistrationDate();
		this.inn = sourceEntity.getInn();
		this.legalAddress = sourceEntity.getLegalAddress();
		this.kpp = sourceEntity.getKpp();
		this.okpo = sourceEntity.getOkpo();
		this.opf = sourceEntity.getOpfName();
		this.country = sourceEntity.getCountryName();
		this.account = sourceEntity.getAccount();
		// Пример дриллдауна с серчспекой
		this.drllDwnWthSrchSpc = "Задачи с восьмеркой";
		Random rand = new Random();

		// Пример для карты
		String[] okatoArray = {"41", "42", "90", "67", "94", "85", "39", "37", "82", "34", "69"};
		this.okato = okatoArray[rand.nextInt(10)];

		this.legalAddressDrillDown = "screen/vanilla/view/vanilla/legalResidentVanilla/" + sourceEntity.getId().toString();

		if (this.id.length() >= 1) {
			this.legalPersonNamebgColor = "#f9ec49";
		}
		if (this.id.length() >= 2) {
			this.legalPersonNamebgColor = "#ff9999";
		}

		this.intendModeBgColor = "#E9E967";

		// Поля для документации
		this.testInput = "Текст";
		this.testText = "Много текста";

		this.testCheckbox = true;
		this.testDictionary = "Самостоятельное задание";
		this.testPickList = null;
		// Пример числовых типов
		int SCALE = 100;
		this.testFractional = SCALE * rand.nextDouble();
		this.testNumber = -SCALE + 2 * SCALE * rand.nextDouble();
		this.testPercent = rand.nextInt(SCALE);
		this.testMoney = 123456789012.89;
		// Пример типов дата
		this.testDate = sourceEntity.getRegistrationDate();
		this.testDateTime = sourceEntity.getRegistrationDate();
		this.testDateTimeWithSeconds = sourceEntity.getRegistrationDate();
		this.testMonthYear = sourceEntity.getRegistrationDate();
	}

}
