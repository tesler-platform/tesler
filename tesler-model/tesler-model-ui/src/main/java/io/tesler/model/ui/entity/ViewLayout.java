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
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * View grids by user
 */
@Entity
@Table(name = "layouts_views")
@Getter
@Setter
@AllArgsConstructor
public class ViewLayout extends BaseEntity {

	@OneToMany
	@JoinColumn(name = "layout_id", nullable = false, insertable = false, updatable = false)
	Set<WidgetLayout> widgets;

	Integer columns;

	Integer rowHeight;

	@Column(name = "view_name")
	private String viewName;

	@Column(name = "user_id")
	private Long userId;

	public ViewLayout() {
		this.columns = 12;
		this.rowHeight = 8;
	}

}
