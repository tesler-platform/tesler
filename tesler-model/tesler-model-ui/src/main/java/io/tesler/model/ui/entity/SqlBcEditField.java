/*-
 * #%L
 * IO Tesler - Model UI
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

package io.tesler.model.ui.entity;

import io.tesler.model.core.entity.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "sql_bc_edit_field")
@Accessors(chain = true)
public class SqlBcEditField extends BaseEntity {

	@Column(name = "bc_name", nullable = false)
	private String bcName;

	@Column(name = "parent_id", nullable = false)
	private String parentId;

	@Column(name = "edit_string1")
	private String editString1;

	@Column(name = "edit_string2")
	private String editString2;

	@Column(name = "edit_string3")
	private String editString3;

	@Column(name = "edit_string4")
	private String editString4;

	@Column(name = "edit_string5")
	private String editString5;

	@Column(name = "edit_string6")
	private String editString6;

	@Column(name = "edit_string7")
	private String editString7;

	@Column(name = "edit_string8")
	private String editString8;

	@Column(name = "edit_string9")
	private String editString9;

	@Column(name = "edit_string10")
	private String editString10;

	@Column(name = "edit_number1")
	private Integer editNumber1;

	@Column(name = "edit_number2")
	private Integer editNumber2;

	@Column(name = "edit_number3")
	private Integer editNumber3;

	@Column(name = "edit_number4")
	private Integer editNumber4;

	@Column(name = "edit_number5")
	private Integer editNumber5;

	@Column(name = "edit_date1")
	private LocalDateTime editDate1;

	@Column(name = "edit_date2")
	private LocalDateTime editDate2;

	@Column(name = "edit_date3")
	private LocalDateTime editDate3;

	@Column(name = "edit_date4")
	private LocalDateTime editDate4;

	@Column(name = "edit_date5")
	private LocalDateTime editDate5;

	@Column(name = "edit_lov1")
	private String editLov1;

	@Column(name = "edit_lov2")
	private String editLov2;

	@Column(name = "edit_lov3")
	private String editLov3;

	@Column(name = "edit_lov4")
	private String editLov4;

	@Column(name = "edit_lov5")
	private String editLov5;

}
