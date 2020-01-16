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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "views") // views, а не view, т.к. это служебное слово oracle
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class View extends BaseEntity {

	@Column(unique = true)
	private String name;

	private String template;

	private String title;

	private String url;

	private Boolean customizable;

	private Boolean editable;

	private Boolean ignoreHistory;

	private String options;

	public View() {
		super();
	}

}
