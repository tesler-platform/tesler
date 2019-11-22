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
import static io.tesler.api.data.dictionary.DictionaryType.TASK_PRIORITY;
import static io.tesler.api.data.dictionary.DictionaryType.TASK_STATUS;
import static io.tesler.api.data.dictionary.DictionaryType.TASK_TYPE;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.TZAware;
import io.tesler.core.dto.Lov;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.SearchParameterType;
import io.tesler.vanilla.entity.VanillaTask;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VanillaTaskDTO extends DataResponseDTO {

	@SearchParameter(name = "supervisedOrg.legalPersonShortName")
	private String legalPersonName;

	private String activityType;

	@SearchParameter(type = SearchParameterType.LOV)
	@Lov(TASK_CATEGORY)
	private String taskCategory;

	@SearchParameter(type = SearchParameterType.LOV)
	@Lov(TASK_TYPE)
	private String taskType;

	@SearchParameter
	private String name;

	@SearchParameter(type = SearchParameterType.LOV)
	@Lov(TASK_PRIORITY)
	private String priority;

	private String job;

	@SearchParameter(type = SearchParameterType.DATE)
	private String reportPeriod;

	@SearchParameter(type = SearchParameterType.DATE)
	private LocalDateTime reportDate;

	private Boolean supervisedMonitor;

	private String result;

	@SearchParameter(type = SearchParameterType.DATE)
	private LocalDateTime planDate;

	private String periodicalType;

	private String executor;

	private String executorName;

	private Long executorId;

	@TZAware
	@SearchParameter(type = SearchParameterType.DATE)
	private LocalDateTime createDate;

	private String fileName;

	private String fileId;

	private Double fund;

	private String taskStatus;

	private Boolean isExecute;

	private Boolean bcDisabledFlg;

	private String priorityBgColor;

	private String nameBgColor;

	private String iconParams;

	private String comboConditionTest;

	private Double moneyInputTest;

	private Long numberInputTest;

	private Double decimalInputTest;

	private Long percentInputTest;

	private String longInputTest;

	private String editor;

	private String icon;

	public VanillaTaskDTO(VanillaTask task) {
		this.id = task.getId().toString();
		this.activityType = task.getActivityType() != null ? task.getActivityType().getKey() : null;
		this.job = task.getJob();
		this.supervisedMonitor = task.getSupervisedMonitor();
		this.result = task.getResult();
		this.reportDate = task.getReportDate();
		this.name = task.getName();
		this.planDate = task.getPlanDate();
		this.executor = task.getExecutor() != null ? task.getExecutor().getFullName() : null;
		this.executorName = task.getExecutor() != null ? task.getExecutor().getFullName() : null;
		this.createDate = task.getCreateDate();
		this.reportPeriod = DictionaryType.REPORT_PERIOD.lookupValue(task.getReportPeriod());
		this.taskType = TASK_TYPE.lookupValue(task.getTaskType());
		this.taskCategory = TASK_CATEGORY.lookupValue(task.getTaskCategory());
		this.priority = TASK_PRIORITY.lookupValue(task.getPriority());
		this.periodicalType = DictionaryType.PERIODICAL_TYPE.lookupValue(task.getPeriodicalType());
		this.fileName = task.getFileEntity() != null ? task.getFileEntity().getFileName() : null;
		this.fileId = task.getFileEntity() != null ? task.getFileEntity().getId().toString() : null;
		this.fund = 123456789012.89;
		this.taskStatus = TASK_STATUS.lookupValue(task.getTaskStatus());
		this.isExecute = task.getIsExecute();
		this.bcDisabledFlg = task.getBcDisabledFlg();
		this.comboConditionTest = task.getComboConditionTest();
		this.moneyInputTest = task.getMoneyInputTest();
		this.numberInputTest = task.getNumberInputTest();
		this.decimalInputTest = task.getDecimalInputTest();
		this.percentInputTest = task.getPercentInputTest();
		this.icon = task.getIcon();
		if (task.getPriority() != null && task.getPriority().getKey() != null) {
			switch (task.getPriority().getKey()) {
				case "LOW":
					this.priorityBgColor = "#6fff4f";
					this.iconParams = "arrow-down green";
					break;
				case "MIDDLE":
					this.priorityBgColor = "#e7ff4f";
					this.iconParams = "arrow-up orange";
					break;
				case "HIGH":
					this.priorityBgColor = "#ff4f4f";
					this.iconParams = "arrow-up red";
					break;
				default:
					break;
			}
		}
		this.nameBgColor = "#9c27b0a8";
		if (task.getSuperVisedOrg() != null) {
			this.setLegalPersonName(task.getSuperVisedOrg().getLegalPersonShortName());
		}
		this.setLongInputTest("vanilla");
		this.editor = "<h1 style=\"z-index:auto;\">Большой заголовок</h1>\n" +
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
				"<p style=\"z-index:auto;\"><del style=\"z-index:auto;\">??? показывать маркеры роста/уменьшения </del></p>\n" +
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
