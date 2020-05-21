/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.core.ui;

import static org.assertj.core.api.Assertions.assertThat;

import io.tesler.core.config.JacksonConfig;
import io.tesler.core.util.SpringBeanUtils;
import io.tesler.model.ui.entity.Widget;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({JacksonConfig.class, SpringBeanUtils.class})
public class WidgetUtilsTest {

	@Test
	void testMultiField() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setFields(IOUtils.toString(
				getClass().getResourceAsStream("MultiField.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(12);
	}

	@Test
	void testPickList() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setFields(IOUtils.toString(
				getClass().getResourceAsStream("PickList.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(5);
	}

	@Test
	void testMultiSource() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setFields(IOUtils.toString(
				getClass().getResourceAsStream("MultiSource.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(3);
	}

	@Test
	void testAssocHierarchy() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setType("AssocListPopup");
		widget.setOptions(IOUtils.toString(
				getClass().getResourceAsStream("AssocHierarchy.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(3);
	}

	@Test
	void testMultiValue() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setFields(IOUtils.toString(
				getClass().getResourceAsStream("MultiValueField.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(4);
	}

	@Test
	void testTitleExraction() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setTitle("${FieldFromTitle}");
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(1);
	}

	@Test
	void FormFieldExraction() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setType("Form");
		widget.setFields(IOUtils.toString(
				getClass().getResourceAsStream("FormFields.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(2);
	}

	@Test
	void testShowConditionExraction() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setShowCondition(IOUtils.toString(
				getClass().getResourceAsStream("ShowCondition.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(1);
		widget.setShowCondition("[]");
		assertThat(WidgetUtils.extractAllFields(widget)).isEmpty();
	}

	@Test
	void testChartExtraction() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setChart(IOUtils.toString(
				getClass().getResourceAsStream("Chart.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(3);
	}

	@Test
	void testPivotExtraction() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setPivotFields(IOUtils.toString(
				getClass().getResourceAsStream("PivotFields.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(5);
	}

	@Test
	void testFieldMetaBaseFields() throws Exception {
		Widget widget = getEmptyWidget();
		widget.setFields(IOUtils.toString(
				getClass().getResourceAsStream("FieldMetaBase.json"),
				Charset.defaultCharset()
		));
		assertThat(WidgetUtils.extractAllFields(widget)).isNotEmpty().hasSize(8);
	}

	private Widget getEmptyWidget() {
		Widget widget = new Widget();
		widget.setType("List");
		widget.setName("widgetName");
		widget.setTitle("");
		widget.setBc("testBc");
		widget.setFields("[]");
		widget.setShowCondition("[]");
		widget.setChart("[]");
		return widget;
	}

}
