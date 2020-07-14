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

import static io.tesler.api.data.dictionary.DictionaryType.TASK_CATEGORY;
import static io.tesler.api.data.dictionary.DictionaryType.TASK_TYPE;
import static java.lang.Math.abs;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.TZAware;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.Lov;
import io.tesler.core.dto.multivalue.MultivalueField;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.impl.BooleanValueProvider;
import io.tesler.core.util.filter.provider.impl.LongValueProvider;
import io.tesler.vanilla.entity.VanillaTask;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VanillaDocDTO extends DataResponseDTO {

	private String legalAddressDrillDown;

	private String drllDwnWthSrchSpc;
	//Поля для документации

	@SearchParameter(name = "isExecute", provider = BooleanValueProvider.class)
	private Boolean testCheckbox;

	private String testInput;

	private String longTestInput;

	@SearchParameter(name = "description")
	private String testText;

	// Пример числовых типов для форматирования
	private Long testPercent; //процнет

	private Double testFractional; //плавающая точка

	@SearchParameter(name = "numberInputTest", provider = LongValueProvider.class)
	private Long testNumber; //число

	@SearchParameter(name = "virtualNumberTest", provider = LongValueProvider.class)
	private Long testVirtualNumber; //число (виртуальная колонка)

	private Double testMoney;

	private String ephemeral;

	private String hidden;

	@TZAware
	private LocalDateTime testDate;

	private LocalDateTime testDateTime;

	private LocalDateTime testDateTimeWithSeconds;

	private LocalDateTime testMonthYear;

	private String testDictionary;

	private String testPickList;

	private String bcColorKey1;

	private String bcColorKey2;

	private String bcColorKey3;

	private String bcColorKey4;

	private String name;

	private String forceName;

	private String errorName;

	private String result;

	@Lov(DictionaryType.TASK_TYPE)
	private String errorType;

	private LocalDateTime planDate;

	@Lov(DictionaryType.TASK_CATEGORY)
	private String taskStatus;

	private LocalDateTime disablePlanDate;

	private LocalDateTime createdDate;

	@Lov(DictionaryType.TASK_CATEGORY)
	private String disableTaskStatus;

	@Lov(DictionaryType.TASK_CATEGORY)
	private String errorCategory;

	@Lov(DictionaryType.TASK_CATEGORY)
	private String forceTaskStatus;


	private String innerDrilldown;

	private String innerDrilldownType;

	private String innerDrilldownKey;

	private String intendModeBgColor;

	private String relativeDrilldown;

	private String relativeDrilldownType;

	private String relativeDrilldownKey;

	private String relativeNewDrilldown;

	private String relativeNewDrilldownType;

	private String relativeNewDrilldownKey;

	private String absoluteDrilldown;

	private String absoluteDrilldownType;

	private String absoluteDrilldownKey;

	private String absoluteNewDrilldown;

	private String absoluteNewDrilldownType;

	private String absoluteNewDrilldownKey;

	private String dataDrilldown;

	private String dataDrilldownType;

	private String dataDrilldownKey;

	private String rowMetaDrilldown;

	private String testFileName;

	private String testFileRoName;

	private String testFileId;

	private String testSourceFileName;

	private String testSourceFileId;

	private String richTextEditor;

	private String icon;

	private Boolean bcDisabledFlg;

	private String maskedPhone;

	private String maskedPostalCode;

	private String multivalueHoverText;

	private String multivalueHoverHint;

	private MultivalueField testMultivalue;

	private String testMultivalueCount;

	public VanillaDocDTO(VanillaTask sourceEntity) {

		Random rand = new Random();
		this.id = sourceEntity.getId().toString();

		// Поля для документации
		this.testInput = sourceEntity.getName();
		this.longTestInput =
				"Пример развернутого длинного текста для колонки содержащей " +
						"сокращение из другого поля БК соотносящийся со значением ячейки: "
						+ sourceEntity.getName();
		this.testText = sourceEntity.getDescription();

		this.testCheckbox = sourceEntity.getIsExecute();
		this.testDictionary = "Самостоятельное задание";
		this.testPickList = null;
		// Пример числовых типов
		this.testFractional = sourceEntity.getDecimalInputTest();
		this.testNumber = sourceEntity.getNumberInputTest();
		this.testPercent = sourceEntity.getPercentInputTest();
		this.testMoney = sourceEntity.getMoneyInputTest();
		this.testVirtualNumber = sourceEntity.getVirtualNumberTest();
		// Пример типов дата
		this.testDate = sourceEntity.getPlanDate();
		this.testDateTime = sourceEntity.getCreateDate();
		this.testDateTimeWithSeconds = sourceEntity.getDueDate();
		this.testMonthYear = sourceEntity.getDueDate();
		this.bcDisabledFlg = sourceEntity.getBcDisabledFlg();
		this.maskedPhone = sourceEntity.getPhone();
		this.maskedPostalCode = sourceEntity.getPostalCode();
		this.name = sourceEntity.getName();
		this.result = sourceEntity.getResult();
		this.planDate = sourceEntity.getReportDate();
		this.taskStatus = TASK_CATEGORY.lookupValue(sourceEntity.getTaskCategory());

		int check = abs(rand.nextInt() % 4);
		switch (check) {
			case 0:
				this.bcColorKey1 = "#6fff4f";
				this.bcColorKey2 = "#ff4f4f";
				this.bcColorKey3 = "#e7ff4f";
				this.bcColorKey4 = "#00ff00";
				break;
			case 1:
				this.bcColorKey1 = "#ff4f4f";
				this.bcColorKey2 = "#e7ff4f";
				this.bcColorKey3 = "#00ff00";
				this.bcColorKey4 = "#6fff4f";
				break;
			case 2:
				this.bcColorKey1 = "#e7ff4f";
				this.bcColorKey2 = "#00ff00";
				this.bcColorKey3 = "#6fff4f";
				this.bcColorKey4 = "#ff4f4f";
				break;
			case 3:
				this.bcColorKey1 = "#00ff00";
				this.bcColorKey2 = "#6fff4f";
				this.bcColorKey3 = "#ff4f4f";
				this.bcColorKey4 = "#e7ff4f";
				break;
		}

		this.disableTaskStatus = this.taskStatus;
		this.disablePlanDate = this.planDate;
		this.forceTaskStatus = this.taskStatus;
		this.forceName = this.forceTaskStatus;
		this.createdDate = this.planDate;

		this.errorName = this.name;
		this.errorType = TASK_TYPE.lookupValue(sourceEntity.getTaskType());
		this.errorCategory = TASK_CATEGORY.lookupValue(sourceEntity.getTaskCategory());

		this.intendModeBgColor = "#86CFC2";

		this.innerDrilldown = "Дриллдаун";
		this.innerDrilldownKey = "screen/doc/view/errors";
		this.innerDrilldownType = DrillDownType.INNER.getValue();

		this.relativeDrilldown = "Относительный";
		this.relativeDrilldownKey = "#/screen/doc/view/errors";
		this.relativeDrilldownType = DrillDownType.RELATIVE.getValue();

		this.relativeNewDrilldown = "Относительный с новым окном";
		this.relativeNewDrilldownKey = "#/screen/doc/view/errors";
		this.relativeNewDrilldownType = DrillDownType.RELATIVE_NEW.getValue();

		this.absoluteDrilldown = "Абсолютный";
		this.absoluteDrilldownKey = "https://yandex.ru";
		this.absoluteDrilldownType = DrillDownType.EXTERNAL.getValue();

		this.absoluteNewDrilldown = "Абсолютный с новым окном";
		this.absoluteNewDrilldownKey = "https://yandex.ru";
		this.absoluteNewDrilldownType = DrillDownType.EXTERNAL_NEW.getValue();

		this.dataDrilldown = "Сыылка из данных";
		this.dataDrilldownKey = "screen/doc/view/errors";
		this.dataDrilldownType = DrillDownType.INNER.getValue();

		this.rowMetaDrilldown = "Ссылка из row-meta";

		this.multivalueHoverText = "Название комбо-поля";
		this.multivalueHoverHint = "Подсказка комбо-поля";

		this.testFileName = sourceEntity.getFileEntity() != null ? sourceEntity.getFileEntity().getFileName() : null;
		this.testFileRoName = sourceEntity.getFileEntity() != null ? sourceEntity.getFileEntity().getFileName() : null;
		this.testFileId = sourceEntity.getFileEntity() != null ? sourceEntity.getFileEntity().getId().toString() : null;
		this.testSourceFileName =
				sourceEntity.getVanillaFileEntity() != null ? sourceEntity.getVanillaFileEntity().getFileName() : null;
		this.testSourceFileId =
				sourceEntity.getVanillaFileEntity() != null ? sourceEntity.getVanillaFileEntity().getId().toString()
						: null;
		this.icon = sourceEntity.getIcon();
		this.richTextEditor = "<h1 style=\"z-index:auto;\">Большой заголовок</h1>\n" +
				" \n" +
				"<p>Необходимо транспонировать таблицу на фронте. (Поменять колонки и строки местами)</p>\n" +
				" \n" +
				"<p>Вводим <del style=\"z-index:auto;\">новый</del> тип <i>виджета</i> на <u>фронте</u>. (Например, <b>pivot</b>)</p>\n"
				+
				" \n" +
				"<h2 style=\"z-index:auto;\">Требования</h2>\n" +
				" \n" +
				"<h3 style=\"z-index:auto;\">Технические требования:</h3>\n" +
				" \n" +
				"<ol>\n" +
				"<li>Колонки и строки меняем местами (см. картинку [Транспонирование таблицы])</li>\n" +
				"<li>при изменении отображение сервисы не изменяются, данные получаем так же как и раньше, поэтому <b>пагинация будет по колонкам</b>, на 1 страницы дата с 01.09.2017 по 31.09.2017, при пагинации, как будто переворачиваем страницу (см [Пример пагинации])</li>\n"
				+
				"<li>выделение курсора для сохранения связности будет по колонкам</li>\n" +
				"<li>входные данные не изменяются</li>\n" +
				"<li>запки таблицы приходят с бекенда (таблица строится динамически)</li>\n" +
				"</ol>\n" +
				" \n" +
				"<hr />\n" +
				"<p>Функциональные требования:</p>\n" +
				" \n" +
				"<ol>\n" +
				"<li>Видеть таблицу в развернутом виде (на весь экран)</li>\n" +
				"<li>при пагинации листаем колонки (см [Пример пагинации])</li>\n" +
				"<li>\n" +
				"<p style=\"z-index:auto;\"><del style=\"z-index:auto;\">??? показывать маркеры роста/уменьшения </del></p>\n"
				+
				"</li>\n" +
				"<li>??? агрегация по строкам и полям (см [Агрегация на транспонированнои? таблице])</li>\n" +
				"<li>Группировка и скрытие(toggle) строк</li>\n" +
				"<li>DrillDown на SearchSpec</li>\n" +
				"<li> Выделение цветом вместо 3</li>\n" +
				"</ol>\n" +
				" \n" +
				"<h3 style=\"z-index:auto;\">Ненумерованный список</h3>\n" +
				" \n" +
				"<ul>\n" +
				"<li>элемент 1</li>\n" +
				"<li>элемент 2</li>\n" +
				"</ul>\n" +
				" \n" +
				"<blockquote>\n" +
				"<p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diem nonummy nibh euismod tincidunt ut lacreet dolore magna aliguam erat volutpat. Ut wisis enim ad minim veniam, quis nostrud exerci tution ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>\n"
				+
				"</blockquote>";

	}

}
