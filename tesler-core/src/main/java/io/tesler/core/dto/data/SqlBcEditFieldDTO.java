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

package io.tesler.core.dto.data;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.model.ui.entity.SqlBcEditField;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SqlBcEditFieldDTO extends DataResponseDTO {

	private String edit_string1;

	private String edit_string2;

	private String edit_string3;

	private String edit_string4;

	private String edit_string5;

	private String edit_string6;

	private String edit_string7;

	private String edit_string8;

	private String edit_string9;

	private String edit_string10;

	private String edit_lov1;

	private String edit_lov2;

	private String edit_lov3;

	private String edit_lov4;

	private String edit_lov5;

	private Number edit_number1;

	private Number edit_number2;

	private Number edit_number3;

	private Number edit_number4;

	private Number edit_number5;

	private LocalDateTime edit_date1;

	private LocalDateTime edit_date2;

	private LocalDateTime edit_date3;

	private LocalDateTime edit_date4;

	private LocalDateTime edit_date5;

	public SqlBcEditFieldDTO(SqlBcEditField entity) {
		this.edit_string1 = entity.getEditString1();
		this.edit_string2 = entity.getEditString2();
		this.edit_string3 = entity.getEditString3();
		this.edit_string4 = entity.getEditString4();
		this.edit_string5 = entity.getEditString5();
		this.edit_string6 = entity.getEditString6();
		this.edit_string7 = entity.getEditString7();
		this.edit_string8 = entity.getEditString8();
		this.edit_string9 = entity.getEditString9();
		this.edit_string10 = entity.getEditString10();
		this.edit_number1 = entity.getEditNumber1();
		this.edit_number2 = entity.getEditNumber2();
		this.edit_number3 = entity.getEditNumber3();
		this.edit_number4 = entity.getEditNumber4();
		this.edit_number5 = entity.getEditNumber5();
		this.edit_date1 = entity.getEditDate1();
		this.edit_date2 = entity.getEditDate2();
		this.edit_date3 = entity.getEditDate3();
		this.edit_date4 = entity.getEditDate4();
		this.edit_date5 = entity.getEditDate5();
		this.edit_lov1 = entity.getEditLov1();
		this.edit_lov2 = entity.getEditLov2();
		this.edit_lov3 = entity.getEditLov3();
		this.edit_lov4 = entity.getEditLov4();
		this.edit_lov5 = entity.getEditLov5();
	}

}
